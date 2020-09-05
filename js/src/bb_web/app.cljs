(ns bb-web.app
  (:require [goog.dom :as gd]
            [reagent.dom :as rd]
            [reagent.ratom :as ra]
            [sci.core :as sci]
            [ajax.core :refer [GET]]))

(defonce state (ra/atom {}))

(def url js/url)

(defn server-get [kw]
  (GET (str url (name kw))
       :handler (fn [response]
                  (swap! state assoc kw response))))

(defn server-code [_]
  (server-get :code))

(defn main-comp []
  (let [_ (GET (str url "code")
               :handler (fn [response]
                          (swap! state assoc :code response))
               :error-handler (fn [_]
                                (.log js/console "Babashka server not responding")))]
    (fn []
      [:div
       (try
         (sci/eval-string (:code @state)
                          {:bindings {'state state 'GET GET 'url url}})
         (catch :default e
           [:div
            [:button {:on-click server-code} "hot reload"]
            [:div>code (.-message e)]]))
       (when (or (:app-text @state)  (exists? js/app_text))
         [:p "This message is from behind the scenes of bb_web"])])))

(defn ^:dev/after-load main []
  (rd/render [main-comp] (gd/getElement "app")))
