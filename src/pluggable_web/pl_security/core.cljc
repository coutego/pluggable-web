(ns pluggable-web.pl-security.core
  (:require [pluggable-injectable.core :as pwc]))

(defprotocol IUserRoles
  (-roles [_]))

(defn roles [this]
  (-roles this))

(defn has-rol? [this rol]
  (some #(= %1 rol) (roles this)))

(defn has-any-rol? [this roles]
  (some #(has-rol? this %) roles))

(defn has-all-roles? [this roles]
  (reduce #(and %1 (has-rol? this %2)) true roles))

(defrecord NilUserRoles []
  IUserRoles
  (-roles [_] []))

(defn ui-when-rol [srv rol & body]
  (when (has-rol? srv rol) body))

(defn ui-when-any-rol [srv roles & body]
  (when (has-any-rol? srv roles) body))

(defn ui-when-all-roles [srv roles & body]
  (when (has-all-roles? srv roles) body))

(defn ui-require-rol [srv rol content error]
  (if (has-rol? srv rol) content error))

(defn ui-require-any-rol [srv roles content error]
  (if (has-any-rol? srv roles) content error))

(defn ui-require-all-roles [srv roles content error]
  (if (has-all-roles? srv roles) content error))

(def plugin
  {:id ::security

   :beans
   [{::srv-user-roles       (->NilUserRoles)
     ::ui-when-rol          [partial ui-when-rol          ::srv-user-roles]
     ::ui-when-any-rol      [partial ui-when-any-rol      ::srv-user-roles]
     ::ui-when-all-roles    [partial ui-when-all-roles    ::srv-user-roles]
     ::ui-require-rol       [partial ui-require-rol       ::srv-user-roles]
     ::ui-require-any-rol   [partial ui-require-any-rol   ::srv-user-roles]
     ::ui-require-all-roles [partial ui-require-all-roles ::srv-user-roles]}]

   :extensions
   [(pwc/extension-keep-last
     ::srv-user-roles
     "Implementation of the IUserRoles protocol to be used by the application.
      Defining this extension overwrites whatever implementation set by previous plugins")]})
