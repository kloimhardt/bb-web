(require '[clojure.edn :as edn]
         '[clojure.java.browse :as browse]
         '[clojure.java.io :as io]
         '[cognitect.transit :as transit]
         '[org.httpkit.server :as srv])

(import 'java.time.format.DateTimeFormatter
        'java.time.LocalDateTime
        'java.io.ByteArrayOutputStream)

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

(defn db-save-message! [params]
  (->> formatter
       (.format (date))
       (assoc params :timestamp)
       (update (readfile) :messages conj)
       pr-str
       (spit filename)))

(defn db-get-messages []
  (:messages (readfile)))

(defn home-save-message! [req]
  (let [params (transit/read (transit/reader (:body req) :json))]
    (db-save-message! params)
    "post success!"))

(defn home-message-list [_]
  (let [out (ByteArrayOutputStream. 4096)
        writer (transit/writer out :json)]
    (transit/write writer {:messages (vec (db-get-messages))})
    (.toString out)))

(defn home-page [request] html)

(defn home-routes [{:keys [:request-method :uri] :as req}]
  (case [request-method uri]
    [:get "/"] {:body (home-page req)
                :status 200}
    [:get "/messages"] {:headers {"Content-type" "application/transit+json"}
                        :body (home-message-list req)
                        :status 200}
    [:post "/message"] {:body (home-save-message! req)
                        :status 200}
    [:get "/code"] {:body (slurp (first *command-line-args*))
                    :status 200}))

(defn core-http-server []
  (srv/run-server home-routes {:port port}))

(let [url (str "http://localhost:" port "/")]
  (core-http-server)
  (println "serving" url)
  (browse/browse-url url)
  @(promise))
