(defn server-get [kw]
  (GET (str "http://localhost:8000/" (name kw))
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

[:div
 [:button {:on-click server-code} "hot reload"]
 [:p "Press the following button to increase the counter"]
 [:div
  [:button {:on-click increase} "Click me"]
  (str " " (:counter @state))]
 [second-component]
 ]
