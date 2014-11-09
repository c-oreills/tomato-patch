(ns tomato-patch.util
  (:require [clojure.string :as string]
            [goog.string :refer [format]]
            ))


(defn spy [v]
  (js/console.log v)
  v)


(defn friendly-time [secs]
  (let [mins (int (/ secs 60))
        secs (mod secs 60)]
    (string/join ":"
                 [(str mins)
                  (format "%02d" secs)])))

; TODO: cache
(defn get-corner-perc [x y]
  (/ (js/Math.atan2 y x) (* 2 js/Math.PI)))

; TODO: cache
(defn get-rad [x y]
  (js/Math.sqrt (+ (js/Math.pow x 2) (js/Math.pow y 2))))


(defn poly-point [x y]
  (str x "px " y "px"))

(defn dial-path [x y perc]
  (let [perc-theta (* 2 js/Math.PI perc)
        corner-perc (get-corner-perc x y)
        half-x (/ x 2)
        half-y (/ y 2)
        rad (get-rad half-x half-y)]
    (str "polygon("
         (string/join ","
                      (filter (complement nil?)
                              [(poly-point half-x half-y)
                               (poly-point half-x 0)
                               (if (> perc corner-perc) (poly-point x 0))
                               (if (> perc (- 0.5 corner-perc)) (poly-point x y))
                               (if (> perc (+ 0.5 corner-perc)) (poly-point 0 y))
                               (if (> perc (- 1 corner-perc)) (poly-point 0 0))
                               (poly-point (+ half-x (* rad (js/Math.sin perc-theta)))
                                           (- half-y (* rad (js/Math.cos perc-theta))))
                               ]))
         ")")))


(defn circle-offsets [n]
  (condp = n
    0 []
    1 [(/ js/Math.PI 2)]
    (let [rad-space (/ (* 2 js/Math.PI) n)]
      (for [i (range n)] (* rad-space (+ i (/ 1 2)))))))


(defn circle-positions [n rad]
  (let [offsets (circle-offsets n)]
    (for [offset offsets]
      [(* rad (js/Math.sin offset))
       (* -1 rad (js/Math.cos offset))])))


(defn min-max
  "Coerce a number n so that min_ <= n <= max_"
  ([n min_ max_] (max min_ (min max_ n)))
  ([n] (min-max n 0 1)))
