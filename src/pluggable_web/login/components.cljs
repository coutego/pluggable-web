(ns pluggable-web.login.components
  (:require [pluggable-web.login.core :as c]
            [pluggable-web.login.pages.main :as login]
            [pluggable-web.navigation.core :as nav]
            [clojure.string :as st]))

(defn navbar-logged-user-entry []
  (let [user (c/<logged-user)
        fi (-> user :name first str)
        si (-> user :surname first str)
        user-initials (str fi si)]
    [:div.ui.text.container
     {:on-click (fn [e]
                  (.preventDefault e)
                  (nav/>goto-page login/page))}
     [:div.right.floated.item
      (if (or (not user-initials)
              (= 0 (count (st/trim user-initials))))
        [:a {:href :#} [:i.ui.user.icon] "Log in"]
        [:a.ui.blue.label user-initials])]]))
