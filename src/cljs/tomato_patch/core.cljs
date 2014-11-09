(ns tomato-patch.core
  (:require
   [reagent.core :as reagent]
   [tomato-patch.views.tomatoes-view :refer [tomatoes-view]]))


(enable-console-print!)


(reagent/render-component
 [tomatoes-view]
 (. js/document (getElementById "app")))
