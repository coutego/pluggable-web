(ns pluggable-sample-app.core
  (:require [pluggable-injectable.core :as pic]
            [pluggable-web.spa.core :as spa]
            [pluggable-web.template.core :as template]
            [pluggable-web.routing.core :as routing]
            [pluggable-web.notifications.core :as notifications]
            [pluggable-sample-app.home-plugin.core :as sample-app]))

(defn ^:dev/after-load init []
(pic/push-plugins! [spa/plugin
                    template/plugin
                    routing/plugin
                    notifications/plugin
                    sample-app/plugin]))
