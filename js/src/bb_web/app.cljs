(ns bb-web.app
  (:require [goog.dom :as gd]
            [reagent.dom :as rd]
            [reagent.ratom :as ra]
            [sci.core :as sci]
            [ajax.core :refer [GET]]))

(defonce state (ra/atom {}))

(def url js/url)

(defn main-comp []
  (let [_ (GET (str url "code")
               :handler (fn [response]
                          (swap! state assoc :code response))
               :error-handler (fn [_]
                                (.log js/console "Babashka server not responding")))]
    (fn []
      [:div
       (sci/eval-string (:code @state)
                        {:bindings {'state state 'GET GET 'url url}})
       (when (or (:app-text @state)  (exists? js/app_text))
         [:p "This message is from behind the scenes of bb_web"])])))

(defn ^:dev/after-load main []
  (rd/render [main-comp] (gd/getElement "app")))
