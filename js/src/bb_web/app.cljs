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

(declare main-comp)

(defn get-code []
  (let [bindings
        {:bindings {'state state 'println println 'timestamp timestamp}
         :namespaces {'ajax.core {'GET aj/GET 'POST aj/POST}
                      'reagent.core {'cursor rc/cursor}
                      'clojure.string {'join st/join}
                      'goog.object {'get go/get}
                      'cljs.reader {'read-string edn/read-string}}}]
    (aj/GET "/code"
            :handler (fn [response]
                       (let [ev (try
                                  (sci/eval-string response bindings)
                                  (catch :default e
                                    (fn [] [:div>code (.-message e)])))]
                         (rd/render [main-comp ev] (gd/getElement "app"))))
            :error-handler (fn [_]
                             (rd/render [:p "Babashka server not responding"]
                                        (gd/getElement "app"))))))

(defn main-comp [ev]
  [:div
   (when-not (:no-hot-reload @state)
     [:button {:on-click get-code} "hot reload"])
   [ev]
   (when (:app-text @state)
     [:p "This message is from behind the scenes of bb_web"])])

(defn ^:dev/after-load main []
  (get-code))
