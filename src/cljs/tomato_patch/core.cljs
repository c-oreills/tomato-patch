(ns tomato-patch.core
  (:require [clojure.string :as string]
            [cljs-time.core :as time]
            [cljs-time.coerce :as time-coerce]
            [tomato-patch.util :refer [map-map friendly-time dial-path min-max circle-positions spy]]
            [reagent.core :as reagent :refer [atom]]
            ))

(enable-console-print!)

(def tomato-length (* 25 60))

(def app-state
  (atom
    {:user {:name "christy"}
     :tomatoes {"christy" {:ending (time-coerce/from-string "2014-09-19T22:02:23.663Z")
                           :secs-left (* 10 60)}
                "tomato" {:ending (time-coerce/from-string "2014-09-19T22:12:23.663Z")
                          :secs-left 150}
                "pieface" {:ending (time-coerce/from-string "2014-09-19T22:12:23.663Z")
                           :secs-left 15}
                "egg" {:ending (time-coerce/from-string "2014-09-19T22:12:23.663Z")
                       :secs-left 715}
                "cheese" {:ending (time-coerce/from-string "2014-09-19T22:12:23.663Z")
                          :secs-left 515}
                "el burro" {:ending (time-coerce/from-string "2014-09-19T22:12:23.663Z")
                          :secs-left 815}
                }}))


(defn current-user? [name]
  (= (get-in @app-state [:user :name])
     name))


(defn position-tomatoes [state]
  (let [tomatoes (:tomatoes state)
        n (count tomatoes)
        rad (* (min js/window.innerHeight js/window.innerWidth) 0.4)
        tomato-positions (circle-positions (- n 1) rad)
        {other-tomatoes false [user-tomato] true} (group-by (comp current-user? first) tomatoes)]
    (assoc state :tomatoes
      (into {}
            (map (fn [[n t] [x y]] [n (assoc t :x x :y y)])
                 (conj (into (array-map) (sort other-tomatoes)) user-tomato)
                 (map (partial map +)
                      (conj (vec tomato-positions) [0 0])
                      (repeat [(/ js/window.innerWidth 2) (/ js/window.innerHeight 2)])))))))


(swap! app-state position-tomatoes)


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
  (let [{:keys [:height-offset :width-offset]} @app-state
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


(defn window-resize []
  (swap! app-state assoc :resize true))


(def tomato-view
  (with-meta -tomato-view
    {:component-did-mount
     window-resize ; instantly force rerender with offsets
     :component-did-update
     (fn
       [this _]
       (if (contains? @app-state :resize)
         (let [set-offset! (fn [dim node-prop]
                             (swap!
                              app-state assoc dim
                              (->
                               (reagent/dom-node this)
                               (aget node-prop)
                               (/ 2)
                               (-))))]
           (do
             (set-offset! :height-offset "offsetHeight")
             (set-offset! :width-offset "offsetWidth")
             (swap! app-state dissoc :resize)))))}))


(defn tomatoes-view []
  [:div
   (for [[name tomato] (:tomatoes @app-state)]
     ^{:key name} [tomato-view [name tomato]])])


(reagent/render-component
 [tomatoes-view]
 (. js/document (getElementById "app")))


(defn countdown []
  (swap! app-state update-in [:tomatoes]
         map-map (fn [v] (update-in v [:secs-left] dec))))

(js/setInterval countdown 1000)
