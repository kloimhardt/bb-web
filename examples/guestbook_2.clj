(ns guestbook-2)

(require '[clojure.edn :as edn]
         '[clojure.java.browse :as browse]
         '[clojure.java.io :as io]
         '[cognitect.transit :as transit]
         '[org.httpkit.server :as http]
         '[reitit.ring :as ring]
         '[ring.middleware.defaults
           :refer [wrap-defaults api-defaults]]
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

(def handler
  (ring/ring-handler
    (ring/router
      [["/"
        {:get (fn [request]
                (-> html
                    (response/response)
                    (response/header "content-type" "text/html")))}]
       ["/code"
        {:get (fn [request]
                (-> (slurp "examples/guestbook_1.cljs")
                    (response/response)
                    (response/header "content-type" "text/html"))
                )}]


       ])))

(defmethod response/resource-data :resource
  [^java.net.URL url]
  (let [conn (.openConnection url)]
    {:content        (.getInputStream conn)
     :content-length (let [len (.getContentLength conn)] (if-not (pos? len) len))}))

(defn app [{:keys [:request-method :uri] :as req}]
  (case [request-method uri]
    [:get "/"] {:body html
                :status 200}
    [:get "/code"] {:body (slurp (first *command-line-args*))
                    :status 200}
    [:get "/messages"] {:body (pr-str (readfile))
                        :status 200}
    [:post "/message"] {:body (message req)
                        :status 200}))

(defn -main [& _args]
  (let [url (str host ":" port "/")]
    (println "serving" url)
    (browse/browse-url url))

  (http/run-server
    (wrap-defaults
      handler
      (assoc api-defaults :static {:resources "public"
                                   :io-resource-fn io/resource}))
    {:port port})

  @(promise))


