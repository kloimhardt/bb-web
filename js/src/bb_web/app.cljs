(ns bb-web.app
  (:require [ajax.core :as aj]
            [cljs.reader :as edn]
            [clojure.string :as st]
            [goog.dom :as gd]
            [goog.object :as go]
            [reagent.dom :as rd]
            [reagent.ratom :as ra]
            [reagent.core :as rc]
            [sci.core :as sci]))

(defonce state (ra/atom {}))

(defn timestamp [] (.toLocaleString (js/Date.)))

(declare get-code)

(defn interpret [code]
  (let [bindings
        {:bindings {'state state 'println println 'timestamp timestamp
                    'get-code get-code}
         :namespaces {'ajax.core {'GET aj/GET 'POST aj/POST}
                      'reagent.core {'cursor rc/cursor}
                      'clojure.string {'join st/join}
                      'goog.object {'get go/get}
                      'goog.dom {'getElement gd/getElement}
                      'cljs.reader {'read-string edn/read-string}}}]
    (try (sci/eval-string code bindings)
         (catch :default e
           (fn []
             [:div
              [:div>code "Small Clojure Interpreter Error:"]
              [:div>code (.-message e)]])))))

(defn get-code
  ([]
   (let [s "error: function \"get-code\" wants a node id in any case"]
     (.log js/console s)
     (rd/render [:p s] (gd/getElement "cljs-app"))))
  ([app-node-id]
   (let [app-node (gd/getElement app-node-id)
         render-fn (interpret (.-textContent app-node))]
     (rd/render [render-fn] app-node)))
  ([app-node-id handler-id]
   (let [app-node (gd/getElement app-node-id)]
     (aj/GET handler-id
             :handler
             (fn [request]
               (let [render-fn (interpret request)]
                 (rd/render [render-fn] app-node)))
             :error-handler
             (fn [_] (-> [:p (str "Babashka server handler with id \""
                                  handler-id
                                  "\" not responding")]
                         (rd/render app-node)))))))

(defn main []
  (get-code "cljs-app"))
