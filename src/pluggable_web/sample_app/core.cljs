(ns pluggable-web.sample-app.core
  (:require [pluggable-web.base.core :as base]
            [pluggable-web.template.core :as template]
            [pluggable-web.spa.core :as app]
            [pluggable-web.routing.core :as routing]
            [pluggable-web.notifications.core :as notifications]
            [pluggable-web.recent.core :as recent]))

(defn init []
  (app/load-plugins [base/plugin
                     app/plugin
                     template/plugin
                     routing/plugin
                     notifications/plugin
                     recent/plugin]))
