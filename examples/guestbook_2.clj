(ns guestbook-2)

(require '[clojure.edn :as edn]
         '[clojure.java.browse :as browse]
         '[clojure.java.io :as io]
         '[muuntaja.core :as m]
         '[muuntaja.middleware :refer [wrap-format wrap-params]]
         '[org.httpkit.server :as http]
         '[reitit.ring :as ring]
         '[ring.middleware.anti-forgery :refer [wrap-anti-forgery
                                                get-anti-forgery-token]]
         '[ring.middleware.content-type :refer [wrap-content-type]]
         '[ring.middleware.webjars :refer [wrap-webjars]]
         '[ring.middleware.defaults :refer [wrap-defaults site-defaults]]
         '[ring.util.http-response :as response]
         '[selmer.parser :as parser])

(import 'java.time.format.DateTimeFormatter
        'java.time.LocalDateTime)

(parser/set-resource-path!  (clojure.java.io/resource "html"))

(def host "http://localhost")

(def port 8080)

(defn error-page
  "error-details should be a map containing the following keys:
   :status - error status
   :title - error title (optional)
   :message - detailed error message (optional)

   returns a response map with the error page as the body
   and the status specified by the status key"
  [error-details]
  {:status  (:status error-details)
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body  (str "<p> error: " error-details " </p>")})

(defn date [] (LocalDateTime/now))

(def formatter (DateTimeFormatter/ofPattern "yyyy-MM-dd HH:mm:ss"))

(def filename "examples/m.txt")

(defn readfile []
  (if (.exists (io/file filename))
    (edn/read-string (slurp filename))
    {:messages []}))

(defn wrap-csrf [handler]
  (wrap-anti-forgery
    handler
    {:error-response
     (error-page
       {:status 403
        :title "Invalid anti-forgery token"})}))

(def formats-instance
  (m/create m/default-options))

(defn wrap-formats [handler]
  (let [wrapped (-> handler wrap-params (wrap-format formats-instance))]
    (fn [request]
      ;; disable wrap-formats for websockets
      ;; since they're not compatible with this middleware
      ((if (:websocket? request) handler wrapped) request))))

(defn validate-message [params] false) ;; stub

(defn db-save-message! [params]
  (let [m (readfile)
        nm params]
    (->> formatter
         (.format (date))
         (assoc nm :timestamp)
         (update m :messages conj)
         pr-str
         (spit filename))))

(defn save-message! [{:keys [params]}]
  (if-let [errors (validate-message params)]
    (response/bad-request {:errors errors})
    (try
      (db-save-message! params)
      (response/ok {:status :ok})
      (catch Exception e
        (response/internal-server-error
          {:errors {:server-error ["Failed to save message!"]}})))))

(defn get-messages []
  (:messages (readfile)))

(defn message-list [_]
  (response/ok {:messages (vec (get-messages))}))

(defn layout-render
  [request template & [params]]
  (response/content-type
    (response/ok
      (parser/render-file
        template
        (assoc params
               :page template
               :csrf-token (get-anti-forgery-token)
               :bb-web-js (slurp "js/bb_web/bb_web.js"))))
    "text/html; charset=utf-8"))

(defn home-page [request]
  (layout-render
    request
    "home.html"))

(defn home-routes []
  [""
   {:middleware [wrap-csrf
                 wrap-formats]}
   ["/"
    {:get home-page}]
   ["/messages"
    {:get message-list}]
   ["/message"
    {:post save-message!}]
   ["/code"
    {:get (fn [request]
            (-> (slurp "examples/guestbook_2.cljs")
                (response/ok)
                (response/content-type "text/html")))}]])

(def handler
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
           (error-page
             {:status 404, :title "404 - Page not found"}))
         :method-not-allowed
         (constantly
           (error-page
             {:status 405, :title "405 - Not allowed"}))
         :not-acceptable
         (constantly
           (error-page
             {:status 406, :title "406 - Not acceptable"}))}))))

(defn wrap-internal-error [handler]
  (fn [req]
    (try
      (handler req)
      (catch Throwable t
        (println t (.getMessage t))
        (error-page {:status 500
                     :title "Something very bad has happened!"
                     :message "We've dispatched a team of highly trained
                               gnomes to take care of the problem."})))))

(defn wrap-base [handler]
  (-> handler
      (wrap-defaults
        (-> site-defaults
            (assoc-in [:security :anti-forgery] false)))
      wrap-internal-error))

(defn -main [& _args]
  (let [url (str host ":" port "/")]
    (println "serving" url)
    (browse/browse-url url))
  (http/run-server
      (wrap-base
        handler)
      {:port port})
  @(promise))
