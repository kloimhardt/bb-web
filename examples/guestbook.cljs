(require '[clojure.string :as string]
         '[goog.object :as go])

(defn get-messages []
  (swap! state assoc :messages [{:name "Markus" :message "This is a new message" :timestamp "2020-09-06T13:47:57.914-00:00"}]))

(defn click [messages]
  (fn [e]
    (println (str messages))))

(defn message-list [messages]
  (println messages)
  [:ul.messages
   (for [{:keys [timestamp message name]} (:messages @state)]
     ^{:key timestamp}
     [:li
      [:time timestamp]
      [:p message]
      [:p " - " name]])])

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
      :value (get-in @state [:fileds :message])
      :on-change (fn [e] (swap! state assoc-in [:fileds :message]
                                (-> e (go/get "target") (go/get "value"))))}]]
   [:input.button.is-primary
    {:type :submit
     ;;:on-click #(send-message! fields errors messages)
     :value "comment"}]])

(defn main-comp []
  (let [_ (get-messages)]
    (fn []
      (let [messages (:messages @state)]
        [:div.content>div.columns.is-centered>div.column.is-two-thirds
         [:div.columns>div.column
          [:h3 "Messages"]
          [:button {:on-click (click messages)} "test"]
          [message-list messages]]
         [:div.columns>div.column
          [message-form messages]]]))))

[main-comp]
