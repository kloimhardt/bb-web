(require '[ajax.core :refer [GET]]
         '[bb-web :refer [state]])

(declare second-component)

(defn main-comp []
  [:div
   [:p "Press the following button to increase the counter"]
   [:div
    [:button {:on-click (fn [_] (swap! state update :counter inc))} "Count up"]
    (str " " (or (:counter @state) 0))]
   [second-component]])

(defn server-get [kw]
  (GET (str "/" (name kw))
       :handler (fn [response]
                  (swap! state assoc kw response))))

(defn second-component []
  [:div
   [:p "Fetch data from the server"]
   [:div
    [:button {:on-click (fn [_] (server-get :data))} "Get data"]
    (str " " (:data @state))]])

[main-comp]
