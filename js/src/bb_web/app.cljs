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

(declare reload-code)

(def bindings
  {:bindings {'println println}
   :namespaces {'bb-web {'state state 'timestamp timestamp
                         'reload-code reload-code}
                'ajax.core {'GET aj/GET 'POST aj/POST}
                'reagent.core {'cursor rc/cursor}
                'clojure.string {'join st/join}
                'goog.object {'get go/get}
                'goog.dom {'getElement gd/getElement}
                'cljs.reader {'read-string edn/read-string}}})

(defn interpret [code]
  (try (sci/eval-string code bindings)
       (catch :default e
         (let [msg (.-message e)]
           (.log js/console msg)
           [:div
            [:div>code "Small Clojure Interpreter Error:"]
            [:div>code msg]]))))

(defn reload-code [render-node-id handler-id]
  (let [app-node (gd/getElement render-node-id)]
    (aj/GET handler-id
            :handler
            (fn [request]
              (rd/render (interpret request) app-node))
            :error-handler
            (fn [_] (-> [:p (str "Babashka server handler with id \""
                                 handler-id
                                 "\" not responding")]
                        (rd/render app-node))))))

(defn ^:export run [render-node-id code-text]
  (let [render-node (gd/getElement render-node-id)
        code (or code-text (.-textContent render-node))]
    (rd/render (interpret code) render-node)))
