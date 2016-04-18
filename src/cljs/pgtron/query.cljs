(ns pgtron.query
  (:require-macros [cljs.core.async.macros :as m :refer [go alt!]])
  (:require [reagent.core :as reagent :refer [atom]]
            [pgtron.layout :as l]
            [pgtron.pg :as pg]
            [cljs.core.async :refer [>! <!]]
            [chloroform.core :as form]
            [charty.core :as chart]
            [pgtron.style :refer [style icon]]))

(defn- pie? [x]
  (.log js/console "PIE" (and x (.-pie_label x) (.-pie_value x) true) x)
  (and x (.-pie_label x) (.-pie_value x) true))

(def default-sql "
  SELECT pg_relation_size(c.oid) AS pie_value,
         t.tablename as pie_label
   FROM pg_tables t
   JOIN pg_class c
     ON c.relname = t.tablename
  JOIN pg_namespace n
    ON n.oid = c.relnamespace AND t.schemaname = n.nspname
  ORDER BY pg_relation_size(c.oid) DESC
  LIMIT 10 ")

(defn $index [params]
  (let [state (atom {:sql default-sql})
        handle (fn [ev]
                 (let [sql (:sql @state)]
                   (pg/query-assoc "postgres" sql
                                   state [:result]
                                   identity)))]
    (fn []
      [l/layout {:bread-crump [{:title "Query" :icon :search}]}
       [:div#query
        (style
         [:#query
          [:.results {:$margin 1}]
          [:textarea
           {:$color [:white :bg-0]
            :$padding 1
            :$height 4
            :$text [1.1 1.5]}]])
        [form/codemirror state [:sql] {:theme "railscasts"
                                       :mode "text/x-sql"
                                       :extraKeys {"Ctrl-Enter" handle}}]
        [:p.text-muted "Press [Ctrl-Enter] to execute query"]
        (let [rows (:result @state)
              fst (first rows)]
          [:div.results
           (when (pie? fst)
             [:div
              [:h3 "Pie Chart:"]
              [chart/pie {:width 1000 :height 600} (map (fn [x] {:label (.-pie_label x) :value (.-pie_value x)}) rows)]])
           (for [row rows]
             [:div {:key (gensym)}
              [:pre (.stringify js/JSON row nil " ")]])])]])))
