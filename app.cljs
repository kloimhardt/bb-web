(defn f1 [_]
   (GET "http://localhost:8000/data"
        :handler (fn [response]
                   (swap! state assoc :data response))))

(defn comp2 []
  [:div
   [:p "hi"]
   [:button {:on-click f1} "get data"]])

[:div
  [:p "Hi There"]
  [:p (str @state)]
  [comp2]
  ]

