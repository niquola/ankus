(ns charty.core
  (:require [reagent.core :as r]))


(defn render-pie [{height :height width :width :as opts} data]
  #_(.log js/console "render" (pr-str opts))
  #_(.log js/console "data" data)
  (let [svg (.. js/d3 (select "svg") (append "g"))
        slices (.. svg (append "g") (attr "class" "slices"))
        lables (.. svg (append "g") (attr "class" "labels"))
        slieces (.. svg (append "g") (attr "class" "lines"))
        radius (/ (.min js/Math width height) 2.5)
        pie (.. (.pie (.-layout js/d3))
                (sort nil)
                (value (fn [x] (.-value x))))

        arc  (.. (.arc (.-svg js/d3))
                 (outerRadius (* radius 0.8))
                 (innerRadius (* radius 0.4)))

        outerArc  (.. (.arc (.-svg js/d3))
                      (outerRadius (* radius 0.9))
                      (innerRadius (* radius 0.9)))
        _ (.. svg (attr "transform" (str "translate(" (/ width 2) "," (/ height 2) ")")))

        key (fn [x] (.. x -data -label))
        color (.. (.-scale js/d3)
                  (ordinal)
                  (domain #js["Lorem ipsum", "dolor sit", "amet", "consectetur", "adipisicing", "elit", "sed", "do", "eiusmod", "tempor", "incididunt"])
                  (range  #js["#98abc5", "#8a89a6", "#7b6888", "#6b486b", "#a05d56", "#d0743c", "#ff8c00"]))

        update (fn [data]
                 (let [slice (.. svg
                                 (select ".slices")
                                 (selectAll "path.slice")
                                 (data (pie data) key))

                       _  (.. slice (enter)
                              (insert "path")
                              (style "fill" (fn [d] (color (.. d -data -label))))
                              (attr "class" "slice"))
                       _ (.. slice (transition) (duration 1000)
                             (attrTween "d", (let [current (atom nil)]
                                               (fn [d]
                                                 (when (not @current) (reset! current d))
                                                 (let [interpolate (.. js/d3 (interpolate @current d))]
                                                   (reset! current (interpolate 0))
                                                   (fn [t] (arc (interpolate t))))))))
                       _ (.. slice (exit) (remove))


                       text  (..  svg (select ".labels")
                                  (selectAll "text")
                                  (data (pie data) key))

                       _ (.. text (enter)
                             (append "text")
                             (attr "dy" ".35em")
                             (text (fn [d] (.. d -data -label))))

                       midAngle (fn [d]
                                  (+ (.-startAngle d)
                                     (/ (- (.-endAngle d) (.-startAngle d)) 2)))

                       _ (..  text (transition) (duration 1000)
                              (attrTween "transform"
                                         (let [current (atom nil)]
                                           (fn [d]
                                             (when (not @current) (reset! current d))
                                             (let [interpolate (.. js/d3 (interpolate @current d))]
                                               (reset! current (interpolate 0))
                                               (fn [t]
                                                 (let [d2 (interpolate t)
                                                       pos (.. outerArc (centroid d2))
                                                       pos-0  (* radius (if (< (midAngle d2) (.-PI js/Math)) 1 -1))]
                                                   (str "translate(" pos ")")))))))

                              (styleTween "text-anchor" (let [current (atom nil)]
                                                          (fn [d]
                                                            (when (not @current) (reset! current d))
                                                            (let [interpolate (.. js/d3 (interpolate @current d))]
                                                              (reset! current (interpolate 0))
                                                              (fn [t]
                                                                (let [d2 (interpolate t)]
                                                                  (if (< (midAngle d2) (.-PI js/Math)) "start" "end"))))))))

                       _ (.. text (exit) (remove))


                       polyline (.. svg (select ".lines") (selectAll "polyline") (data (pie data) key))

                       _ (.. polyline (enter) (append "polyline"))

                       _ (.. polyline (transition) (duration 1000)
                             (attrTween "points"
                                        (let [current (atom nil)]
                                          (fn [d]
                                            (when (not @current) (reset! current d))
                                            (let [interpolate (.. js/d3 (interpolate @current d))]
                                              (reset! current (interpolate 0))
                                              (fn [t]  (let [d2 (interpolate t)
                                                             pos (.. outerArc (centroid d2))]
                                                         (aset pos 0 (* 0.95 radius (if (< (midAngle d2) (.-PI js/Math)) 1 -1)))
                                                         #js[(.. arc (centroid d2))
                                                             (.. outerArc (centroid d2))
                                                             pos])))))))

                       _ (.. polyline (exit) (remove))]))]
    (update data)
    nil))

(defn pie [opts data]
  (.log js/console "pie" (pr-str opts) data)
  (r/create-class
   {:reagent-render (fn [] [:div
                            [:style " path.slice{ stroke-width:2px;} .labels text {fill: white;} polyline{opacity: .3; stroke: white; stroke-width: 2px; fill: none;} "]
                            [:svg {:width (:width opts)
                                        :height (:height opts)}]])

    :component-did-mount (fn []
                           (let [d3data (clj->js data)]
                             (render-pie opts d3data)))

    :component-did-update (fn [this]
                            (let [[_ _ data] (r/argv this)
                                  d3data (clj->js data)]
                              (render-pie opts d3data)))}))
