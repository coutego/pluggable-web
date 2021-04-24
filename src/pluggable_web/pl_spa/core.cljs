(ns pluggable-web.pl-spa.core
  (:require [injectable.core :as inj]
            [injectable.container :as injcnt]
            [pluggable.core :as plug-core]
            [pluggable-injectable.core :as pwc]
            [reagent.dom :as rdom]
            [reagent.core :as r]))

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

(defn ui-catch-render-error [opts [error error-info]]
  (let [err-renderer (:err-renderer opts)]
    (case err-renderer
      nil
      [:div {:style {:font-weight :800
                     :font-style :italic}}
        "*error*"]

      :debug
      [:pre [:code (pr-str error)]]

      [err-renderer error error-info])))

(defn ui-catch-render-success [& [opts children :as opts&children]]
  (let [[opts children] (if (map? opts)
                          [opts children]
                          [{} opts&children])]
    (into [:<>] children)))

(defn ui-catch [& [opts children :as opts&children]]
  (let [[opts children] (if (map? opts)
                          [opts children]
                          [{} opts&children])
        error-atom      (atom nil)]
    (r/create-class
     {:display-name (or (:boundary-name opts) "ErrBoundary")

      :get-derived-state-from-error
      (fn [error error-info]
        (reset! error-atom [error error-info]))

      :reagent-render
      (fn [& args]
        (if @error-atom
          (ui-catch-render-error opts @error-atom)
          (ui-catch-render-success (first args) (rest args))))})))

(defn render-app! [main-component]
  (let [root-el       (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [ui-catch main-component] root-el)))

(defn main-component-ext-handler [db vals]
  (let [all-vals vals
        last-val (last vals)]
    (assoc-in db [:beans ::main-component] last-val)))

(def plugin
  {:id ::spa

   :beans
   {::app {:constructor [(fn [] "App!")]
           :mutators [[render-app! ::main-component]]}}

   :extensions
   [(pwc/extension-keep-last ::main-component
                             "Sets the main component for the application, overwriting any
                              main component that could have been set by a previous plugin.")]

   ::main-component default-main-component})
