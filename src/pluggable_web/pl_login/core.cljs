(ns pluggable-web.pl-login.core
  (:require [pluggable-web.pl-security.core :as sec]
            [pluggable-web.pl-template.core :as template]
            [pluggable-web.pl-routing.core :as routing]
            [pluggable-web.pl-login.pages.main :as page]
            [reagent.core :as r]))

;; (defrecord User [name surname type roles])

;; (def sample-users
;;   [(->User "Guest" "Guest" "Guest User" [])
;;    (->User "Martin" "Murray" "Normal Access " [:user])
;;    (->User "John" "Doe" "Admin" [:user :admin])
;;    (->User "Ada" "Lovelace" "Developer" [:dev :user])])

(defprotocol IUserInfo
  (-user-data [_])) ; :name :surname :initials 

(defprotocol -IUserManager
  (-set-current-user! [_ user-data roles])) 

(defn create-srv-user []
  (let [current-user (r/atom nil)]
    (reify
      IUserInfo
      (-user-data [_]
        (:user-data @current-user))
      -IUserManager
      (-set-current-user! [_ user-data roles]
        (swap! @current-user #(do (assoc % :user-data user-data)
                                  (assoc % :roles roles))))
      sec/IUserRoles
      (-roles [_] (:roles @current-user)))))

(defn ui-logged-user [initials-fn on-click]
  [:div.ui.text.container
   {:on-click on-click}
   [:div.right.floated.item
    (if-let [initials (and initials-fn (initials-fn))]
      [:a.ui.blue.label initials]
      [:a {:href :#} [:i.ui.user.icon] "Log in"])]])

(defn ui-logging-page []
  [:h1 "Login"])

(defn fn-on-go-login-page [router]
  #(routing/goto router ::login-page))

(defn fn-initials [srv-user]
  (fn [x] "FOO"))

  ;; #(or (:initials (-user-data srv-user)) "Login"))

(def plugin
  {:id                     ::login
   ::template/topbar-right ::ui-logged-user
   ::routing/routes        [["/login"
                             {:name ::login-page
                              :view ::login-page}]]
   :beans
   {::ui-logged-user   [ui-logged-user #'fn-initials ::on-go-login-page]
    ::fn-initials      [fn-initials ::srv-user]
    ::on-go-login-page [fn-on-go-login-page ::routing/router]

    ::login-page page/ui-login-page}
   :reusable-beans
   {::srv-user [vector create-srv-user]}})

