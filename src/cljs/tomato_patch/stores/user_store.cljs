(ns tomato-patch.stores.user-store
  (:require
   [reagent.core :refer [atom]]))


(def user-state
  (atom
    {:name "christy"}))


(defn current-user? [name]
  (= (@user-state :name) name))
