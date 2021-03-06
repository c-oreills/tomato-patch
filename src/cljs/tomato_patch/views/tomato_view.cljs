(ns tomato-patch.views.tomato-view
  (:require
   [reagent.core :as reagent]
   [tomato-patch.dispatcher :refer [dispatch-ui-action]]
   [tomato-patch.stores.display-store :refer [set-window-resized set-offsets get-offsets]]
   [tomato-patch.stores.tomato-store :refer [secs-left tomato-state tomato-length]]
   [tomato-patch.stores.user-store :refer [current-user? get-user]]
   [tomato-patch.util :refer [friendly-time dial-path min-max]]))


(defn friendly-secs-left [secs-left]
  (str
   (friendly-time (js/Math.abs secs-left))
   (if (neg? secs-left) " overtime")))


(defn tomato-wrapper-class-name [secs-left user-id]
  (str "tomato-wrapper clickable"
       (if
         (and (neg? secs-left)
              (current-user? user-id))
         " pulse")))


(defn on-click-tomato [user-id]
  (dispatch-ui-action {:type :tomato-click
                       :user-id user-id}))


(defn -tomato-view
  [[user-id tomato]]
  (let [[height-offset width-offset] (get-offsets)
        user (get-user user-id)
        secs-left (secs-left user-id)
        tomato-perc (min-max
                     (- 1 (/ secs-left tomato-length)))]
    [:div.tomato-container
     {:style {:top (+ (:y tomato) height-offset)
              :left (+ (:x tomato) width-offset)}}
     [:div.text-center (user :name)]
     [:div
      {:class (tomato-wrapper-class-name secs-left user-id)
       :on-click (partial on-click-tomato user-id)}
      [:div.tomato.shadow]
      [:div.tomato
       {:style {:-webkit-clip-path
                (dial-path 150 140 tomato-perc)}}]]
     [:div.text-center
      (if (nil? secs-left)
        "Stopped"
        (friendly-secs-left secs-left))]]))


(def tomato-view
  (with-meta -tomato-view
    {:component-did-mount
     set-window-resized ; instantly force rerender with offsets
     :component-did-update
     (fn [this _] (set-offsets (reagent/dom-node this)))}))
