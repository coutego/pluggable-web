(ns pluggable-web.pl-login.pages.main)

(defn- show-user [u]
  [:li
   [:a
    {:key (:name u)
     :href :#
     :on-click #(js/alert "Not implemented")}
    (:name u)
    " "
    (:surname u)
    (when-let [roles (:roles u)] 
      (str " (roles: "
           (reduce #(str %1 (and %2 (str ", " %2))) 
                   (map str roles))
           ")"))]])

(defn- make-user-list [us]
  [:ul (map #(show-user %) us)])
  ;; (let [items (mapv show-user us)
  ;;       ul (vec (concat [:ul] items))]
  ;;   ul))

(defn button-logout []
  [:div.middle.aligned.column
   {:style {:text-align :center}}
   [:div.ui.blue.button
    {:on-click #(js/alert "Not implemented")}
    "Logout"]])

(def users
  [{:name "John" :surname "Doe" :roles [:admin]}
   {:name "Anthony" :surname "Suriman" :roles [:admin :user]}])

(defn ui-login-page []
  [:<>
   [:h1 "Login / Logout"]
   [:div.ui.placeholder.segment
    [:div.ui.two.column.very.relaxed.stackable.grid
      [button-logout]
     [:div.middle.aligned.column
      [:div "Log in as:"
       [make-user-list users]]]]
    [:div.column
     [:div.ui.vertical.divider "Or"]]]])
