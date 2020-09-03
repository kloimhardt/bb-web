(require '[org.httpkit.server :as srv]
         '[clojure.java.browse :as browse])

(def host "http://localhost")

(def port 8080)

(defn url []
  (str host ":" port "/"))

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

(defn app [{:keys [:request-method :uri]}]
  (case [request-method uri]
    [:get "/"] {:body html
                :status 200}
    [:get "/code"] {:body (slurp "client.cljs")
                    :status 200}
    [:get "/data"] {:body "Hello from the server-side"
                    :status 200}))

(srv/run-server app {:port port})

(println "serving" (url))

(browse/browse-url (url))

@(promise)
