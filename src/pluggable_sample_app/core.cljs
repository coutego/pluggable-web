(ns pluggable-sample-app.core
  (:require [pluggable-injectable.core :as pic]
            [pluggable-web.pl_spa.core :as spa]
            [pluggable-web.pl-template.core :as template]
            [pluggable-web.pl-routing.core :as routing]
            [pluggable-web.notifications.core :as notifications]
            [pluggable-sample-app.home-plugin.core :as sample-app]))

(defn ^:dev/after-load init []
(pic/push-plugins! [spa/plugin
                    template/plugin
                    routing/plugin
                    notifications/plugin
                    sample-app/plugin]))