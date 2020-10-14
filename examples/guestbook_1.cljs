;---
; Excerpted from "Web Development with Clojure, Third Edition",
; published by The Pragmatic Bookshelf.
; Copyrights apply to this code. It may not be used to create training material,
; courses, books, articles, and the like. Contact us if you are in doubt.
; We make no guarantees that this code is fit for any purpose.
; Visit http://www.pragmaticprogrammer.com/titles/dswdcloj3 for more book information.
;---
; small changes made for use with https://github.com/kloimhardt/bb-web
; zip archive retrieved from the "Resources" section of
; https://pragprog.com/titles/dswdcloj3/web-development-with-clojure-third-edition/
; file: guestbook-reagent/src/cljs/guestbook/core.cljs

(require '[reagent.core :as r]
         ;; '[reagent.dom :as dom]
         '[ajax.core :refer [GET POST]]
         '[clojure.string :as string]
         ;; '[cljs.pprint :refer [pprint]]
         '[goog.dom :as gd]
         '[goog.object :as go]
         '[bb-web :as bb-web :refer [state]])

;
(defn get-messages [messages]
  (GET "/messages"
       {:headers {"Accept" "application/transit+json"}
        :handler (fn [r] (reset! messages (:messages r)))}))
;

;
(defn message-list [messages]
  (println messages)
  [:ul.messages
   (for [{:keys [timestamp message name]} @messages]
     ^{:key timestamp}
     [:li
      [:time timestamp]
      [:p message]
      [:p " - " name]])])
;

;
(defn send-message! [fields errors messages]
  (POST "/message"
        {;; :format :json
         :headers
         {"Accept" "application/transit+json"
          "x-csrf-token" (go/get (gd/getElement "token") "value")}
         :params @fields
         :handler (fn [_]
                    (do
                      (swap! messages conj (assoc @fields :timestamp (bb-web/timestamp)))
                      (reset! fields nil)
                      (reset! errors nil)))
         :error-handler (fn [e]
                          (do
                            (println (str e))
                            (reset! errors (get-in e [:response :errors]))))}))
;

;
(defn errors-component [errors id]
  (when-let [error (id @errors)]
    [:div.notification.is-danger (string/join error)]))
;

;
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

(defn home []
  (let [messages (r/cursor state [:messages])]
    (get-messages messages)
    (fn []
      [:div.content>div.columns.is-centered>div.column.is-two-thirds
       [:div.columns>div.column
        [:h3 "Messages"]
        [message-list messages]]
       [:div.columns>div.column
        [message-form messages]]])))
;
[home]
