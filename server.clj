(require '[org.httpkit.server :as srv]
         '[clojure.java.browse :as browse]
         '[clojure.string :as s])

(def html
  (str "
  <!DOCTYPE html>
  <html>
  <head>
  <meta charset=\"UTF-8\">
  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">
  <title>Kreuzfahrt</title>
  </head>
  <body>
  <div id=\"app\"></div>
  <script>"

       (s/replace (slurp "js/bb_web/bb_web.js")
                  #"</script>\""
                  "</scr\" + \"ipt>\"")
       "</script>
  </body>
  </html>"))

(defn app [{:keys [:request-method :uri]}]
  (case [request-method uri]
    [:get "/"] {:body html
                :status 200}
    [:get "/code"] {:body (slurp "app.cljs") #_(pr-str f1 comp2 sci-comp)
                    :status 200}
    [:get "/data"] {:body "the data 3"
                    :status 200}))

(srv/run-server app {:port 8000})

(println "8000")
(browse/browse-url "http://localhost:8000")

@(promise)
