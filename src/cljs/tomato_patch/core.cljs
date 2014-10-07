(ns tomato-patch.core
  (:require [clojure.string :as string]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs-time.core :as time]
            [cljs-time.coerce :as time-coerce]
            [tomato-patch.util :refer [map-map friendly-time dial-path min-max circle-positions spy]]
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
        rad (* (spy (min js/window.innerHeight js/window.innerWidth)) 0.4)
        tomato-positions (circle-positions (- n 1) rad)
        {other-tomatoes false [user-tomato] true} (group-by (comp current-user? first) tomatoes)]
    (assoc state :tomatoes
      (into {}
            (map (fn [[n t] [x y]] [n (assoc t :x x :y y)])
                 (conj (into (array-map) (sort other-tomatoes)) user-tomato)
                 (map (partial map +)
                      (conj (vec tomato-positions) [0 0])
                      (repeat [(/ js/window.innerWidth 2) (/ js/window.innerHeight 2)])
                      ))))))


(swap! app-state position-tomatoes)


(defn tomato-view [[name tomato] _]
  (reify
    om/IRender
    (render
     [_]
     (let [tomato-perc (min-max
                          (- 1
                             (/ (:secs-left tomato)
                                tomato-length)))
           secs-left (:secs-left tomato)]
       (dom/div #js {:className "tomato-container"
                     :style #js {:top (:y tomato)
                                 :left (:x tomato)}}

                (dom/div #js {:className "text-center"}
                         name)
                (dom/div #js {:className (str "tomato-wrapper"
                                              (if (and (neg? secs-left)
                                                       (current-user? name)) " pulse"))}
                         (dom/div #js {:className "tomato shadow"})
                         (dom/div #js {:className "tomato"
                                       :style #js {:-webkit-clip-path
                                                   (dial-path 150 140 tomato-perc)}})
                         )
                (dom/div #js {:className "text-center"}
                         (str
                          (friendly-time (js/Math.abs secs-left))
                          (if (neg? secs-left) " overtime"))))))))

(comment
  (time/in-seconds
   (time/interval
    (time/minus (:ending tomato) (time/minutes 20))
    (:ending tomato))))


(defn tomatoes-view [app owner]
  (reify
    om/IRender
    (render [_]
            (apply dom/div nil
                   (om/build-all tomato-view (:tomatoes app) )))))


(om/root
 tomatoes-view
 app-state
 {:target (. js/document (getElementById "app"))})


(defn countdown []
  (swap! app-state update-in [:tomatoes]
         map-map (fn [v] (update-in v [:secs-left] dec))))

(js/setInterval countdown 1000)
