(defn server-get [kw]
  (GET (str url (name kw))
       :handler (fn [response]
                  (swap! state assoc kw response))))

(defn server-code [_]
  (server-get :code))

[:div
 [:button {:on-click server-code} "hot reload"]
 [:div
  [:select
   [:option {:value 1} "run"]
   [:option {:value 2} "push ups"]]
  [:select
   [:option {:value 1} "10"]
   [:option {:value 2} "20"]]]]
