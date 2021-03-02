(ns pluggable-web.notifications.pages.main
  (:require [pluggable-web.notifications.api :as api]
            [pluggable-web.pl-routing.core :as routing]))

(def icon-color :#07b)
(def style-icon {:style {:color        icon-color
                         :margin-right :1em}})

(defn ui-notif-list-item [n router srv]
  ^{:key n}
  [:tr
   [:td (:id n)]
   [:td
    [:i.icon.file style-icon]
    [:a {:href (routing/href router :pluggable-web.notifications.core/view-notification-page)}
     (:title n)]]
   [:td (:description n)]
   [:td [:a
         {:href :#
          :on-click (fn [e] (.preventDefault e) (api/delete-notification srv (:id n)))}
         [:i.icon.trash style-icon]]]])

(defn ui-notifications-page [router srv]
  [:div
   [:h1 "Notifications"]
   [:div (str "You have " (api/notification-count srv) " notifications:")]
   [:table.ui.striped.table
    [:thead
     [:tr
      [:th "ID"]
      [:th "Title"]
      [:th "Description"]
      [:th ""]]]
    [:tbody
     (map #(ui-notif-list-item % router srv) (api/all-notifications srv))]]])

(defn ui-new-notification-page [router srv]
  [:h1 "New notification"]
  [:div "Not implemented"])

(defn ui-view-notification-page [router srv]
  [:h1 "New notification"]
  [:div "Not XXXXX implemented"])
