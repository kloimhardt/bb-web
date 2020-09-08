(require '[clojure.string :as string]
         '[goog.object :as go]
         '[cljs.reader :as edn])

(defn get-messages []
  (GET "/messages"
       {:handler (fn [response]
                   (swap! state assoc :messages (:messages (edn/read-string response))))}))

(defn message-list [messages]
  [:ul.messages
   (for [{:keys [timestamp message name]} messages]
     ^{:key timestamp}
     [:li
      [:time timestamp]
      [:p message]
      [:p " - " name]])])

(defn send-message! [_]
  (println @state)
  (POST "/message"
        {:params (:fields @state)
         :handler (fn [e] (do
                            (swap! state update :messages conj (assoc (:fields @state) :timestamp (timestamp)))
                            (swap! state assoc :fields nil)
                            (swap! state assoc :errors nil)))
         :error-handler (fn [e] (do
                                  (println (str e))
                                  (swap! state assoc :errors (get-in e [:response :errors]))))}))

(defn errors-component [errors id]
  (when-let [error (id errors)]
    [:div.notification.is-danger (string/join error)]))

(defn message-form [messages]
  [:div
   [errors-component (:errors @state) :server-error]
   [:div.field
    [:label.label {:for :name} "Name"]
    [errors-component (:errors @state) :name]
    [:input.input
     {:type :text
      :name :name
      :on-change (fn [e] (swap! state assoc-in [:fields :name]
                                (-> e (go/get "target") (go/get "value"))))
      :value (get-in @state [:fields :name])}]]
   [:div.field
    [:label.label {:for :message} "Message"]
    [errors-component (:errors state) :message]
    [:textarea.textarea
     {:name :message
      :value (get-in @state [:fields :message])
      :on-change (fn [e] (swap! state assoc-in [:fields :message]
                                (-> e (go/get "target") (go/get "value"))))}]]
   [:input.button.is-primary
    {:type :submit
     :on-click send-message!
     :value "comment"}]])

(defn home []
  (let [_ (get-messages)]
    (fn []
      (let [messages (:messages @state)]
        [:div.content>div.columns.is-centered>div.column.is-two-thirds
         [:div.columns>div.column
          [:h3 "Messages"]
          [message-list messages]]
         [:div.columns>div.column
          [message-form messages]]]))))

[home]
