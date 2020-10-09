;; this file is a modified copy of
;; https://github.com/yogthos/graal-web-app-example
;; src/server/core.clj

(ns parinfer-codemirror)

(require
  '[org.httpkit.server :as http]
  '[reitit.ring :as ring]
  '[ring.middleware.defaults
   :refer [wrap-defaults api-defaults]]
  '[ring.util.request :as request]
  '[ring.util.response :as response]
  '[clojure.java.io :as io]
  '[clojure.java.browse :as browse])

(def file-to-edit "examples/hot_reload.cljs")

(def host "http://localhost")

(def port 8084)

(def handler
  (ring/ring-handler
   (ring/router
     [["/"
       {:get (fn [request]
               (-> (slurp "examples/parinfer_codemirror.html")
                   (response/response)
                   (response/header "content-type" "text/html")))}]
      ["/codeget"
       {:get (fn [request]
               (-> (slurp file-to-edit)
                   (response/response)
                   (response/header "content-type" "text/html")))}]
      ["/codepost"
       {:post (fn [request]
                (spit file-to-edit (request/body-string request))
                (-> "status: ok"
                    (response/response)
                    (response/header "content-type" "text/html")))}]])))

(defn -main [& _args]
  (println "🔥 starting on port:" port "🔥")
  (http/run-server
    (wrap-defaults
      handler
      (assoc api-defaults :static {:resources "public"
                                   :io-resource-fn io/resource}))
    {:port port})

  (browse/browse-url (str host ":" port "/"))
  @(promise))
