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
    {1 {:status :running
        :ending (time/plus (time/now) (time/minutes 5))}
     2 {:status :running
        :ending (time/plus (time/now) (time/seconds 150))}
     3 {:status :running
        :ending (time/plus (time/now) (time/seconds 15))}
     4 {:status :stopped}
     5 {:status :running
        :ending (time/plus (time/now) (time/minutes 2))}
     6 {:status :stopped}})))


(defn- get-ending [user-id]
  (let [tomato (get-tomato user-id)]
    (case (tomato :status)
      :running (tomato :ending)
      :synced (get-ending (tomato :synced-to))
      nil)))


(defn secs-left [user-id]
  (when-let [ending (get-ending user-id)]
    (let [times [(get-time) ending]
          sorted-times (sort times)
          parity (if (= times sorted-times) 1 -1)]
      (* (time/in-seconds (apply time/interval sorted-times))
         parity))))


(defn get-tomato [user-id]
  (@tomato-state user-id))


(defn- update-tomato [user-id f & args]
  (swap! tomato-state
         (fn [m]
           (assoc m user-id (apply f (m user-id) args)))))


(defn- start-tomato [user-id]
  (update-tomato user-id
                 assoc
                 :status :running
                 :ending (time/plus (time/now) (time/seconds tomato-length))))


(defn- stop-tomato [user-id]
  (update-tomato user-id
                 #(-> %
                      (assoc :status :stoped)
                      (dissoc :ending))))


(defn- sync-tomatoes [from-user-id to-user-id]
  (update-tomato from-user-id
                 assoc
                 :status :synced
                 :synced-to to-user-id))


(defn- unsync-tomato [user-id]
  (update-tomato user-id
                 #(-> %
                      (assoc :status :running)
                      (dissoc :synced-to))))


(defn- handle-own-tomato-click []
  (let [user-id (get-current-user-id)
        tomato (get-tomato user-id)]
    (if (= (tomato :status) :running)
      (stop-tomato user-id)
      (start-tomato user-id))))


(defn- handle-other-tomato-click [user-id]
  (let [tomato (get-tomato user-id)
        current-user-tomato (get-tomato (get-current-user-id))]
    (when (contains? #{:running :synced} (tomato :status))
      (if (= (current-user-tomato :synced-to) user-id)
        (unsync-tomato (get-current-user-id))
        (sync-tomatoes (get-current-user-id) user-id)))))


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
