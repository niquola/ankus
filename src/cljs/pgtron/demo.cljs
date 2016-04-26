(ns pgtron.demo
  (:require [pgtron.layout :as l]
            [charty.core :as chart]
            [charty.dsl :as dsl]
            [reagent.core :as r]
            [pgtron.style :refer [style]]))


#_(defn $index [params]
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

#_(def data
  #js[#js{:graph_source_id "node1" :graph_target_id "node2"}
      #js{:graph_source_id "node2" :graph_target_id "node2"}
      #js{:graph_source_id "node3" :graph_target_id "node2"}
      #js{:graph_source_id "node4" :graph_target_id "node4"}])

#_(defn $index [params]
  (let [state (r/atom {:inc 1 :items []})]
    (fn []
      [l/layout {}
       [:div [:h1 "Graph"]
        [chart/force-graph {:width 800 :height 800} data]]])))

(def fs (js/require "fs"))

(defn read-file [path]
  (.readFileSync fs path))

(def data (.parse js/JSON (read-file "plan.json")))

(defn $index [params]
  (let [state (r/atom {:inc 1 :items []})]
    (fn []
      [l/layout {}
       [:div [:h1 "Graph"]
        [chart/sankey {:width 1400 :height 1000} (chart/plan-to-sankey data)]]])))


