(ns tomato-patch.stores.tomato-store
  (:require
   [cljs-time.core :as time]
   [reagent.core :refer [atom]]
   [tomato-patch.dispatcher :refer [register-callback]]
   [tomato-patch.stores.tick-store :refer [get-time]]
   [tomato-patch.stores.user-store :refer [current-user? get-current-user-id]]
   [tomato-patch.util :refer [map-map dial-path circle-positions]]))


(def tomato-length (* 25 60))


(defn position-tomatoes [tomatoes]
  (let [n (count tomatoes)
        rad (* (min js/window.innerHeight js/window.innerWidth) 0.4)
        tomato-positions (circle-positions (- n 1) rad)
        {other-tomatoes false [user-tomato] true} (group-by (comp current-user? first) tomatoes)]
    (into {}
          (map (fn [[n t] [x y]] [n (assoc t :x x :y y)])
               (conj (into (array-map) (sort other-tomatoes)) user-tomato)
               (map (partial map +)
                    (conj (vec tomato-positions) [0 0])
                    (repeat [(/ js/window.innerWidth 2) (/ js/window.innerHeight 2)]))))))


(def tomato-state
  (atom
   (position-tomatoes
    {1 {:ending (time/plus (time/now) (time/minutes 10))}
     2 {:ending (time/plus (time/now) (time/seconds 150))}
     3 {:ending (time/plus (time/now) (time/seconds 15))}
     4 {:ending (time/plus (time/now) (time/minutes 5))}
     5 {:ending (time/plus (time/now) (time/minutes 2))}
     6 {:ending (time/plus (time/now) (time/minutes 8))}})))


(defn secs-left [tomato]
  (let [times [(get-time) (tomato :ending)]
        sorted-times (sort times)
        parity (if (= times sorted-times) 1 -1)]
    (* parity (time/in-seconds (apply time/interval sorted-times)))))


(defn- handle-own-tomato-click []
  (js/console.log "click own" (get-current-user-id)))


(defn- handle-other-tomato-click [user-id]
  (js/console.log "click other" user-id))


(defn- handle-tomato-click [user-id]
  (if (current-user? user-id)
    (handle-own-tomato-click)
    (handle-other-tomato-click user-id)))


(def token
  (register-callback
   (fn [payload]
     (case (payload :type)
       :tomato-click (handle-tomato-click (payload :user-id))
       nil))))
