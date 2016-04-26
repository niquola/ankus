(ns charty.sankey
  (:require [reagent.core :as r]))

(defn select [sel] (.. js/d3 (select sel)))

(defn translate [from to]
  (str "translate(" from "," to ")"))

(defn mk-data [data]
  (let [nodes #js[#js{:name "one"}
                  #js{:name "two"}
                  #js{:name "zero"}
                  #js{:name "tree"}
                  #js{:name "4node"}
                  #js{:name "fifth"}
                  ]
        idx (.reduce nodes (fn [acc x idx] (aset acc (.-name x) idx) acc) #js{})
        links #js[#js{:source "one" :target "two" :value 20}
                  #js{:source "tree" :target "4node" :value 5}
                  #js{:source "one" :target "tree" :value 7}
                  #js{:source "zero" :target "two" :value 4}
                  #js{:source "two" :target "tree" :value 7}
                  #js{:source "4node" :target "fifth" :value 17}
                  ]

        links-data (reduce (fn [acc x]
                             (.push acc #js{:source (aget idx (.-source x))
                                            :target (aget idx (.-target x))
                                            :value (or (.-value x) 10)})
                             acc) #js[] links)]
    [nodes links-data]))

(defn sankey-diag [opts data]
  (let [id (gensym)
        margin {:top 50 :left 50 :right 50 :bottom 50}
        box {:width (- (:width opts) (+ (:left margin) (:right margin)))
             :height (- (:height opts) (+ (:top margin) (:bottom margin)))}

        render (fn [data]
                 (.. (select (str "#" id)) (select "g.pane") (remove))

                 (let [g (.. (select (str "#" id))
                           (append "g")
                           (attr "class" "pane")
                           (attr "transform" (translate (:left margin) (:right margin))))

                       color (.. js/d3 -scale (category20))

                       sank (.. js/d3 (sankey)
                                  (nodeWidth 5)
                                  (nodePadding 30)
                                  (size #js[(:width box) (:height box)]))

                       path  (.. sank (link))

                       node-data (.-nodes data)

                       link-data (.-links data) ]

                   (.. sank
                       (nodes node-data)
                       (links link-data)
                       (layout 32))

                   (let [link (.. g (append "g")
                                  (selectAll ".link")
                                  (data link-data)
                                  (enter)
                                  (append "path")
                                  (attr "class" "link")
                                  (attr "d" (fn [d] (path d)))
                                  (style "fill" "transparent")
                                  (style "stroke"
                                         (fn [d]
                                           (let [clr (color (.. d -source -type))]
                                             (aset d "color" clr)
                                             clr)))
                                  (style "stroke-width" (fn [d] (.max js/Math 1 (.-dy d))))
                                  (sort (fn [a b] (- (.-dy b) (.-dy a)))))

                         _ (.. link (append "title")
                               (text (fn [d] (.. d -details))))

                         node (.. g (append "g")
                                  (selectAll ".node")
                                  (data node-data)
                                  (enter)
                                  (append "g")
                                  (attr "class" "node")
                                  (attr "transform" #(translate (.-x %) (.-y %)))
                                  #_(.call
                                   (.. js/d3 -behavior (drag)
                                       (origin identity)
                                       (on "dragstart" (fn [] (this-as this (.. this -parentNode (appendChild this)))))
                                       (on "drag" #(.log js/console "move")))))
                         ]
                     (.. node (append "rect")
                         (attr "height" #(.-dy %))
                         (attr "width" #(.. sank (nodeWidth)))
                         (style "fill" (fn [d]
                                         (let [clr (color (.-type d))]
                                           (aset d "color" clr)
                                           clr)))
                         (style "stroke" (fn [d] (.-color d)))
                         (append "title")
                         (text (fn [d] (.. d -name))))


                     (.. node (append "text")
                         (attr "x" -6)
                         (attr "y" (fn [d] (/ (.-dy d) 2)))
                         (attr "dy" "0.35em")
                         (attr "text-anchor" "end")
                         (attr "transform" nil)
                         (text (fn [d] (.-name d)))
                         (filter (fn [d] (< (.-x d) (/ (:width box) 2))))
                         (attr "x" (+ 6 (.. sank (nodeWidth))))
                         (attr "text-anchor" "start"))
                     )))]
    (r/create-class
     {:reagent-render (fn [] [:div
                              [:style "
                                  .node rect {cursor: move; fill-opacity: .9; shape-rendering: crispEdges;}
                                  .node text {pointer-events: none; font-size: 12px; fill: white;}
                                  .link {fill: none; stroke: #000; stroke-opacity: .2;}
                                  .link:hover {z-index: 1000; stroke-opacity: .5;}
                                  "]
                              [:svg.sankey {:id id :width (:width opts) :height (:height opts)}]])

      :component-did-mount (fn []
                             (let [d3data (clj->js data)]
                               (render d3data)))

      :component-did-update (fn [this]
                              (let [[_ _ data] (r/argv this)
                                    d3data (clj->js data)]
                                (render d3data)))})))

(defn copy [node]
  (let [obj (.parse js/JSON (.stringify js/JSON node))]
    (js-delete obj "Plans")
    obj))

(defn recur-plan [nodes links node]
  (let [dnode #js{:name (str (aget node "Node Type")
                             " "
                             (aget node "Relation Name"))
                  :type (aget node "Node Type")
                  :details (.stringify js/JSON (copy node) nil " ")
                  :value (aget node "Plan Width")}] ;;"Total Cost"
    
    (when-let [plans (aget node "Plans")]
      (doseq [plan plans]
        (let [child (recur-plan nodes links plan)]
          (.push links
                 #js{:target dnode
                     :source child
                     :details (.-details child) 
                     :value (.-value child)}))))
    (.push nodes dnode)
    dnode))

(defn plan-to-sankey [data]
  (let [plan (aget (first (aget data "QUERY PLAN")) "Plan")
        nodes #js[]
        links #js[]]
    (recur-plan nodes links plan)
    (println links)
    #js{:nodes nodes :links links}))
