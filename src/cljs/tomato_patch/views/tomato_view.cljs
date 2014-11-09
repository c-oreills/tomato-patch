(ns tomato-patch.views.tomato-view
  (:require
   [reagent.core :as reagent]
   [tomato-patch.stores.display-store :refer [set-window-resized set-offsets get-offsets]]
   [tomato-patch.stores.tomato-store :refer [tomato-state tomato-length]]
   [tomato-patch.stores.user-store :refer [current-user?]]
   [tomato-patch.util :refer [friendly-time dial-path min-max]]))


(defn friendly-secs-left [secs-left]
  (str
   (friendly-time (js/Math.abs secs-left))
   (if (neg? secs-left) " overtime")))


(defn tomato-wrapper-class-name [secs-left name]
  (str "tomato-wrapper"
       (if
         (and (neg? secs-left)
              (current-user? name))
         " pulse")))


(defn -tomato-view
  [[name tomato]]
  (let [[height-offset width-offset] (get-offsets)
        secs-left (:secs-left tomato)
        tomato-perc (min-max
                     (- 1 (/ secs-left tomato-length)))]
    [:div.tomato-container
     {:style {:top (+ (:y tomato) height-offset)
              :left (+ (:x tomato) width-offset)}}
     [:div.text-center name]
     [:div
      {:class (tomato-wrapper-class-name secs-left name)}
      [:div.tomato.shadow]
      [:div.tomato
       {:style {:-webkit-clip-path
                (dial-path 150 140 tomato-perc)}}]]
     [:div.text-center
      (friendly-secs-left secs-left)]]))


(def tomato-view
  (with-meta -tomato-view
    {:component-did-mount
     set-window-resized ; instantly force rerender with offsets
     :component-did-update
     (fn [this _] (set-offsets (reagent/dom-node this)))}))
