(ns tomato-patch.views.tomatoes-view
  (:require
   [tomato-patch.stores.tomato-store :refer [tomato-state]]
   [tomato-patch.views.tomato-view :refer [tomato-view]]))


(defn tomatoes-view []
  [:div
   (for [[user-id tomato] @tomato-state]
     ^{:key user-id} [tomato-view [user-id tomato]])])
