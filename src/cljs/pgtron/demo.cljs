(ns pgtron.demo
  (:require [pgtron.layout :as l]
            [charty.core :as chart]
            [charty.dsl :as dsl]
            [reagent.core :as r]
            [pgtron.style :refer [style]]))


(defn $index [params]
  (let [state (r/atom {:inc 1 :items []})
        handle (fn []
          (let [cur @state]
            (reset! state {:items (reduce (fn [acc i]
                                     (.. acc (push #js{:x i :y (.sin js/Math (+ (rand) (/ i 5)))}))
                                     acc) #js[] (range 100))})))]
    (handle)
    (fn []
      [l/layout {}
       [:div
        [:button {:on-click handle} "Update"]
        (style [:svg [:circle {:fill "#fff"}]
                [:text {:fill "yellow"}]])
        [:pre (pr-str @state)]
        [:h1 "Chart"]
        [chart/area-chart {:width 800 :height 300} (:items @state)]]])))

#_(defn $index [params]
  (let [state (r/atom {:inc 1 :items []})
        handle (fn []
                 (let [cur @state]
                   (reset! state {:items (reduce (fn [acc i]
                                                   (.. acc (push #js{:x i :y (.sin js/Math (+ (rand) (/ i 5)))}))
                                                   acc) #js[] (range 100))})))]
    (fn []
      [l/layout {}
       [:div
        [:button {:on-click handle} "Update"]
        (style [:svg [:circle {:fill "#fff"}]
                [:text {:fill "yellow"}]])
        [:pre (pr-str @state)]
        [:h1 "Chart"]
        [dsl/demo {:width 800 :height 400} #js[1 2 3 4]]

        ]])))
