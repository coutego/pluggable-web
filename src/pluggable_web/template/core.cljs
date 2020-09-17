(ns pluggable-web.template.core
  (:require
   [com.fulcrologic.guardrails.core :refer [>def >defn ? | =>]]
   [reagent.core :as r]
   [pluggable-web.base.core :as bc]
   [pluggable-web.core :as ap]
   [pluggable.core :as plugins]))

(defn ui-top-row-entry [on-click & children]
  (let [on-click (or on-click (fn []))]
    (vec
     (concat
      [:a.header.item
       {:on-click (fn [e]
                    (.preventDefault e)
                    (on-click))
        :style {:font-size :small}}]
      children))))

(defn- ui-login-top-row-v [user-initials]
  [:div.ui.text.container
   [:div.right.floated.item
    (if-not user-initials
      [:a [:i.ui.user.icon] "Log in"]
      [:a.ui.blue.label user-initials])]])

(defn- ui-login-top-row []
  [ui-login-top-row-v "PAS"])

(defn- ui-top-row [app-icon app-name topbar-center topbar-right on-goto-home-page]
  (vec
   (concat
    [:div.ui.text.menu
     {:style {:position :sticky
              :top 0
              :z-index :1
              :background-color :#f5f2ee
              :border :none
              :box-shadow "0 -1px 10px rgba(0,0,0,0.05), 0 1px 4px rgba(0,0,0,0.1), 0 10px 30px #f3ece8"
              :opacity :93%
              :margin :0}}]
    [[:div.ui.text.container
      [:a.header.item
       {:on-click (fn [e]
                    (.preventDefault e)
                    (when on-goto-home-page (on-goto-home-page)))}
       app-icon
       app-name]]
     topbar-center
     topbar-right])))

(defn ui-page-template [top-row contents]
  [:div.ui.container {:style {:background-color :#fff}}
   top-row
   [:div.ui {:style {:padding :2em
                     :padding-top :0.8em
                     :box-shadow "0 -1px 10px rgba(0,0,0,0.05), 0 1px 4px rgba(0,0,0,0.1), 0 10px 30px #f3ece8"
                     :border-radius "0.2em 0.2em 0 0"}}
    contents]])

(defn debug [& args] (.log js/console (apply str args)))

(defn set-bean [db id val]
  (let [val (if (or (keyword? val) (symbol? val)) val [:= val])]
    (assoc-in db [:beans id] val)))

(defn process-plugin [db plugin]
  ;; (debug "Call to template process-plugins.\nplugin = " (:id plugin)); "\ndb = " db
  (let [{:pluggable-web.template.core/keys
         [app-name app-icon topbar-center topbar-right plugin on-logo-click]} plugin
         main-component (:main-component plugin)]
    (as-> db it
      (if app-name (set-bean it ::app-name app-name) it)
      (if on-logo-click (set-bean it ::on-logo-click on-logo-click) it)
      (if app-icon (set-bean it ::app-icon app-icon) it)
      (if topbar-center (set-bean it ::topbar-center topbar-center) it)
      (if topbar-right (set-bean it ::topbar-right topbar-right) it)
      (if main-component (set-bean it :main-component main-component) it))))

(defn plugin-loader [db plugins]
  (reduce process-plugin db plugins))

(defn default-content [] [:div "Not content..."])

(def plugin
  {:id             ::template
   :loader         plugin-loader
   :beans {:main-component [:ui-page-template default-content]
           ::top-row       [ui-top-row
                            ::app-icon
                            ::app-name
                            ::topbar-center
                            ::topbar-right
                            ::on-logo-click]
           ::app-icon      [:= [:div "*app-icon*"]]
           ::app-name      [:= [:div "*app-name*"]]
           ::topbar-center [:= [:div.ui.text.container
                                [ui-top-row-entry nil [:i.ui.upload.icon] "Upload"]
                                [ui-top-row-entry nil [:i.ui.clock.icon] "Recent"]
                                [ui-top-row-entry
                                 nil
                                 [:i.ui.envelope.icon]
                                 "Notifications"
                                 [:span.ui.label {:style {:font-size :xx-small}} 2]]]]
           ::topbar-right [ui-login-top-row]
           :ui-page-template [ui-page-template ::top-row '?]}
   ::on-logo-click #(println "on-logo-click not defined")})


