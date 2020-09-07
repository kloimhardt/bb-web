(require '[reagent.ratom :as r]
         '[clojure.string :as string])

(defn get-messages []
  (swap! state assoc :messages [{:name "Markus" :message "This is a new messageiii" :timestamp "2020-09-06T13:47:57.914-00:00"}]))

(defn click [messages]
  (fn [e]
    (log (str messages))))

(defn message-list [messages]
  (println messages)
  [:ul.messages
   (for [{:keys [timestamp message name]} (:messages {})]
     ^{:key timestamp}
     [:li
      [:time timestamp]
      [:p message]
      [:p " - " name]])])

(defn errors-component [errors id]
  (when-let [error (id errors)]
    [:div.notification.is-danger (string/join error)]))

(defn message-form [messages]
  (let [fields (r/atom {})
        errors (r/atom nil)]
    (fn [messages]
      [:div
       [errors-component errors :server-error]
       [:div.field
        [:label.label {:for :name} "Name"]
        [errors-component errors :name]
        [:input.input
         {:type :text
          :name :name
          ;;:on-change #(swap! fields assoc :name (-> % .-target .-value))
          :value (:name @fields)}]]
       [:div.field
        [:label.label {:for :message} "Message"]
        ;;[errors-component errors :message]
        [:textarea.textarea
         {:name :message
          :value (:message @fields)
          :on-change #(swap! fields assoc :message (-> % .-target .-value))}]]
       [:input.button.is-primary
        {:type :submit
         ;;:on-click #(send-message! fields errors messages)
         :value "comment"}]])))

(defn main-comp []
  (let [_ (get-messages)]
    [:div.content>div.columns.is-centered>div.column.is-two-thirds
     [:div.columns>div.column
      [:h3 "Messages"]
      [:button {:on-click (click (:messages @state))} "test"]
      [message-list (:messages @state)]]
     [:div.columns>div.column
      [message-form (:messages @state)]]]))

[main-comp]
