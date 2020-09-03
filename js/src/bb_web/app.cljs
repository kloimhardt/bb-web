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
                          (swap! state assoc :code response)))]
    (fn []
      [:div
       (sci/eval-string (:code @state)
                        {:bindings {'state state 'GET GET 'url url}})])))

(defn ^:dev/after-load main []
  (rd/render [main-comp] (gd/getElement "app")))
