(ns pluggable-web.core
  (:require [injectable.core :as su]
            [reagent.dom :as rdom]
            [pluggable.core :as plug-core]))

(defn debug [& args]
  (comment
    (.log js/console (apply str args))))

(defn default-main-component []
  [:div
   [:h1
    {:style {:margin :10px}}
    (str "Hello. This is the Home Page.")]
   [:div
    {:style {:margin      :10px
             :font-weight :bold}}
    "No main panel has been defined."]
   [:div
    {:style {:margin :10px}}
    (str
     "If you are seeing this message, it means you have not declared a :main-component in "
     "any of your plugins. Most likely, the reason is that your plugins have not been loaded.")]])

(defonce -loadonce-beans (atom {}))
(defonce -plugins (atom {}))

(declare remount-root)

(defn load-plugins
  "Function to be called in order to bootstrap the application"
  [plugins-v-or-fn]
  (let [plugins-fn (if (fn? plugins-v-or-fn)
                     plugins-v-or-fn
                     (fn [] plugins-v-or-fn))]
    (reset! -plugins plugins-fn)
    (let [plugins       (plugins-fn)
          conf          (plug-core/load-plugins plugins)
          loadonce-beans (:loadonce-beans conf)]
      (debug "Loading plugins" (map :id plugins))
      (debug "Loadonce beans: " -loadonce-beans)
      (reset! -loadonce-beans (su/create-container loadonce-beans))
      (remount-root))))

(defn- wrap-loadonce-beans [m]
  (into {} (for [[k v] m] [k {:constructor (fn [] v)}])))

(defn- refresh-system [plugins loadonce-beans]
  (let [plugins   (plugins) 
        _         (debug "pluggable-web.core/refresh-system: about to reload all the plugins: " (map :id plugins))
        conf      (plug-core/load-plugins plugins)
        _         (debug "pluggable-web.core/refresh-system: After loading plugins, conf = " conf)
        beans     (:beans conf)
        _         (debug "pluggable-web.core/refresh-system: Beans: " beans)
        loadonce-beans (wrap-loadonce-beans loadonce-beans)
        _         (debug "pluggable-web.core/refresh-system: Load once beans: " loadonce-beans)
        all-beans (merge loadonce-beans 
                         beans)
        system    (su/create-container all-beans)]
    system))

(defn ^:dev/after-load remount-root []
  (debug "Reloading application: call to mount root")
  (let [root-el       (.getElementById js/document "app")
        new-system    (try
                        (refresh-system @-plugins @-loadonce-beans)
                        (catch :default e
                          (debug "*** Error *** "
                                 "in mount-root:\n" e)))
        new-main-comp (or (:main-component new-system) [default-main-component])]
    (when new-main-comp
      (rdom/unmount-component-at-node root-el)
      (rdom/render new-main-comp root-el))))

(defn mount-in-root [c]
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render c root-el)))
