(require '[ajax.core :refer [GET]])

(defn server-get [kw]
  (GET (str "/" (name kw))
       :handler (fn [response]
                  (swap! state assoc kw response))))

(defn increase [_]
  )

(defn second-component []
  [:div
   [:p "Fetch data from the server"]
   [:div
    [:button {:on-click (fn [_] (server-get :data))} "Get data"]
    (str " " (:data @state))]])

;; The :app-text flag controls the "This message is from behind the scenes of bb_web"
;; at the bottom of the web page.
;; Replace true with false to get rid of this text on hot-reload,
;; or delete the entire line (and then browser-reload).
;; You can remove the hot reload button with the :no-hot-releoad flag

(defn main-comp []
  (swap! state assoc :app-text true)
  (swap! state assoc :no-hot-reload false)
  (fn []
    [:div
     [:p "Press the following button to increase the counter"]
     [:div
      [:button {:on-click (fn [_] (swap! state update :counter inc))} "Count up"]
      (str " " (or (:counter @state) 0))]
     [second-component]]))

main-comp
