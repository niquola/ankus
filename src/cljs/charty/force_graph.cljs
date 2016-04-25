(ns charty.force-graph
  (:require [reagent.core :as r]))

(defn select [sel] (.. js/d3 (select sel)))


(defn $or [x y] (if x x y))

(defn collect-nodes [data]
  (let [idx #js{}
        nodes #js[]
        links #js[]]

    (doseq [d data]
      (when-let [sid (.-graph_source_id d)]
        (when-not (aget idx sid)
          (let [id (.-length nodes)]
            (.push nodes #js{:label sid})
            (aset idx sid id))))

      (when-let [tid (.-graph_target_id d)]
        (when-not (aget idx tid)
          (let [id (.-length nodes)]
            (.push nodes #js{:label tid})
            (aset idx tid id)))))

    (doseq [d data]
      (let [tid (aget idx (.-graph_target_id d))
            sid (aget idx (.-graph_source_id d))]
        (when (and sid tid)
          (.push links #js{:source sid :target tid :weight 1}))))

    [nodes links]))

(defn force-graph [opts data]
  (let [id (gensym)

        render (fn [data]
                 (let [svg (select (str "#" id))

                       g (.. svg (append "g") (attr "class" "graph"))

                       [nodes links] (collect-nodes data)

                       force  (.. js/d3 -layout
                                  (force)
                                  (size #js[(:width opts) (:height opts)])
                                  (nodes nodes)
                                  (links links)
                                  (gravity 0.1)
                                  (linkDistance 100)
                                  (charge -300)
                                  (linkStrength (fn [x] (or (* (.-weight x) 10) 1))))

                       link   (.. g (selectAll "line.link")
                                  (data links)
                                  (enter)
                                  (append "svg:line")
                                  (attr "class" "link")
                                  (style "stroke-width" "1px")
                                  (style "stroke" "#555"))

                       node  (.. g (selectAll "g.node")
                                 (data (.nodes force))
                                 (enter)
                                 (append "g")
                                 (attr "class" "node"))

                       _ (do (.. node (append "circle")
                                 (attr "r" 8)
                                 (style "fill" "#555")
                                 (style "stroke" "#777")
                                 (style "stroke-width" "1px"))
                             (.. node (append "text")
                                 (attr "dy" "0.1em")
                                 (attr "dx" "0.8em")
                                 (style "stroke-width" "0")
                                 (style "fill" "#ddd")
                                 (style "font-size" "12px")
                                 (text (fn [d] (.-label d))))
                             (.call node (.-drag force)))

                       update-link (fn []
                                     (this-as x
                                       (.. x (attr "x1" (fn [d] ($or (.. d -source -x) 0))))
                                       (.. x (attr "y1" (fn [d] ($or (.. d -source -y) 0))))
                                       (.. x (attr "x2" (fn [d] ($or (.. d -target -x) 0))))
                                       (.. x (attr "y2" (fn [d] ($or (.. d -target -y) 0))))))

                       update-node (fn []
                                     (this-as x
                                       (.. x (attr "transform" (fn [d]
                                                                 (str "translate(" ($or (.-x d) 10) "," ($or (.-y d) 10) ")"))))))]
                   (.start force)

                   (.. force (on "tick" (fn []
                                          (.call node update-node)
                                          (.call link update-link))))))]
    (r/create-class

     {:reagent-render (fn [] [:div [:svg.force {:id id :width (:width opts) :height (:height opts)}]])

      :component-did-mount (fn [] (let [d3data (clj->js data)] (render d3data)))

      :component-did-update (fn [this]
                              #_(let [[_ _ data] (r/argv this)
                                    d3data (clj->js data)]
                                (render d3data)))})))
