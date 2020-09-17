(ns pluggable-web.login.api)

(defprotocol IGuardian
  (current-user [this]
    "Returns the current logged user or nil if the user is not logged in")
  (current-profile [this]
    "Returns the effective current profile for the user"))

(defn logged?
  "Returns true if the current user is authenticated, false otherwiese"
  [this] (nil? (current-user this)))

(defn current-user-roles
  "Returns the roles of the currently logged user or nil if the user has not logged in"
  [this]
  (cond
    (not (logged? this))
    #{}

    (nil? (current-profile this))
    #{}

    :default
    (:roles (current-profile this))))

(defn has-role
  "Returns true if the current user has the given role, false otherwise"
  [this role]
  (not (nil? (filter #(= role %) (current-user-roles this)))))
