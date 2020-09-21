(require '[org.httpkit.server :as http]
         '[clojure.java.browse :as browse]
         '[ring.middleware.defaults
           :refer [wrap-defaults api-defaults]])

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
  </head>
  <body><h2>Hello from the server with ring 2</h2>
  </body>
  </html>"))

(defn handler [{:keys [:request-method :uri]}]
  (case [request-method uri]
    [:get "/"] {:body html
                :status 200}))

(http/run-server
  (wrap-defaults
    handler
    (assoc api-defaults :static {:resources "public"
                                 :io-resource-fn io/resource}))
  {:port port})
(def url (str host ":" port "/"))

(println "serving" url)

(browse/browse-url url)

@(promise)
