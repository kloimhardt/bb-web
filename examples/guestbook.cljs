(require '[goog.object :as go]
         '[ajax.core :refer [GET POST]]
         '[cljs.reader :as edn]
         '[clojure.string :as string]
         '[reagent.core :as r])

(defn get-messages [messages]
  (GET "/messages"
       {:handler (fn [response]
                   (reset! messages (:messages (edn/read-string response))))}))

(defn message-list [messages]
  (println messages)
  [:ul.messages
   (for [{:keys [timestamp message name]} @messages]
     ^{:key timestamp}
     [:li
      [:time timestamp]
      [:p message]
      [:p " - " name]])])

(defn send-message! [fields errors messages]
  (POST "/message"
        {:params @fields
         :handler (fn [e] (do
                            (swap! messages conj (assoc @fields :timestamp (timestamp)))
                            (reset! fields nil)
                            (reset! errors nil)))
         :error-handler (fn [e] (do
                                  (println (str e))
                                  (reset! errors (get-in e [:response :errors]))))}))

(defn errors-component [errors id]
  (when-let [error (id @errors)]
    [:div.notification.is-danger (string/join error)]))

(defn message-form [messages]
  (let [fields (r/cursor state [:fields])
        errors (r/cursor state [:errors])]
    (fn [messages]
      [:div
       [errors-component errors :server-error]
       [:div.field
        [:label.label {:for :name} "Name"]
        [errors-component errors :name]
        [:input.input
         {:type :text
          :name :name
          :on-change (fn [e] (swap! fields assoc :name
                                    (-> e (go/get "target") (go/get "value"))))
          :value (:name @fields)}]]
       [:div.field
        [:label.label {:for :message} "Message"]
        [errors-component errors :message]
        [:textarea.textarea
         {:name :message
          :value (:message @fields)
          :on-change (fn [e] (swap! fields assoc :message
                                    (-> e (go/get "target") (go/get "value"))))}]]
       [:input.button.is-primary
        {:type :submit
         :on-click (fn [_] (send-message! fields errors messages))
         :value "comment"}]])))

(defn home [_]
  (let [messages (r/cursor state [:messages])
        _ (get-messages messages)
        _ (println "thestate " @state)]
    (fn [_]
      [:div.content>div.columns.is-centered>div.column.is-two-thirds
       [:div.columns>div.column
        [:h3 "Messages"]
        [message-list messages]]
       [:div.columns>div.column
        [message-form messages]]])))

home
