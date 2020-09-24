(ns pluggable-web.sample-app
  (:require [pluggable-web.core :as pw]
            [pluggable-web.spa.core :as spa]
            [pluggable-web.template.core :as template]
            [pluggable-web.routing.core :as routing]
            [pluggable-web.notifications.core :as notifications]))

(defn ^:dev/after-load init4 []
  (pw/push-plugins! [spa/plugin
                     template/plugin
                     routing/plugin
                     notifications/plugin]))
