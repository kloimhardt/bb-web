;; this file is a modified copy of
;; https://github.com/yogthos/graal-web-app-example
;; src/server/core.clj

(ns yogthos-graal-web-app-example)

(require
  '[org.httpkit.server :as http]
  '[reitit.ring :as ring]
  '[ring.middleware.defaults
   :refer [wrap-defaults api-defaults]]
  '[ring.util.response :as response])

(def port 8080)

(def handler
  (ring/ring-handler
   (ring/router
    [["/"
      {:get (fn [request]
              (->
                "<!DOCTYPE html>
                <html><head><link href=\"screen.css\" rel=\"stylesheet\" type=\"text/css\"></head><div class=\"content\"><h2>Hello from the server</h2></div></html>"
                  (response/response)
                  (response/header "content-type" "text/html")))}]])))

(defmethod response/resource-data :resource
  [^java.net.URL url]
  (let [conn (.openConnection url)]
    {:content        (.getInputStream conn)
     :content-length (let [len (.getContentLength conn)] (if-not (pos? len) len))}))

(defn -main [& _args]
  (println "🔥 starting on port:" port "🔥")
  (http/run-server
    (wrap-defaults
      handler
      (assoc api-defaults :static {:resources "public"}))
    {:port port})

  @(promise))
