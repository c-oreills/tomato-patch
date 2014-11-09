(ns tomato-patch.views.tomatoes-view
  (:require
   [tomato-patch.stores.tomato-store :refer [tomato-state]]
   [tomato-patch.views.tomato-view :refer [tomato-view]]))


(defn tomatoes-view []
  [:div
   (for [[name tomato] @tomato-state]
     ^{:key name} [tomato-view [name tomato]])])
