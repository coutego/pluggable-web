(ns pluggable-web.spa.core
 (:require [injectable.core :as inj]
           [injectable.container :as injcnt]
           [pluggable.core :as plug-core]
           [reagent.dom :as rdom]))

(defn debug [& args] (comment (.log js/console (apply str args))))

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
      (reset! -loadonce-beans (inj/create-container loadonce-beans))
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
        system    (inj/create-container all-beans)]
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

(defn loader [db plugins]
  (println "=====================================")
  (println "loader: beans = " (->> db :beans keys)))

(defn beans-ext-handler [db vals]
  (assoc db :beans (reduce merge vals)))

(defn loadonce-beans-ext-handler [db vals]
  (assoc db :loadonce-beans (reduce merge vals)))

(def plugin
  {:id ::spa
   :loader loader
   :extensions
   [{:key     :beans
     :handler beans-ext-handler
     :doc     "Defines a contribution to the injectable system configuration (i.e. a
              series of beans) to be used in the application."}

     ;; :spec    ::injcnt/container}
    {:key     :loadonce-beans
     :handler loadonce-beans-ext-handler
     :doc     "Similar to :beans, but defines a series of beans that will not be reloaded during
              hot reloads. This is the place to put any beans that are stateful.
              Please note that the beans on :loadonce-beans are loaded independently of the
              beans in :beans and, therefore, can't depend on any bean not defined in
              :loadonce-beans. On the other hand, beans defined in the :bean key can
              depend on beans defined in :loadonce-beans and will be injected in every hot
              code reload."}]})

     ;; :spec    ::injcnt/container}]})
