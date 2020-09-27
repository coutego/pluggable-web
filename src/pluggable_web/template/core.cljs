(ns pluggable-web.template.core
  (:require
   [reagent.core :as r]
   [pluggable.core :as plugins]
   [pluggable-injectable.core :as pwc]
   [pluggable-web.spa.core :as spa]))

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

(defn default-content [] [:div "[No content defined...]"])

(def plugin
  {:id         ::template
   :extensions [(pwc/extension-keep-last ::app-icon "Application icon component")
                (pwc/extension-keep-last ::app-name "Application name")
                (pwc/extension-keep-last ::topbar-center "Components on the center of the topbar")
                (pwc/extension-keep-last ::topbar-right "components on the right of the topbar")
                (pwc/extension-keep-last ::on-logo-click
                                         "callback to be called when clicking on the application icon")]
   :beans      {::top-row [ui-top-row
                            ::app-icon
                            ::app-name
                            ::topbar-center
                            ::topbar-right
                            ::on-logo-click]
                ::ui-page-template [ui-page-template ::top-row '?]}
   ::spa/main-component [::ui-page-template [default-content]]
   ::on-logo-click      #(println "on-logo-click not defined")

   ::app-icon [:= [:i.ui.envelope.icon]]
   ::app-name [:= [:div "Demo app"]]

   ::topbar-center [:=
                    [:div.ui.text.container
                     [ui-top-row-entry nil [:i.ui.upload.icon] "Upload"]
                     [ui-top-row-entry nil [:i.ui.clock.icon] "Recent"]
                     [ui-top-row-entry
                      nil
                      [:i.ui.envelope.icon]
                      "Notifications"
                      [:span.ui.label {:style {:font-size :xx-small}} 5]]]]

   ::topbar-right [:= [#'ui-login-top-row]]
   :deps [spa/plugin]})
