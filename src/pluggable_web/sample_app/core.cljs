(ns pluggable-web.sample-app.core
  (:require [pluggable-web.routing.core :as routing]
            [pluggable-web.template.core :as template]))

(defn on-home-click [router]
  (fn [] (routing/go-home router)))

(def plugin
  {:id    ::sample-app
   :beans {::on-logo-click [on-home-click ::routing/router]}
   ::template/on-logo-click ::on-logo-click})
