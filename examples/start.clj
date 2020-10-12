(require '[org.httpkit.server :as srv]
         '[clojure.java.browse :as browse])

(def host "http://localhost")

(def port 8081)

(def bb-web-js (slurp "js/bb_web/bb_web.js"))

(defn html [cljs-code]
  (str "
  <!DOCTYPE html>
  <html>
  <head>
  <meta charset=\"UTF-8\">
  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">
  <link rel=\"shortcut icon\" href=\"data:,\">
  <link rel=\"icon\" href=\"data:,\">
  <link rel=\"apple-touch-icon\" href=\"data:,\">
  <script>"
       bb-web-js
       "</script>
  <title>start</title>
  </head>
  <body>
  <div id=\"cljs-app\">"
       cljs-code
       "</div>
  <script>bb_web.app.run(\"cljs-app\")</script>
  </body>
  </html>"))

(defn app [{:keys [:request-method :uri]}]
  (case [request-method uri]
    [:get "/"] {:body (html (slurp (or (first *command-line-args*) "examples/start.cljs")))
                :status 200}
    [:get "/data"] {:body "Hello from the server-side"
                    :status 200}))

(srv/run-server app {:port port})

(def url (str host ":" port "/"))

(println "serving" url)

(browse/browse-url url)

@(promise)
