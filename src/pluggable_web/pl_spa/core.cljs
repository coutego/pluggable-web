(ns pluggable-web.pl_spa.core
 (:require [injectable.core :as inj]
           [injectable.container :as injcnt]
           [pluggable.core :as plug-core]
           [pluggable-injectable.core :as pwc]
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

(defn render-app! [main-component]
  (let [root-el       (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render main-component root-el)))

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
