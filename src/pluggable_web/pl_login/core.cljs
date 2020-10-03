(ns pluggable-web.pl-login.core
  (:require [pluggable-web.pl-security.core :as sec]
            [pluggable-web.pl-template.core :as template]
            [pluggable-web.pl-routing.core :as routing]
            [pluggable-web.pl-login.pages.main :as page]))

;; (defprotocol IUserInfo
;;   (-user-data [_])) ; :name :surname :initials 

;; (defrecord User [name surname type roles])

;; (def sample-users
;;   [(->User "Guest" "Guest" "Guest User" [])
;;    (->User "Martin" "Murray" "Normal Access " [:user])
;;    (->User "John" "Doe" "Admin" [:user :admin])
;;    (->User "Ada" "Lovelace" "Developer" [:dev :user])])

(defn ui-logged-user [initials-fn on-click]
  [:div.ui.text.container
   {:on-click on-click}
   [:div.right.floated.item
    (if initials-fn
      [:a.ui.blue.label (initials-fn)]
      [:a {:href :#} [:i.ui.user.icon] "Log in"])]])

(defn ui-logging-page []
  [:h1 "Login"])

(defn on-go-login-page [router]
  #(routing/goto router ::login-page))

(defn fn-initials [] "PAS")

(def plugin
  {:id ::login
   ::template/topbar-right ::ui-logged-user
   ::routing/routes [["/login"
                      {:name ::login-page
                       :view ::login-page}]]
   :beans
   {::ui-logged-user [ui-logged-user #'fn-initials ::on-go-login-page]
    ::on-go-login-page [on-go-login-page ::routing/router]
    ::login-page page/ui-login-page}})

