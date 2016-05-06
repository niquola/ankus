(ns pgtron.state
  (:require-macros [cljs.core.async.macros :as m :refer [go alt!]])
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.core :as r]
            [cljs.core.async :refer [>! <!]]))


(def state (atom {:tabs {}}))

(defn redirect [path]
  (set! (.. js/window -location -hash) path))

(defn current-tab []
  (when-let [current (:current-tab @state)]
    (r/cursor state [:tabs current])))

