(ns pluggable-sample-app.core
  (:require [pluggable-injectable.core :as pic]
            [pluggable-web.pl-spa.core :as spa]
            [pluggable-web.pl-template.core :as template]
            [pluggable-web.pl-routing.core :as routing]
            [pluggable-web.notifications.core :as notifications]
            [pluggable-web.pl-login.core :as login]
            [pluggable-sample-app.home-plugin.core :as sample-app]))

(defn ^:dev/after-load init []
  (pic/push-plugins! [spa/plugin
                      template/plugin
                      routing/plugin
                      notifications/plugin
                      login/plugin
                      sample-app/plugin]))
