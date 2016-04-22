(ns pgtron.database
  (:require-macros [cljs.core.async.macros :as m :refer [go alt!]])
  (:require [reagent.core :as reagent :refer [atom]]
            [pgtron.layout :as l]
            [pgtron.pg :as pg]
            [cljs.core.async :refer [>! <!]]
            [pgtron.style :refer [style]]))

(def extensions-query
  "SELECT * FROM pg_extension")

(def tables-query "
  SELECT *
        , pg_size_pretty(pg_relation_size(c.oid)) AS size
   FROM pg_tables t
   JOIN pg_class c
     ON c.relname = t.tablename
  JOIN pg_namespace n
    ON n.oid = c.relnamespace AND t.schemaname = n.nspname
  WHERE t.schemaname NOT IN ('information_schema', 'pg_catalog')
  ")

(def schema-sql "
  select
   schema_name,
   (SELECT count(*) FROM pg_tables t WHERE t.schemaname = schema_name) AS tables_count,
   (select count(*) from information_schema.views v WHERE v.table_schema = schema_name) AS views_count
   FROM information_schema.schemata
   ORDER BY schema_name
  ")

(defn extensions [db]
  (let [state (atom {})]
    (pg/query-assoc db extensions-query state [:items])
    (fn []
      [:div#extensions
       [:h4 "Extesions"]
       [:div.section
        (for [tbl (:items @state)]
          [:div.box {:key (.stringify js/JSON tbl nil " ")}
           [:h3 (.-extname tbl) " " (.-extversion tbl)]
           #_[:pre (.stringify js/JSON tbl nil " ")]])]])))

(defn schemas [db]
  (let [state (atom {})]
    (pg/query-assoc db schema-sql state [:items])
    (fn []
      [:div#schemas
       [:h4 "Schemata"]
       [:div.section
        (for [tbl (:items @state)]
          [:a.box {:key (.stringify js/JSON tbl nil " ") :href (str "/dbs/" db "/schema/" (.-schema_name tbl))}
           [:h3 (.-schema_name tbl)]
           [:div.details
            [:span (.-tables_count tbl) " tables; "]
            [:span (.-views_count tbl) " views; "]]])]])))

(defn *tables [db state]
  (go (let [res (<! (pg/exec db tables-query))]
      (swap! state assoc :items
             (->> res
                  (group-by (fn [x] (.-schemaname x)))
                  (map (fn [[k v]] [k (sort-by #(.-tablename %)  v)]))
                  (into {}))))))

(defn tables [db]
  (let [state (atom {})]
    (*tables db state)
    (fn []
      [:div#tables
       (style
        [:#tables
         [:.tbl {:display "inline-block"
                 :$width 25
                 :vertical-align "top"
                 :$margin 0.5
                 :$color [:gray :bg-1]
                 :$padding 1}
          [:h3 [:i {:$color [:gray]}]]
          [:.label {:$text [1 1.2 300 :center]
                    :$color :light-gray
                    :display "block"}]
          [:.details {:$text [0.8 1 :center]
                      :$padding 0.5}]]])

       (for [[sch tbls] (:items @state)]
         [:div {:key sch}
          [:h4 sch]
          [:div.section
           (for [tbl tbls]
             [:div.tbl {:key (.-tablename tbl)}
              [:a.label {:href (str "#/db/" db "/tbl/" (.-tablename tbl))} (.-tablename tbl)]
              [:div.details
               [:span.user [:a {:href (str  "#/users" (.-tableowner tbl))} "@" (.-tableowner tbl) " "]]
               "~" (.-reltuples tbl) " rows; "
               (.-size tbl)]
              #_[:pre (.stringify js/JSON tbl nil " ")]])]])])))

(defn $index [{db :db :as params}]
  [l/layout {:bread-crump [{:title (str "db: " db) :params params}]}
   [:div#database
    (style [:#database
            [:.section {:$margin [0 0 0 2]}]
            [:.box {:display "inline-block"
                    :$width 25
                    :vertical-align "top"
                    :$margin 0.5
                    :$color [:gray :bg-1]
                    :$padding [0.5 1]}
             [:.details {:$text [0.8 1 :center] :$padding 0.5}]
             [:h3 {:$color :light-gray
                   :$text [1 1.2 :center]}]]])
    [extensions db]
    [schemas db]
    [tables db]]])
