(ns tomato-patch.stores.display-store
  (:require
   [reagent.core :refer [atom]]))


(def display-state (atom {}))


(defn set-window-resized []
  (swap! display-state assoc :resize true))


(defn set-offsets [dom-node]
  (if (contains? @display-state :resize)
    (let [set-offset! (fn [dim node-prop]
                        (swap!
                         display-state assoc dim
                         (->
                          dom-node
                          (aget node-prop)
                          (/ 2)
                          (-))))]
      (do
        (set-offset! :height-offset "offsetHeight")
        (set-offset! :width-offset "offsetWidth")
        (swap! display-state dissoc :resize)))))


(defn get-offsets []
  [(:height-offset @display-state) (:width-offset @display-state)])
