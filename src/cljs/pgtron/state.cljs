(ns pgtron.state
  (:require [reagent.core :as reagent :refer [atom]]))

(defonce state (atom {}))

(defn redirect [path]
  (set! (.. js/window -location -hash) path))
