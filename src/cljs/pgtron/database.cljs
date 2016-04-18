(ns pgtron.database
  (:require-macros [cljs.core.async.macros :as m :refer [go alt!]])
  (:require [reagent.core :as reagent :refer [atom]]
            [pgtron.layout :as l]
            [pgtron.pg :as pg]
            [cljs.core.async :refer [>! <!]]
            [pgtron.style :refer [style]]))

(def tables-query "
  SELECT *
        , pg_size_pretty(pg_relation_size(c.oid)) AS size
   FROM pg_tables t
   JOIN pg_class c
     ON c.relname = t.tablename
  JOIN pg_namespace n
    ON n.oid = c.relnamespace AND t.schemaname = n.nspname
  ")

(defn extensions [db]
  (let [state (atom {})]
    (go
      (let [res (<! (pg/exec db "SELECT * FROM pg_extension"))]
        (swap! state assoc :items res)))
    (fn []
      [:div#tables
       (style
        [:#tables
         [:.tbl {:display "inline-block"
                 :$width 25
                 ;;:$height 5
                 :vertical-align "top"
                 :$margin 0.5
                :$color [:gray :bg-1]
                :$padding 1}
          [:h3 {:$color [:gray]
                :$text [1.2 1.5 :center]}]]])
       [:h4 "Extesions"]
       [:div.section
        (for [tbl (:items @state)]
          [:div.tbl {:key (.stringify js/JSON tbl nil " ")}
           [:h3 (.-extname tbl) " " (.-extversion tbl)]
           #_[:pre (.stringify js/JSON tbl nil " ")]])]])))

(defn tables [db]
  (let [state (atom {})]
    (go
      (let [res (<! (pg/exec db tables-query))]
        (swap! state assoc :items
               (->> res
                    (group-by (fn [x] (.-schemaname x)))
                    (map (fn [[k v]] [k (sort-by #(.-tablename %)  v)]))
                    (into {})))))
    (fn []
      [:div#tables
       (style
        [:#tables
         [:.tbl {:display "inline-block"
                 :$width 25
                 ;;:$height 5
                 :vertical-align "top"
                 :$margin 0.5
                :$color [:gray :bg-1]
                :$padding 1}
          [:h3 [:i {:$color [:gray]}]]
          [:.label {:$text [1 1.2 300 :center]
                    :$color :light-gray
                    :display "block"}]
          [:.details {:$text 0.8
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

(defn $index [{db :db}]
  [l/layout {:bread-crump [{:title (str "db: " db)}]}
   [:div#database
    (style [:#database
            [:.section {:$margin [0 0 0 2]}]])
    [extensions db]
    [tables db]]])
