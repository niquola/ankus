(ns pgtron.demo
  (:require [pgtron.layout :as l]
            [charty.dsl :as v]
            [reagent.core :as r]
            [pgtron.style :refer [style]]))


(defn $index [params]
  (let [state (r/atom {:inc 1 :items []})
        handle (fn []
          (let [cur @state]
            (.log js/console (pr-str cur))
            (reset! state {:inc (rand)
                           :items (take 3 (conj (:items cur) (:inc cur)))})))]
    (fn []
      [l/layout {}
       [:div
        [:button {:on-click handle} "Update"]
        (style [:svg [:circle {:fill "#fff"}]
                [:text {:fill "yellow"}]])
        [:pre (pr-str @state)]
        [:h1 "Hello all"]
        [v/demo {:width 1000 :height 1000} (:items @state)]]])))
