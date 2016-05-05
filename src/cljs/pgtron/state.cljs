(ns pgtron.state
  (:require-macros [cljs.core.async.macros :as m :refer [go alt!]])
  (:require [reagent.core :as reagent :refer [atom]]
            [cljs.core.async :refer [>! <!]]))


(def state (atom {:tabs []}))

(defn redirect [path]
  (set! (.. js/window -location -hash) path))

