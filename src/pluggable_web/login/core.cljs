(ns pluggable-web.login.core
  (:require [day8.re-frame.tracing :refer-macros [fn-traced]]
            [re-frame.core :as rf]
            [pluggable.re-frame.macros :refer [defsub defevent]]
            [pluggable-web.login.api :as api]))

(defrecord User [name surname type profiles roles])

(defsub <logged-user)
(defevent >log-in-user [db user]
  (-> db
      (assoc ::logged-user user)
      (assoc ::current-profile (first (:profiles user)))))

(def sample-users
  [(->User "John" "Doe" "PMO" [:contractor] [:contractor :qa4])
   (->User "Martin" "Murray" "PMO" [:contractor] [:contractor :tes])
   (->User "Dimos" "Cailteux" "TAXUD" [:contract-manager] [:contract-manager :taxud-user])
   (->User "Pedro" "Abelleira Seco" "TAXUD" [:project-leader :taxud-user] [:project-leader :taxud-user])])

(def profiles
  [{:contractor [:p "Contractor home page"]}
   {:contract-manager [:p "Contract manager home page"]}
   {:project-leader [:p "Project leader home page"]}
   {:taxud-user [:p "Taxud user home page"]}])

(defn- before-load [db]
  (-> db
      (dissoc ::logged-user)
      (assoc ::possible-users sample-users)))

(defn- loader [db plugin]
  (let [pages (:pages plugin)]))

        ;; home-pages (ma)]))

(def plugin
  {:id ::login
   :before-load #'before-load})


   ;; :loader #'loader
   ;; :extra-profiles (profiles)})
