(ns pluggable-web.routing.core
  (:require [reagent.core :as r]
            [reitit.frontend :as rf]
            [reitit.frontend.easy :as rfe]
            [pluggable-web.template.core :as template]))

(defn debug [& args]
  (println "==============================================")
  (apply println args)
  (println "=============================================="))

(defprotocol IRouter
  (href [_ route-id])
  (goto [_ route-id])
  (go-home [_]))

(defprotocol IInternalRouter
  (-current-route [_]))

(defn- ui-no-page-defined [router]
  [:h1 "Error: no page has been defined"])

(defn- current-page [router template]
     (let [curr @(-current-route router)
           curr (if-not curr
                   (do
                     (go-home router)
                     @(-current-route router))
                   curr)]
       [template
        [:div
         [:div
          (if curr
            (let [view (:view (:data curr))]
              (view curr))
            [ui-no-page-defined router])]]]))

(defn- ui-home-page []
  [:div
   [:h1 "This is the homepage"]
   [:p (str "No content has been defined. You need to set the app main page"
              " by defining a ::pluggable-web.routing.core/home-page extension")]
   [:p
    [:a {:href (rfe/href ::about-page)} "Go to test about page"]]])

(defn- about-page []
  [:div
   [:h1 "About"]
   [:div "This is the test about page"]
   [:div
    [:a {:href (rfe/href ::home-page)} "Return to home page"]]])

(defn- init! [routes set-route]
  (rfe/start!
   (rf/router routes)
   set-route
   {:use-fragment true})) ;; set to false to enable HistoryAPI

(defonce current-route (r/atom nil))

(defn- init-router [routes]
  (init! routes (fn [m] (reset! current-route m))))

(defn- create-router []
  (reify
    IRouter
    (href [_ route-id] (rfe/href route-id))
    (goto [_ route-id] (rfe/push-state route-id))
    (go-home [this] (goto this ::home-page))

    IInternalRouter
    (-current-route [_] current-route)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Extension handlers
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn- create-bean [kw name props]
  (let [key (keyword (str ::routes "--" kw))
        val [(fn [b] (vector name (assoc props :view b))) kw]]
    {:key key :val val}))

(defn- add-route-to-db [db r] ;; FIXME: this would be simpler if used inner beans
  (let [h     (first r)
        props (second r)
        view  (:view props)
        bean? (keyword? view)
        bean  (if bean? (create-bean view h props))]
    (as-> db it
      (if bean?
        (-> it
            (assoc-in [:beans (:key bean)] (:val bean))
            (update-in [:beans ::routes] #(conj (or % [vector]) (:key bean))))
        (-> it
            (update-in [:beans ::routes] #(conj (or % [vector]) r)))))))

(defn ext-handler-home-page [db vals]
  (add-route-to-db
   db
   ["/home"
    {:name ::home-page
     :view (last vals)}]))

(defn- add-routes-to-db [ db routes]
  (reduce add-route-to-db db routes))

(defn ext-handler-routes [db vals]
  (reduce add-routes-to-db db vals))

(def plugin
  {:id         ::routing
   ;; :loader     #'loader
   :beans      {:main-component [vector #'current-page ::router :ui-page-template]
                ::router        {:constructor [#'create-router]
                                 :mutators    [[#'init-router ::routes]]}
                ::about-page    about-page}
   :extensions [{:key     ::home-page
                 :handler ext-handler-home-page
                 :doc     "Fixes the home page (route) of the application,
                           replacing any previous defined home page"}
                {:key     ::routes
                 :handler ext-handler-routes
                 :doc     "Adds the given list of routes to the routes of the application"}]
   ::routes    [["/about"
                 {:name ::about-page
                  :view ::about-page}]]
   ::home-page #'ui-home-page})
