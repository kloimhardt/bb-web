(defn server-get [kw]
  (GET (str url (name kw))
       :handler (fn [response]
                  (swap! state assoc kw response))))

(defn server-code [_]
  (server-get :code))

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

;; Shows "This message is from behind the scenes of bb_web" at bottom.
;; Replace true with false to get rid of it with hot-reload,
;; or delete the entire line (and browser-reload)
(swap! state assoc :app-text true)

[:div
 [:button {:on-click server-code} "hot reload"]
 [:p "Press the following button to increase the counter"]
 [:div
  [:button {:on-click increase} "Click me"]
  (str " " (:counter @state))]
 [second-component]]
