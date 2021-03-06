(ns pgtron.query
  (:require [reagent.core :as reagent :refer [atom]]
            [pgtron.pg :as pg]
            [chloroform.core :as form]
            [pgtron.widgets :as wg]
            [charty.core :as chart]
            [pgtron.style :refer [style icon]]))

(defn- pie? [x]
  (and x (.-pie_label x) (.-pie_value x) true))

(defn- chart? [x]
  (and x (.-area_x x) (.-area_y x) true))

(defn- graph? [x]
  (and x (.-graph_source_id x) (.-graph_target_id x)))

(defn- explain? [x]
  (and x (aget x "QUERY PLAN")))

(def examples [{:title "Pie Chart"
                :sql "SELECT pg_relation_size(c.oid) AS pie_value,
         t.tablename as pie_label
   FROM pg_tables t
   JOIN pg_class c
     ON c.relname = t.tablename
  JOIN pg_namespace n
    ON n.oid = c.relnamespace AND t.schemaname = n.nspname
  ORDER BY pg_relation_size(c.oid) DESC
  LIMIT 10 "
                }
               {:title "Area Chart"
                :sql "SELECT x area_x, random() * 0.1 + sin(x / 5) area_y
 FROM generate_series(1,100) x"}

               {:title "Graph Chart"
                :sql "
SELECT tc.table_name as graph_source_id,
   ccu.table_name AS graph_target_id
FROM
information_schema.table_constraints AS tc
JOIN information_schema.key_column_usage AS kcu
  ON tc.constraint_name = kcu.constraint_name
JOIN information_schema.constraint_column_usage AS ccu
  ON ccu.constraint_name = tc.constraint_name
WHERE constraint_type = 'FOREIGN KEY'"}

               {:title "Explain"
                :sql "EXPLAIN (FORMAT JSON)
  SELECT *
    FROM information_schema.columns
   WHERE column_name = 'ups'
   LIMIT 10"}])


(defn $index [state]
  (let [handle (fn [ev] (pg/query-assoc (:sql @state) state [:result] identity))]
    (swap! state assoc :sql (:sql (first examples)))
    (fn []
      [:div#query
       (style
        [:#query {:$padding [0 1]}
         [:.results {:$margin [1 0]}]
         [:.example {:padding "0 0.3em" :cursor "pointer"}]
         [:textarea
          {:$color [:white :bg-0]
           :$padding 1
           :$height 4
           :$text [1.1 1.5]}]])
       [form/codemirror state [:sql] {:theme "railscasts"
                                      :mode "text/x-sql"
                                      :extraKeys {"Ctrl-Enter" handle}}]
       [:p.text-muted "Press [Ctrl-Enter] to execute query. Examples: "
        (for [e examples]
          [:a.example {:key (:title e) :on-click (fn [ev] (.preventDefault ev)
                                                   (swap! state assoc :sql (:sql e))
                                                   (handle ev))}
           (:title e) " "])]
       (let [rows (:result @state)
             fst (first rows)]
         [:div.results
          (cond
            (pie? fst) [:div
                        [:h3 "Pie Chart:"]
                        [chart/pie {:width 1000 :height 600} (map (fn [x] {:label (.-pie_label x) :value (.-pie_value x)}) rows)]]

            (graph? fst) [:div
                          [:h3 "Graph Chart:"]
                          [chart/force-graph {:width 1000 :height 600} rows]]

            (explain? fst) [:div [:h1 "Graph"]
                            [chart/sankey {:width 1200 :height 800}
                             (chart/plan-to-sankey fst)]]
            
            (chart? fst) [:div
                          [:h3 "Area Chart:"]
                          [chart/area-chart {:width 1000 :height 400}
                           (map (fn [d] {:x (.-area_x d) :y (.-area_y d)}) rows)]])
          [wg/table rows]])])))
