(ns tomato-patch.stores.tick-store
  (:require
   [cljs-time.core :as time]
   [cljs-time.coerce :as time-coerce]
   [reagent.core :refer [atom]]))


(def tick-store (atom (time/now)))


(defn get-time [] @tick-store)


(defn tick []
  (reset! tick-store (time/now)))


(js/setInterval tick 1000)
