(ns pgtron.view
  (:require [reagent.core :as reagent :refer [atom]]
            [pgtron.layout :as l]
            [pgtron.pg :as pg]
            [pgtron.docs :as docs]
            [pgtron.widgets :as wg]
            [chloroform.core :as form]
            [pgtron.style :refer [style icon]]))

(defn json [x]
  (.stringify js/JSON x nil " "))

(defn sql [sch vw]
  (str "SELECT pg_get_viewdef('" sch "." vw "', true) as define"))

(defn query-attrs [db tbl]
  (str
   "SELECT
a.attname as column_name,
a.attnotnull as not_null,
t.relname as table_name,
tp.typname as type
FROM pg_attribute a
JOIN pg_class t ON t.oid = a.attrelid
JOIN pg_type tp  ON a.atttypid = tp.oid
WHERE NOT a.attisdropped
AND a.attnum > 0 AND t.relname = '" tbl "'"))

(defn attributes [tbl attrs]
  [:div
  [:table.table-condensed
   [:thead
    [:tr
     [:th "column"]
     [:th "type"]]]
   [:tbody
    (for [attr attrs]
      [:tr {:key (.-column_name attr)}
       [:td (.-column_name attr)
        (when (.-not_null attr) [:span.required " *"])
        (when-let [doc (docs/view-column-docs tbl (.-column_name attr))]
          [wg/tooltip "docs" [:div
                              [:p {:dangerouslySetInnerHTML #js{:__html (.-ref doc)}}]
                              [:p {:dangerouslySetInnerHTML #js{:__html (.-details doc)}}]]])]
       [:td [:span.type (.-type attr)]]])]]])

(defn $index [{db :db sch :schema vw :view :as params}]
  (let [state (atom {})
        info (docs/view-docs vw)]

    (pg/query-assoc db (str "SELECT * FROM " sch "." vw " LIMIT 20") state [:data])

    (pg/query-assoc db (query-attrs db vw) state [:attrs])

    (pg/query-assoc db (sql sch vw) state [:define]
                    (fn [xs]
                      (when-let [row (first xs)] (.-define row))))

    (fn []
      [:div#view
       (style [:#view {:$padding [1 2]}
               [:.docs {:$width [60]}]
               wg/tooltip-style
               wg/block-style
               [:.type {:$color :green}]
               [:.CodeMirror {:height "auto"}]])

       (when info
         (wg/block "Documentation"
                   [:div.docs {:dangerouslySetInnerHTML #js{:__html info}}]))

       (wg/block "Columns" [attributes vw (:attrs @state)])

       (wg/block "Definition"
                 [form/codemirror state [:define] {:theme "railscasts"
                                                   :mode "text/x-sql"
                                                   :extraKeys {"Ctrl-Enter" println}}])

       (wg/block "Data" [wg/table (:data @state)])])))
