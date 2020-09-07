(defn server-get [kw]
  (GET (str url (name kw))
       :handler (fn [response]
                  (swap! state assoc kw response))))

(defn server-data [_]
  (server-get :data))

(defn increase [_]
  (swap! state update :counter inc))

(defn second-component []
  [:div
   [:p "Fetch data from the server"]
   [:div
    [:button {:on-click server-data} "Get data"]
    (str " " (:data @state))]])

;; :app-text shows "This message is from behind the scenes of bb_web" at bottom.
;; Replace true with false to get rid of it on hot-reload,
;; or delete the entire line (and then browser-reload)
;; remove the hot reload button with the :no-hot-releoad flag

(defn main-comp []
  (let [_ (swap! state assoc :app-text true)
        _ (swap! state assoc :no-hot-reload false)]
    (fn []
      [:div
       [:p "Press the following button to increase the counter"]
       [:div
        [:button {:on-click increase} "Count up"]
        (str " " (or (:counter @state) 0))]
       [second-component]])))

[main-comp]
