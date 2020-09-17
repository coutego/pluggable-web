(ns pluggable-web.base.core
  (:require [reagent.dom :as rdom]
            [pluggable.core :as plug-core]
            [pluggable-web.core :as ap]))

(defn default-main-component []
  [:div
   [:h1
    {:style {:margin :10px}}
    (str "Home page from Base Plugin")]
   [:div
    {:style {:margin      :10px
             :font-weight :bold}}
    "This is the default main panel. No application main panel has been defined by any plugin."]
   [:div
    {:style {:margin :10px}}
    (str "If you are seeing this message it means you have not declared a :main-component"
         " in any of your plugins. More likely, the reason is that your plugins have not been loaded.")]])

(defn add-beans-to-conf [conf beans reloadable]
  (update conf (if reloadable
                 ::ap/reloadable-conf
                 ::ap/non-reloadable-conf)
          merge
          beans))

(defn process-plugin-bak [db plugin] ;; FIXME: remove 'bak' and login code below
  (ap/debug "Call to base-plugin process-plugins.\nplugin = " (:id plugin) "\ndb = " db "\n")
  (let [{:keys [beans loadonce-beans main-component]} plugin]
    (as-> db it
      (if beans
        (update it :beans #(merge (or % {}) beans))
        it)
      (if loadonce-beans
        (update it :loadonce-beans #(merge (or % {}) loadonce-beans))
        it)
      (if main-component (update-in it [:beans :main-component] (fn [mc] main-component)) it))))

(defn process-plugin [db plugin]
  (ap/debug "base.core/process-plugin. Call with arguments\n    " db "\n    " plugin "\n")
  (let [ret (process-plugin-bak db plugin)]
    (ap/debug "Returning: \n    " ret "\n")
    ret))

(defn loader [db plugins]
  (ap/debug "Call to base/loader. Plugins:\n    " (map :id plugins) "\nDB:\n    " db)
  (reduce process-plugin db plugins))

(def plugin
  {:id             :base
   :main-component #'default-main-component
   :loader         #'loader})
