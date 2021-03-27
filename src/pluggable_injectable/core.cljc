(ns pluggable-injectable.core
  "Plugin and plugin loader helper functions that implement an extension
   system based on Injectable containers.

   The plugins using this system can declare two entries, :beans and :reusable-beans
   defining Injectable configurations. All the configurations declared on the
   :beans and :reusable elements of the plugins will be merged to build a
   Injectable container.

   The utility functions in this namespace load-plugins and push-plugins!
   will load all the plugins, take the :beans and :reusable-beans definitions
   and build the application from that by building an Injectable container out
   of them.

   The functions load-plugins push-plugins! allow for a complete solution to
   build applications based on Pluggable, with the help of this plugin.

   load-plugins is a plugin loader that takes a list of plugins, adds this plugins
   to the baggining of the list, loads the list normally (using Pluggable loader.
   The resulting 'db' containing the ':beans' and ':reusable-beans' is used to
   build an Injectable container by calling Injectable build-container.

   push-plugins! is similar, but caches the reloadble plugins, offering an
   easy to use solution during development to reload plugins while keeping
   state between reloads.

   The use of load-plugins and push-plugins! offer a framework to build applications
   based on Pluggable and Injectable."

  (:require [injectable.core :as inj]
            [pluggable.core :as plug]))

(defn- debug [& args] (println args))

(defn- beans-handler-impl [key db vals] (assoc db key (apply merge vals)))

(defn- reusable-beans-handler [db vals]
  (beans-handler-impl :reusable-beans db vals))

(defn- beans-handler [db vals]
  (beans-handler-impl :beans db vals))

(def injectable-plugin
  {:id :injectable-plugin
   :extensions
   [{:key :beans
     :doc "Map containing a valid configuration map for Injectable.
           The configuration map may refer to keys that are not defined.
           This is fine, as long as some other plugin defines them."
     :handler beans-handler
     :spec map?}
    {:key :reusable-beans
     :doc "Map containing a valid configuration map for Injectable.
           This is similar to :beans, with the difference that the system
           created by this map is supposed to be cached between restarts
           in development time."
     :handler reusable-beans-handler
     :spec map?}]})

(defn reuse-container-keys
  "Takes a container and returns an injectable configuration which creates this container"
  [container keys]
  (into {} (for [k keys] [k {:constructor [(fn [] (k container))]}])))

(defn load-plugins
  "Loads the plugins and creates a new container, discarding the previous one.
   If reload-all is true, then all the beans will be recreated, even those in the
   loadonce map. Otherwise, those beans will be cached."
  [plugins & [current-container]]
  (let [{:keys [beans reusable-beans]} (plug/load-plugins
                                        (concat [injectable-plugin] plugins))
        reusable-beans (if current-container
                         (merge reusable-beans
                                (reuse-container-keys current-container
                                                      (keys reusable-beans)))
                         reusable-beans)
        all-beans        (merge reusable-beans beans)
        container        (inj/create-container all-beans)]
    container))

(defonce cached-container (atom {}))

(defn push-plugins!
  "Pushes the plugins to the current loaded application. By default,
   reusable-beans (according to the new list of plugins) are not reloaded and
   the current values used as their definition"
  [plugins & [reload-all]]
  (let [new-container (if reload-all
                        (load-plugins plugins)
                        (load-plugins plugins
                                      (if (empty? @cached-container)
                                        nil
                                        @cached-container)))]
    (reset! cached-container new-container)
    new-container))

(defn extension-keep-last
  "Creates an plugin extension that keeps the associated value defined
   by the last plugin and associates is to the bean with the same key
   (or bean-key, if it is defined)"
  [key doc & {:keys [reusable? spec bean-key]}]
  (let [handler (fn [db vals]
                  (assoc-in db
                            [(if reusable? :reusable-beans :beans)
                             key]
                            (last vals)))
        ret     {:key (if bean-key bean-key key)
                 :handler handler
                 :doc doc}
        ret     (if spec (assoc ret :spec spec) ret)]
    ret))
