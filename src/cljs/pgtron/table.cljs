(ns pgtron.table
  (:require-macros [cljs.core.async.macros :as m :refer [go alt!]])
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.string :as str]
            [pgtron.layout :as l]
            [pgtron.pg :as pg]
            [charty.core :as chart]
            [cljs.core.async :refer [>! <!]]
            [pgtron.style :refer [style icon]]))

(defn query-attrs [db tbl]
  (str
"SELECT
a.attname as column_name,
t.relname as table_name,
tp.typname as type,
s.stanullfrac AS null_frac,
s.stawidth AS avg_width,
s.stadistinct AS n_distinct
FROM pg_attribute a
JOIN pg_class t ON t.oid = a.attrelid
JOIN pg_type tp  ON a.atttypid = tp.oid
LEFT JOIN pg_statistic s ON s.starelid = a.attrelid AND s.staattnum = a.attnum
WHERE NOT a.attisdropped
AND NOT a.attislocal
 AND t.relname = '" tbl "'"))

(defn query-indices [db tbl]
  (str "select i.relname as index_name,
       a.attname as column_name,
       pg_get_indexdef(i.oid) as define
       --i.*
 from pg_class t,
      pg_class i,
      pg_index ix,
      pg_attribute a
where t.oid = ix.indrelid
  and i.oid = ix.indexrelid
  and a.attrelid = t.oid
  and a.attnum = ANY(ix.indkey)
  and t.relkind = 'r'
  and t.relname  = '" tbl "'
order by i.relname"))

(defn query-data [db tbl]
  (str "SELECT *  FROM " tbl " LIMIT 50"))

(defn query-stats [db tbl]
  (str "SELECT *  FROM pg_stats WHERE tablename = '" tbl "'"))


(defn table [db tbl]
  (let [state (atom {})]
    (pg/query-assoc db (query-attrs db tbl) state [:items])
    (pg/query-assoc db (query-data db tbl) state [:data])
    (pg/query-assoc db (query-indices db tbl) state [:indices])
    (pg/query-assoc db (query-stats db tbl) state [:stats])
    (fn []
      [:div#table
       (style
        [:#table {:$padding [1 6]}
         [:h3 {:$margin [1 0]
               :border-bottom "1px solid #666"
               :$color :gray}]
         [:#data {:$color [:white :bg-1]
                  :$text [0.8]
                  :vertical-align "top"
                  :clear "both"
                  :$margin [1 0]
                  :$padding [1 2]}]
         [:.columns {:$color [:white :bg-1]
                     :vertical-align "top"
                     :$margin [0 1 1 0]
                     :float "left"
                     :$padding [1 2]
                     :display "inline-block"}
          [:p.notes {:$margin [1 0 0 0]
                     :width "50em"
                     :$text [0.8]}
           [:b {:$color :orange}]]
          [:td.num {:text-align "right" :$color :blue}]
          [:th {:$color :gray}]
          [:.type {:$color :green}]
          [:.attr {:display "block"
                   :$padding 0.1}]]])
       
       [:div.columns
        [:h3 "Columns"]
        [:table.table-condensed
         [:thead
          [:tr
           [:th "column"]
           [:th "type"]
           [:th "nulls"]
           [:th "correlation"]
           [:th "distinct"]
           [:th "avg width"]
           [:th "default"]]]
         [:tbody
          (for [attr (:items @state)]
            [:tr {:key (.-column_name attr)}
             [:td (.-column_name attr)]
             [:td [:span.type (.-type attr)]]
             [:td.nulls
              (if (= "NO" (.-is_nullable attr))
                [:span.text-muted "NOT NULL"]
                (.-null_frac attr))]
             [:td.num (.-correlation attr)]
             [:td.num (.-n_distinct attr)]
             [:td.num (.-avg_width attr)]
             [:td.text-muted (.-column_default attr)]
             #_[:td [:pre (.stringify js/JSON attr nil " ")]]])]]
        [:p.notes
         [:b "Correlation "]
         "Statistical correlation between physical row ordering and
         logical ordering of the column values. This ranges from -1 to +1. When
         the value is near -1 or +1, an index scan on the column will be
         estimated to be cheaper than when it is near zero, due to reduction of
         random access to the disk. (This column is null if the column data type
         does not have a < operator.)"
         [:br]
         [:b "Distinct "]
         "If greater than zero, the estimated number of distinct values in the
          column. If less than zero, the negative of the number of distinct values divided
          by the number of rows. (The negated form is used when ANALYZE believes that the
          number of distinct values is likely to increase as the table grows; the positive
          form is used when the column seems to have a fixed number of possible values.)
          For example, -1 indicates a unique column in which the number of distinct values
          is the same as the number of rows."]]

       [:div.columns
        [:h3 "Indices"]
        (for [attr (:indices @state)]
          [:div.attr {:key (.-index_name attr)}
           [:span (str/replace (.-define attr) #"CREATE (UNIQUE )?INDEX " "")]
           #_[:pre (.stringify js/JSON attr nil " ")]])]


       [:div.columns
        [:h3 "Columns Size"]
        [chart/pie {:width 500 :height 220}
         (map (fn [x] {:label (.-column_name x)
                       :value (.-avg_width x)})
              (take 5 (reverse (sort-by #(.-avg_width %) (:items @state)))))]]

       [:div#data
        [:h3 "Data"]
        (let [rows (:data @state)
              one (first rows)
              keys (and one (.keys js/Object one))]
          [:table.table-condensed
           [:thead
            [:tr
             (for [k keys] [:th {:key k} k])]]
           [:tbody
            (for [row (:data @state)]
              [:tr {:key (.stringify js/JSON row)}
               (for [k keys]
                 [:td.value {:key k :title k}
                  (let [value (.stringify js/JSON (aget row k) nil " ")]
                    (if (< (.-length value) 100)
                      value
                      (str (.substring value 0 100) "...")))
                  ])])]])]])))

(defn $index [{db :db sch :schema tbl :table :as params}]
  [l/layout {:params params
             :bread-crump [{:title [:span (icon :folder-o) " " sch]
                            :href (str "#/db/" db "/schema/" sch)}
                           {:title [:span (icon :table) " " tbl]}]}
   [:div#database
    (style [:#database {}])
    [table db tbl]]])
