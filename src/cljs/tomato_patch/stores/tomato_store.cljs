(ns tomato-patch.stores.tomato-store
  (:require
   [cljs-time.coerce :as time-coerce]
   [reagent.core :refer [atom]]
   [tomato-patch.stores.user-store :refer [current-user?]]
   [tomato-patch.util :refer [map-map dial-path circle-positions]]
   ))


(def tomato-length (* 25 60))

(def tomato-state
  (atom
   {"christy" {:ending (time-coerce/from-string "2014-09-19T22:02:23.663Z")
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
                :secs-left 815}}))


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


(swap! tomato-state position-tomatoes)


(defn countdown []
  (swap! tomato-state map-map (fn [v] (update-in v [:secs-left] dec))))

(js/setInterval countdown 1000)
