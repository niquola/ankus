(ns pgtron.demo
  (:require-macros [cljs.core.async.macros :as am])
  (:require [pgtron.layout :as l]
            [charty.core :as chart]
            [charty.dsl :as dsl]
            [cljs.core.async :as a]
            [pgtron.pg :as pg]
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

#_(def fs (js/require "fs"))

#_(defn read-file [path]
  (.readFileSync fs path))

#_(def data (.parse js/JSON (read-file "plan.json")))

(defn mk-query [q]
  (str "
SELECT json_build_object(
  'tables', (SELECT json_agg(c.relname) FROM pg_class c WHERE relname ilike '%" q "%' LIMIT 30),
  'views',  (SELECT json_agg(c.relname) FROM pg_class c WHERE relname ilike '%" q "%' LIMIT 30),
  'procs',  (SELECT json_agg(DISTINCT c.proname) FROM pg_proc c WHERE proname ilike '%" q "%' LIMIT 30)
) as result
"))

(defn bind-chan [ch] (fn [ev] (let [q (.. ev -target -value)] (am/go (a/>! ch q)))))

(defn bind-query [ch sql-fn state pth]
  (am/go-loop []
    (let [q (a/<! ch)]
      (pg/query-first-assoc "postgres" (sql-fn q) state pth))
    (recur)))

(defn debounce [in ms]
  (let [out (a/chan)]
    (am/go-loop [last-val nil]
      (let [val (if (nil? last-val) (<! in) last-val)
            timer (a/timeout ms)
            [new-val ch] (a/alts! [in timer])]
        (condp = ch
          timer (do (a/>! out val) (recur nil))
          in (recur new-val))))
        out))

(defn $index [params]
  (let [model l/model
        q-ch (a/chan)
        handle (bind-chan q-ch)]

    (bind-query (debounce q-ch 200) mk-query model [:data])
    (fn []
      (let [data (or (when-let [d (:data @model)] (.-result d)) #js{})]
        [l/page model
        [:div#demo
         (style [:#demo {:$padding [2 4]}
                 [:input {:$color [:black :white] :width "100%" :display "block"}]])
         [:input {:on-change handle}]
         [:br]
         [:h3 "Tables"]
         (for [i (.-tables data)]
           [:div {:key i} i])

         [:h3 "Views"]
         (for [i (.-views data)]
           [:div {:key i} i])

         [:h3 "Procs"]
         (for [i (.-procs data)]
           [:div {:key i} i])

         #_[:pre (.stringify js/JSON data nil " ")]]]))))


