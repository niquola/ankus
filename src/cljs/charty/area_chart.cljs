(ns charty.area-chart
  (:require [reagent.core :as r]))

(defn select [sel] (.. js/d3 (select sel)))

(defn linear [from to]
  (.. js/d3 -scale (linear) (range #js[from to])))

(defn axis [sc ori]
  (.. js/d3 -svg (axis) (scale sc) (orient ori)))

(defn mk-area [x-fn y0-fn y1-fn]
  (.. js/d3 -svg (area)
      (x0 x-fn)
      (x1 x-fn)
      (y0 y0-fn)
      (y1 y1-fn)))

(defn translate [from to]
  (str "translate(" from "," to ")"))


(defn area-chart [opts data]
  (let [id (gensym)
        margin {:top 50 :left 50 :right 50 :bottom 50}
        box {:width (- (:width opts) (+ (:left margin) (:right margin)))
             :height (- (:height opts) (+ (:top margin) (:bottom margin)))}
        x   (linear 0 (:width box))
        y   (linear (:height box) 0)
        x-ax (axis x "bottom")
        y-ax (axis y "left")
        ar   (mk-area (fn [d] (x (.-x d))) (:height box) (fn [d] (y (.-y d))))

        render (fn [data]
                 (.. (select (str "#" id)) (select "g.pane") (remove))

                 (let [g (.. (select (str "#" id))
                           (append "g")
                           (attr "class" "pane")
                           (attr "transform" (translate (:left margin) (:right margin))))]

                   (.domain x (.extent js/d3 data (fn [d] (.-x d))))
                   (.domain y (.extent js/d3 data (fn [d] (.-y d))))

                   (.. g
                       (datum data)
                       (append "path")
                       (attr "class" "area")
                       (attr "d" ar))

                   (.. g
                       (append "g")
                       (attr "class" "x axis")
                       (attr "transform" (translate 0 (:height box)))
                       (call x-ax))

                   (.. g
                       (append "g")
                       (attr "class" "y axis")
                       (call y-ax))))]
    (r/create-class
     {:reagent-render (fn [] [:div
                              [:style "
                                  .area-chart .area {
                                      fill: #6ED0E0; 
                                      opacity: 0.4;
                                      stroke-width: 1px;
                                      stroke: #555;
                                  }
                                  .area-chart .axis path,
                                  .area-chart .axis line {
                                    fill: none;
                                    stroke: #ddd;
                                    shape-rendering: crispEdges;
                                  }
                                  .area-chart text { font-size: 12px; stroke-width: 0; fill: #ddd; }
                                  "]
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
