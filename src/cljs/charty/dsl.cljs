(ns charty.dsl
  (:require [reagent.core :as r]
            [clojure.string :as str]))

(comment
  [:polyline
   {:transition {:duration 1000
                 :tween {:attr  {:points (interpolate fn)}
                         :style {:text-anchor (interpol fn)}}}}]

  [:path {:style {:fill #(fill (.-index %))
                  :stroke #(fill (.-index %))}
          :d #(arc inner outer)
          :on {:mouse-over #(fade .1)
               :mouse-out #(fade 1)}}])

(defn select-all [sel] (.. js/d3 (selectAll sel)))

(defn select [sel] (.. js/d3 (select sel)))

(defn apply-attrs [node attrs]
  (let [node (->> (reduce (fn [acc [k v]] (.attr acc (name k) v))
                          node (dissoc attrs :duration :text)))]
    (if (:text attrs) (.text node (:text attrs)) node)))


(defn append [parent [tag {style :style text :text :as opts}]]
  (let [[tag cls] (str/split (name tag) #"\.")
        attrs     (dissoc opts :style :transition :duration)
        node (-> parent
                 (.append tag)
                 (cond-> cls (.attr "class" cls))
                 (apply-attrs attrs))
        node (if (:transition opts)
               (apply-attrs (.. node (transition)
                                (duration (or (get-in opts [:transition :duration]) 1000)))
                            (get-in opts [:transition]))
               node)]
    node))

(defn interpol [cb]
  (let [current (atom nil)]
    (fn [d]
      (when (not @current) (reset! current d))
      (let [interpolate (.. js/d3 (interpolate @current d))]
        (reset! current (interpolate 0))
        (fn [t] (cb interpolate t))))))

(defn mk-svg-component [render]
  (fn [opts data]
    (let [id (gensym)]
      (r/create-class
       {:reagent-render       (fn [] [:svg {:id id :width (:width opts) :height (:height opts)}])
        :component-did-mount  #(render (select (str "#" id)) opts (clj->js data))
        :component-did-update #(let [[_ _ data] (r/argv %)]
                                 (render (select (str "#" id)) opts (clj->js data)))}))))

(def demo
  (mk-svg-component
   (fn [svg opts data]
     (let [cicles (-> svg (.selectAll "circle") (.data data identity))
           enter (.enter cicles)]
       (->  enter
        (append [:circle {:transition  {:cx #(* 100 %)
                                        :cy #(* 100 %)
                                        :r  10}}]))
       (-> enter (append [:text {:duration 5000
                                 :transition {:text identity
                                              :x #(* 100 %)
                                              :dy "0.3em"
                                              :dx "0.3em"
                                              :y #(* 100 %)}}]))
       (-> cicles (.exit) (.remove)))
     nil)))
