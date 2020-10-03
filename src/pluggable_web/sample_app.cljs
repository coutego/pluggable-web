(ns pluggable-web.sample-app
  "Sample app for pluggable web (under construction)"
  (:require [pluggable-injectable.core :as pic]
            [pluggable-web.pl_spa.core :as spa]
            [pluggable-web.pl-template.core :as template]
            [pluggable-web.pl-routing.core :as routing]
            [pluggable-web.notifications.core :as notifications]
            [pluggable-web.pl-sample-app.core :as sample-app]))

(defn ^:dev/after-load init []
  (pic/push-plugins! [spa/plugin
                      template/plugin
                      routing/plugin
                      notifications/plugin
                      sample-app/plugin]))
