(require '[clojure.edn :as edn]
         '[clojure.java.browse :as browse]
         '[clojure.java.io :as io]
         '[cognitect.transit :as transit]
         '[org.httpkit.server :as srv])

(import 'java.time.format.DateTimeFormatter
        'java.time.LocalDateTime)

(def host "http://localhost")

(def port 8080)

(defn url []
  (str host ":" port "/"))

(def filename "examples/m.txt")

(def html
  (str "
  <!DOCTYPE html>
  <html>
  <head>
  <meta charset=\"UTF-8\">
  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">
  <link rel=\"icon\" href=\"data:,\">
  <title>bb-web</title>
  </head>
  <body>
  <div id=\"app\"></div>
  <script>"
       (str "const url=\"" (url) "\";")
       (slurp "js/bb_web/bb_web.js")
       "</script>
  </body>
  </html>"))

(defn date [] (LocalDateTime/now))

(def formatter (DateTimeFormatter/ofPattern "yyyy-MM-dd HH:mm:ss"))

(defn readfile []
  (when (.exists (io/file filename))
    (edn/read-string (slurp filename))))

(defn messages-answer [req]
  (readfile))

(defn message-answer [req]
  (let [m (readfile)
        nm (transit/read (transit/reader (:body req) :json))]
    (spit filename (pr-str (update m
                                   :messages
                                   conj
                                   (assoc nm :timestamp (.format (date) formatter))))))
  "server post")

(defn app [{:keys [:request-method :uri] :as req}]
  (case [request-method uri]
    [:get "/"] {:body html
                :status 200}
    [:get "/code"] {:body (slurp (or (first *command-line-args*) "client.cljs"))
                    :status 200}
    [:get "/messages"] {:body (pr-str (messages-answer req))
                        :status 200}
    [:post "/message"] {:body (message-answer req)
                        :status 200}))

(srv/run-server app {:port port})

(println "serving" (url))

(browse/browse-url (url))

@(promise)
