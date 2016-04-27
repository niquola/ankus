(ns pgtron.table
  (:require-macros [cljs.core.async.macros :as m :refer [go alt!]])
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.string :as str]
            [pgtron.layout :as l]
            [pgtron.pg :as pg]
            [pgtron.docs :as docs]
            [pgtron.widgets :as wg]
            [charty.core :as chart]
            [cljs.core.async :refer [>! <!]]
            [pgtron.style :refer [style icon]]))

(defn query-attrs [db tbl]
  (str
"SELECT
a.attname as column_name,
a.attnotnull as not_null,
t.relname as table_name,
tp.typname as type,
round((
CASE
    WHEN s.stakind1 = 3 THEN s.stanumbers1[1]
    WHEN s.stakind2 = 3 THEN s.stanumbers2[1]
    WHEN s.stakind3 = 3 THEN s.stanumbers3[1]
    WHEN s.stakind4 = 3 THEN s.stanumbers4[1]
    WHEN s.stakind5 = 3 THEN s.stanumbers5[1]
    ELSE NULL::real
END
)::numeric
, 3) AS correlation,
s.stanullfrac AS null_frac,
s.stawidth AS avg_width,
round(s.stadistinct::numeric, 3) AS n_distinct
FROM pg_attribute a
JOIN pg_class t ON t.oid = a.attrelid
JOIN pg_type tp  ON a.atttypid = tp.oid
LEFT JOIN pg_statistic s ON s.starelid = a.attrelid AND s.staattnum = a.attnum
WHERE NOT a.attisdropped
AND a.attnum > 0
 AND t.relname = '" tbl "'"))

(defn query-indices [db tbl]
  (str "select i.relname as index_name,
     --  a.attname as column_name,
       pg_get_indexdef(i.oid) as define
       --i.*
 from pg_class t,
      pg_class i,
      pg_index ix

--      pg_attribute a
where t.oid = ix.indrelid
  and i.oid = ix.indexrelid
--  and a.attrelid = t.oid
--  and a.attnum = ANY(ix.indkey)
  and t.relkind = 'r'
  and t.relname  = '" tbl "'
order by i.relname"))

(defn query-data [db tbl]
  (str "SELECT *  FROM " tbl " LIMIT 50"))

(defn query-stats [db tbl]
  (str "SELECT *  FROM pg_stats WHERE tablename = '" tbl "'"))


(defn table [db tbl]
  (let [state (atom {})
        info (docs/catalog-docs tbl)]
    (pg/query-assoc db (query-attrs db tbl) state [:items])
    (pg/query-assoc db (query-data db tbl) state [:data])
    (pg/query-assoc db (query-indices db tbl) state [:indices])
    (pg/query-assoc db (query-stats db tbl) state [:stats])
    (fn []
      [:div#table
       (style
        [:#table {:$padding [1 6]}
         wg/tooltip-style
         [:h3 {:$margin [1 0]
               :border-bottom "1px solid #666"
               :$color :gray}]
         [:span.required {:$color :red}]
         [:#data {:$color [:white :bg-1]
                  :$text [0.8]
                  :float "left"
                  :vertical-align "top"
                  :clear "both"
                  :$margin [1 0]
                  :$padding [1 2]}]
         docs/styles
         [:.docs {:$width 60}
          #_[:row {:display "none"}]]
         wg/block-style
         [:.block 
          [:td.num {:text-align "right" :$color :blue}]
          [:th {:$color :gray}]
          [:.type {:$color :green}]
          [:.attr {:display "block"
                   :$padding 0.1}]]])

       
       
       (wg/block "Columns"
        [:div
         [:table.table-condensed
          [:thead
           [:tr
            [:th "column"]
            [:th "type"]
            [:th "nulls"]
            [:th "correlation" [wg/tooltip "Distinct" (docs/tip :pg_statistic.correlation)]]
            [:th "distinct"    [wg/tooltip "Distinct" (docs/tip :pg_statistic.distinct)]]
            [:th "avg width"]
            [:th "default"]]]
          [:tbody
           (for [attr (:items @state)]
             [:tr {:key (.-column_name attr)}
              [:td (.-column_name attr)
               (when (.-not_null attr) [:span.required " *"])
               (when-let [doc (docs/catalog-column-docs tbl (.-column_name attr))]
                 [wg/tooltip "docs" [:div
                                     [:p {:dangerouslySetInnerHTML #js{:__html (.-ref doc)}}]
                                     [:p {:dangerouslySetInnerHTML #js{:__html (.-details doc)}}]]])]
              [:td [:span.type (.-type attr)]]
              [:td.nulls
               (when-not (.-not_null attr) (.-null_frac attr))]
              [:td.num (.-correlation attr)]
              [:td.num (.-n_distinct attr)]
              [:td.num (.-avg_width attr)]
              [:td.text-muted (.-column_default attr)]])]]])


       (when info
         (wg/block "Documentation"
                   [:div.docs {:dangerouslySetInnerHTML #js{:__html info}}]))

       (wg/block "Columns Size"
        [:div.docs
         [chart/pie {:width 500 :height 220}
          (map (fn [x] {:label (.-column_name x)
                        :value (.-avg_width x)})
               (take 5 (reverse (sort-by #(.-avg_width %) (:items @state)))))]])

       (wg/block "Indices"
                 [:div (for [attr (:indices @state)]
                         [:div.attr {:key (.-index_name attr)}
                          [:span (str/replace (.-define attr) #"CREATE (UNIQUE )?INDEX " "")]])])


       (wg/block "Data" [wg/table (:data @state)])])))

(defn $index [{db :db sch :schema tbl :table :as params}]
  [l/layout {:params params
             :bread-crump [{:title [:span (icon :folder-o) " " sch]
                            :href (str "#/db/" db "/schema/" sch)}
                           {:title [:span (icon :table) " " tbl]}]}
   [:div#database
    (style [:#database {}])
    [table db tbl]]])
