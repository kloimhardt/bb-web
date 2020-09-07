(ns bb-web.app
  (:require [ajax.core :refer [GET POST]]
            [clojure.string :refer [join]]
            [goog.dom :as gd]
            [goog.object :as go]
            [reagent.dom :as rd]
            [reagent.ratom :as ra]
            [sci.core :as sci]))

(defonce state (ra/atom {}))

(def url js/url)

(defn log [s]
  (.log js/console (str s)))

(declare main-comp)

(defn get-code [_]
  (GET (str url "code")
       :handler (fn [response]
                  (let [ev (try
                             (sci/eval-string response
                                              {:bindings {'state state 'GET GET 'POST POST
                                                          'url url 'println println
                                                          'log log}
                                               :namespaces {'reagent.ratom {'atom atom}
                                                            'clojure.string {'join join}
                                                            'goog.object {'get go/get}}})
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
   ev
   (when (or (:app-text @state) (exists? js/app_text))
     [:p "This message is from behind the scenes of bb_web"])])

(defn ^:dev/after-load main []
  (get-code nil))
