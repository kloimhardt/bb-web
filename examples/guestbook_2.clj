(ns guestbook-2)

(require '[clojure.edn :as edn]
         '[clojure.java.browse :as browse]
         '[clojure.java.io :as io]
         '[cognitect.transit :as transit]
         '[org.httpkit.server :as http]
         '[reitit.ring :as ring]
         '[ring.middleware.content-type :refer [wrap-content-type]]
         '[ring.middleware.webjars :refer [wrap-webjars]]
         '[ring.middleware.defaults
           :refer [wrap-defaults api-defaults site-defaults]]
         '[ring.middleware.reload :refer [wrap-reload]]
         '[ring.util.response :as response])

(import 'java.time.format.DateTimeFormatter
        'java.time.LocalDateTime)

(def host "http://localhost")

(def port 8080)

(def html
  (str "
  <!DOCTYPE html>
  <html>
  <head>
  <meta charset=\"UTF-8\">
  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">
  <link rel=\"icon\" href=\"data:,\">
  <title>bb-web</title>
  <link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/bulma@0.9.0/css/bulma.min.css\">
  </head>
  <body>
  <div id=\"app\"></div>
  <script>"
       (slurp "js/bb_web/bb_web.js")
       "</script>
  </body>
  </html>"))

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

(defn message [req]
  (let [m (readfile)
        nm (transit/read (transit/reader (:body req) :json))]
    (->> formatter
         (.format (date))
         (assoc nm :timestamp)
         (update m :messages conj)
         pr-str
         (spit filename)))
  "post success!")

(defn home-routes []
  [["/"
    {:get (fn [request]
            (-> html
                (response/response)
                (response/header "content-type" "text/html")))}]
   ["/code"
    {:get (fn [request]
            (-> (slurp "examples/guestbook_1.cljs")
                (response/response)
                (response/header "content-type" "text/html")))}]
   ["/messages"
    {:get (fn [request]
            (-> (pr-str (readfile))
                (response/response)
                (response/header "content-type" "text/html")))}]
   ["/message"
    {:post (fn [request]
             (-> (message request)
                 (response/response)
                 (response/header "content-type" "text/html")))}]])

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
             {:status 406, :title "406 - Not acceptable"}))}))
    ))

(defn wrap-dev [handler]
  (-> handler
      wrap-reload)) ;;TODO: wrap-relaod blows up the GraalVM-binary

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
  (-> (wrap-dev handler)
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


