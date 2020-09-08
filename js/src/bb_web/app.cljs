(ns bb-web.app
  (:require [ajax.core :as aj]
            [cljs.reader :as edn]
            [clojure.string :refer [join]]
            [goog.dom :as gd]
            [goog.object :as go]
            [reagent.dom :as rd]
            [reagent.ratom :as ra]
            [reagent.core :as rc]
            [sci.core :as sci]))

(defonce state (ra/atom {}))

(def url js/url)

(defn timestamp [] (.toLocaleString (js/Date.)))

(declare main-comp)

(defn get-code [_]
  (aj/GET (str url "code")
       :handler (fn [response]
                  (let [ev (try
                             (sci/eval-string response
                                              {:bindings {'state state
                                                          'url url 'println println
                                                          'timestamp timestamp}
                                               :namespaces {'ajax.core {'GET aj/GET 'POST aj/POST}
                                                            'reagent.core {'cursor rc/cursor}
                                                            'clojure.string {'join join}
                                                            'goog.object {'get go/get}
                                                            'cljs.reader {'read-string edn/read-string}}})
                             (catch :default e
                               [:div>code (.-message e)]))]
                      (rd/render [main-comp ev] (gd/getElement "app"))))
       :error-handler (fn [_]
                        (.log js/console "Babashka server not responding")
                        (rd/render [main-comp nil] (gd/getElement "app")))))

(defn main-comp [ev]
  [:div
   (when-not (:no-hot-reload @state)
     [:button {:on-click get-code} "hot reload"])
   [ev state]
   (when (or (:app-text @state) (exists? js/app_text))
     [:p "This message is from behind the scenes of bb_web"])])

(defn ^:dev/after-load main []
  (get-code nil))
