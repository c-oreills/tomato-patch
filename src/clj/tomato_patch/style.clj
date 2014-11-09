(ns tomato-patch.style
  (:require [garden.def :refer [defstyles defkeyframes]]
            [garden.units :refer [px px- percent em]]))


(defkeyframes pulse
  [:from
   {:opacity 1}]

  [:to
   {:opacity 0}])


(defstyles style
  pulse
  [[:.text-center
    {:text-align "center"}]

   [:.clickable
    {:cursor "pointer"}]


   [:.tomato-container
    {:position "absolute"}]

   [:.tomato-wrapper
    {:position "relative"}]

   [:.tomato
    {:background-image "url(../../resources/images/tomato.svg)"
     :background-size "contain"
     :width (px 150)
     :height (px 140)}]


   [:.shadow
    ^:prefix {:filter "grayscale(100%)"}
    {:position "absolute"
     :top 0
     :right 0}]]

  [:.pulse
   ^:prefix
   {:animation [[pulse "1.5s" :infinite :alternate]]}])
