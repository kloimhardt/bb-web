(require '[clojure.edn :as edn]
         '[clojure.java.browse :as browse]
         '[clojure.java.io :as io]
         '[muuntaja.core :as m]
         '[muuntaja.middleware :refer [wrap-format wrap-params]]
         '[org.httpkit.server :as http]
         '[reitit.ring :as ring]
         '[ring.middleware.anti-forgery :refer
           [wrap-anti-forgery get-anti-forgery-token]]
         '[ring.middleware.content-type :refer [wrap-content-type]]
         '[ring.middleware.webjars :refer [wrap-webjars]]
         '[ring.middleware.defaults :refer [wrap-defaults site-defaults]]
         '[ring.util.http-response :as response]
         '[selmer.parser :as parser])

(import 'java.time.format.DateTimeFormatter
        'java.time.LocalDateTime)

(def port 8085)

(defn date [] (LocalDateTime/now))

(def formatter (DateTimeFormatter/ofPattern "yyyy-MM-dd HH:mm:ss"))

(def filename "examples/m.txt")

(defn readfile []
  (if (.exists (io/file filename))
    (edn/read-string (slurp filename))
    {:messages []}))

(defn db-save-message! [params]
  (->> formatter
       (.format (date))
       (assoc params :timestamp)
       (update (readfile) :messages conj)
       pr-str
       (spit filename)))

(defn home-validate-message [params] false) ;; stub

(defn db-get-messages []
  (:messages (readfile)))

(def cmd-line-args *command-line-args*)

(defn layout-render
  [request template & [params]]
  (response/content-type
    (response/ok
      (parser/render
        (slurp template)
        (assoc params
               :page template
               :csrf-token (get-anti-forgery-token)
               :cljs-code (slurp (first cmd-line-args))
               :bb-web-js (slurp "js/bb_web/bb_web.js"))))
    "text/html; charset=utf-8"))

(def formats-instance
  (m/create m/default-options))

(defn layout-error-page
 [error-details]
  {:status  (:status error-details)
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body  (str "<p> error: " error-details " </p>")})

(defn home-save-message! [{:keys [params]}]
  (if-let [errors (home-validate-message params)]
    (response/bad-request {:errors errors})
    (try
      (db-save-message! params)
      (response/ok {:status :ok})
      (catch Exception e
        (response/internal-server-error
          {:errors {:server-error ["Failed to save message!"]}})))))

(defn home-message-list [_]
  (response/ok {:messages (vec (db-get-messages))}))

(defn home-page [request]
  (layout-render
    request
    "examples/guestbook_2.html"))

(defn middleware-wrap-formats [handler]
  (let [wrapped (-> handler wrap-params (wrap-format formats-instance))]
    (fn [request]
      ;; disable wrap-formats for websockets
      ;; since they're not compatible with this middleware
      ((if (:websocket? request) handler wrapped) request))))

(defn middleware-wrap-csrf [handler]
  (wrap-anti-forgery
    handler
    {:error-response
     (layout-error-page
       {:status 403
        :title "Invalid anti-forgery token"})}))

(defn home-routes []
  [""
   {:middleware [middleware-wrap-csrf
                 middleware-wrap-formats]}
   ["/"
    {:get home-page}]
   ["/messages"
    {:get home-message-list}]
   ["/message"
    {:post home-save-message!}]])

(defn middleware-wrap-internal-error [handler]
  (fn [req]
    (try
      (handler req)
      (catch Throwable t
        (println t (.getMessage t))
        (layout-error-page {:status 500
                     :title "Something very bad has happened!"
                     :message "We've dispatched a team of highly trained
                               gnomes to take care of the problem."})))))

(def handler-app-routes
  (ring/ring-handler
    (ring/router
      [(home-routes)])
    (ring/routes
      (ring/create-resource-handler
        {:path "/"})
      (wrap-content-type
        (wrap-webjars (constantly nil)))
      (ring/create-default-handler
        {:not-found
         (constantly
           (layout-error-page
             {:status 404, :title "404 - Page not found"}))
         :method-not-allowed
         (constantly
           (layout-error-page
             {:status 405, :title "405 - Not allowed"}))
         :not-acceptable
         (constantly
           (layout-error-page
             {:status 406, :title "406 - Not acceptable"}))}))))

(defn middleware-wrap-base [handler]
  (-> handler
      (wrap-defaults
        (-> site-defaults
            (assoc-in [:security :anti-forgery] false)))
      middleware-wrap-internal-error))

(defn handler-app []
  (middleware-wrap-base handler-app-routes))

(defn core-http-server []
  (http/run-server (handler-app) {:port port}))

(let [url (str "http://localhost:" port "/")]
  (core-http-server)
  (println "serving" url)
  (browse/browse-url url)
  @(promise))
