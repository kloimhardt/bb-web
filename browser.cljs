(defn server-data [_]
  (GET "http://localhost:8000/data"
       :handler (fn [response]
                  (swap! state assoc :data response))))

(defn increase [_]
  (swap! state update :counter inc))

(defn comp2 []
  [:div
   [:p "Fetch data from the server"]
   [:div
    [:button {:on-click server-data} "Get data"]
    (str " " (:data @state))]])

[:div
 [:p "Press the following button to increase the counter"]
 [:div
  [:button {:on-click increase} "Click me"]
  (str " " (:counter @state))]
 [comp2]
 ]
