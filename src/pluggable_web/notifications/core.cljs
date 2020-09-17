(ns pluggable-web.notifications.core
  (:require [reagent.core :as r]
            [pluggable-web.notifications.pages.main :as notifications]
            [pluggable-web.notifications.api :as api]
            [pluggable-web.template.core :as template]
            [pluggable-web.routing.core :as routing]))

(def plugin
  {:id     ::notifications
   :beans  {::ui-notifications-page [(fn [r n] (fn [] (notifications/ui-notifications-page r n))) ::routing/router ::notification-srv]
            ::ui-new-notification-page [(fn [r n] (fn [] (notifications/ui-new-notification-page r n))) ::routing/router ::notification-srv]
            ::ui-view-notification-page [(fn [r n] (fn [] (notifications/ui-view-notification-page r n))) ::routing/router ::notification-srv]
            ::notification-srv [api/create-notification-srv]}

   ::routing/routes         [["/notifications/all"
                              {:name ::notifications-page
                               :view ::ui-notifications-page}]
                             ["/notifications/new"
                              {:name ::new-notification-page
                               :view ::ui-new-notification-page}]
                             ["/notifications/view"
                              {:name ::view-notification-page
                               :view ::ui-view-notification-page}]]})
