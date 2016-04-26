(ns charty.sankey
  (:require [reagent.core :as r]))

(defn select [sel] (.. js/d3 (select sel)))

(defn translate [from to]
  (str "translate(" from "," to ")"))


(defn sankey [opts data]
  (let [id (gensym)
        margin {:top 50 :left 50 :right 50 :bottom 50}
        box {:width (- (:width opts) (+ (:left margin) (:right margin)))
             :height (- (:height opts) (+ (:top margin) (:bottom margin)))}

        render (fn [data]
                 (.. (select (str "#" id)) (select "g.pane") (remove))

                 (let [g (.. (select (str "#" id))
                           (append "g")
                           (attr "class" "pane")
                           (attr "transform" (translate (:left margin) (:right margin))))]))]
    (r/create-class
     {:reagent-render (fn [] [:div
                              [:style "
                                  .sankey {
                                      fill: #6ED0E0; 
                                      opacity: 0.4;
                                      stroke-width: 1px;
                                      stroke: #555;
                                  } "]
                              [:svg.area-chart {:id id
                                     :width (:width opts)
                                     :height (:height opts)}]])

      :component-did-mount (fn []
                             (let [d3data (clj->js data)]
                               (render d3data)))

      :component-did-update (fn [this]
                              (let [[_ _ data] (r/argv this)
                                    d3data (clj->js data)]
                                (render d3data)))})))
