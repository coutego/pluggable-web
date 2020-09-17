(ns pluggable-web.notifications.api
  (:require [reagent.core :as r]))

(defprotocol INotificationSrv
  (all-notifications [_] "Returns the list of all notifications")
  (notification-count [_] "Returns the number of pending notifications")
  (notification-by-id [_ id] "Returns the notification of the given id, nil if not found")
  (delete-notification [_ id]) "Removes the notification of the given id. Silenty no-op if
                                The id doesn't belong to any notification")

(def test-notifications
  (into [{:id          1
          :title       "Document 532 received"
          :description "The document with id 532 was received on 12.03.2020"}

         {:id          2
          :title       "Document 4321 registered"
          :description "The document with id 4321 was registered: REGNUM 311546"}

         {:id          3
          :title       "Message from contract manager for XXX"
          :description "The Report for April was not uploaded yet. Please update it before the end of the week"}

         {:id          4
          :title       "System maintenance"
          :description "The system will be unavailable Friday 19 from 20:00 to 22:00"}]

        (for [i (range 5 15)]
          {:id          i
           :title       (str "Test notification #" i)
           :description "This is a test notification"})))

(defonce notifications (r/atom test-notifications))

(defn create-notification-srv []
  (reify INotificationSrv
    (all-notifications [_] @notifications)

    (notification-count [_] (count @notifications))

    (notification-by-id [_ id]
      (->> @notifications (filter #(= (:id %) id)) first))

    (delete-notification [_ id]
      (swap! notifications
             (fn [nts]
               (remove #(= (:id %) id) nts))))))
