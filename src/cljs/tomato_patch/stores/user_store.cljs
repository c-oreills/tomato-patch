(ns tomato-patch.stores.user-store
  (:require
   [reagent.core :refer [atom]]))


(def current-user-state
  (atom {:id 1}))

(def user-state
  (atom
   {1 {:name "christy"}
    2 {:name "tomato"}
    3 {:name "pieface"}
    4 {:name "egg"}
    5 {:name "cheese"}
    6 {:name "el burro"}}))


(defn current-user? [user-id]
  (= (get-current-user-id) user-id))


(defn get-current-user-id []
  (@current-user-state :id))


(defn get-user [user-id]
  (@user-state user-id))
