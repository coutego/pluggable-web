(ns pluggable-web.recent.core
  (:require [pluggable-web.recent.pages.main :as recent]
            [pluggable-web.pl-routing.core :as routing]))

(def plugin
  {:id ::recent
   ::routing/routes [["/recent"
                      {:name ::recent-page
                       :view #'recent/page-comp}]]})
