(ns pgtron.view
  (:require [reagent.core :as reagent :refer [atom]]
            [pgtron.layout :as l]
            [pgtron.pg :as pg]
            [pgtron.widgets :as wg]
            [chloroform.core :as form]
            [pgtron.style :refer [style icon]]))

(defn json [x]
  (.stringify js/JSON x nil " "))

(defn sql [sch vw]
  (str "SELECT pg_get_viewdef('" vw "', true) as define"))

(defn $index [{db :db sch :schema vw :view :as params}]
  (let [state (atom {})]
    (pg/query-assoc db (str "SELECT * FROM " sch "." vw " LIMIT 20") state [:data])

    (pg/query-assoc db (sql sch vw) state [:define]
                    (fn [xs]
                      (when-let [row (first xs)] (.-define row))))

    (fn []
      [l/layout {:params params
                 :bread-crump [{:title [:span (icon :folder-o) " " sch]
                                :href (str "#/db/" db "/schema/" sch)}
                               {:title [:span (icon :eye) " " vw]}]}
       [:div#view
        (style [:#view {:$padding [1 2]}
                [:.CodeMirror {:height "auto"}]])
        [:h3 "Definition"]
        [form/codemirror state [:define] {:theme "railscasts"
                                          :mode "text/x-sql"
                                          :extraKeys {"Ctrl-Enter" println}}]

        [:div#data
         [:h3 "Data"]
         [wg/table (:data @state)]]
        ]])))
