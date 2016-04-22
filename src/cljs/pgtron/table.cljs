(ns pgtron.table
  (:require-macros [cljs.core.async.macros :as m :refer [go alt!]])
  (:require [reagent.core :as reagent :refer [atom]]
            [pgtron.layout :as l]
            [pgtron.pg :as pg]
            [cljs.core.async :refer [>! <!]]
            [pgtron.style :refer [style icon]]))

(def tables-query " SELECT 1 ")


(defn query-attrs [db tbl]
  (str "SELECT *  FROM information_schema.columns
        WHERE table_name = '" tbl "'
        ORDER BY ordinal_position"))

(defn query-data [db tbl]
  (str "SELECT *  FROM " tbl " LIMIT 10"))

#_[:div.attr {:key (.-attname attr)}
   [:b (.-attname attr)]
   " correlation " (.-correlation attr) "; "
   " average width: " (.-avg_width attr) "; "
   " nulls: " (.-null_frac attr) "; "
   (when-let [cv (.-most-common_vals attr)]
     (str " most common values " cv "; "))
   #_[:pre (.stringify js/JSON attr nil " ")]]

(defn table [db tbl]
  (let [state (atom {})]
    (pg/query-assoc db (query-attrs db tbl) state [:items])
    (pg/query-assoc db (query-data db tbl) state [:data])
    (fn []
      [:div#table
       (style
        [:#table {:$padding [1 6]}
         [:h3 {:$margin [0.5 0]
               :border-bottom "1px solid #666"
               :$color :gray}]
         [:#data {:$color [:white :bg-1]
                  :$margin [2 0]
                  :$padding [1 2]}]
         [:.columns {:$color [:white :bg-1]
                     :$padding [1 2]
                     :display "inline-block"}
          [:.attr {:display "block"
                   :$padding 0.1}
           [:.type {:$color :green}]]]])
       [:div.columns
        [:h3 "Columns"]
        (for [attr (:items @state)]
          [:div.attr {:key (.-column_name attr)}
           [:span (.-column_name attr)]
           " "
           [:span.type (.-data_type attr)]
           " "
           (when (= "NO" (.-is_nullable attr)) [:span.text-muted " NOT NULL "])
           " "
           (when-let [default (.-column_default attr)] [:span " DEFAULT " default])
           #_[:pre (.stringify js/JSON attr nil " ")]])]
       [:div#data
        [:h3 "Data"]
        (let [rows (:data @state)
              one (first rows)
              keys (and one (.keys js/Object one))]
          [:table.table
           [:thead
            [:tr
             (for [k keys] [:th {:key k} k])]]
           [:tbody
            (for [row (:data @state)]
              [:tr {:key (.stringify js/JSON row)}
               (for [k keys]
                 [:td {:key k} (aget row k)])])]])]

       ])))

(defn $index [{db :db sch :schema tbl :table :as params}]
  [l/layout {:params params
             :bread-crump [{:title [:span (icon :folder-o) " " sch]
                            :href (str "#/db/" db "/schema/" sch)}
                           {:title [:span (icon :table) " " tbl]}]}
   [:div#database
    (style [:#database {}])
    [table db tbl]]])
