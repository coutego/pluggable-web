(ns pluggable-web.sample-app
  "Sample app for pluggable web (under construction)"
  (:require [pluggable-web.core :as pw]
            [pluggable-web.spa.core :as spa]
            [pluggable-web.template.core :as template]
            [pluggable-web.routing.core :as routing]
            [pluggable-web.notifications.core :as notifications]
            [pluggable-web.sample-app.core :as sample-app]))

(defn ^:dev/after-load init []
  (pw/push-plugins! [spa/plugin
                     template/plugin
                     routing/plugin
                     notifications/plugin
                     sample-app/plugin]))
