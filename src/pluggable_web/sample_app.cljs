(ns pluggable-web.sample-app
  (:require [pluggable-web.core :as pw]
            [pluggable-web.template.core :as template]
            [pluggable-web.spa.core :as app]
            [pluggable-web.routing.core :as routing]
            [pluggable-web.notifications.core :as notifications]))

(defn ^:dev/after-load init4 []
  (pw/push-plugins! [app/plugin
                     template/plugin
                     routing/plugin
                     notifications/plugin]))
