(ns pgtron.table
  (:require-macros [cljs.core.async.macros :as m :refer [go alt!]])
  (:require [reagent.core :as reagent :refer [atom]]
            [pgtron.layout :as l]
            [pgtron.pg :as pg]
            [cljs.core.async :refer [>! <!]]
            [pgtron.style :refer [style]]))

(def tables-query " SELECT 1 ")


(defn query [db tbl]
  (str "SELECT * FROM pg_stats WHERE "
       "tablename = '" tbl "'"))

(defn table [db tbl]
  (let [state (atom {})]
    (go
      (let [res (<! (pg/exec db (query db tbl)))]
        (swap! state assoc :items res)))
    (fn []
      [:div#table
       (style
        [:.attr {:display "block"
                 :$padding 1
                 :$margin 1
                 :$color [:light-gray :bg-1]}])
       (for [attr (:items @state)]
         [:div.attr {:key (.-attname attr)}
          [:b (.-attname attr)]
          " correlation " (.-correlation attr) "; "
          " average width: " (.-avg_width attr) "; "
          " nulls: " (.-null_frac attr) "; "
          (when-let [cv (.-most-common_vals attr)]
            (str " most common values " cv "; "))
          #_[:pre (.stringify js/JSON attr nil " ")]])])))

(defn $index [{db :db tbl :tbl}]
  [l/layout {:bread-crump [{:title (str db)} {:title (str tbl)}]}
   [:div#database
    (style [:#database {}])
    [table db tbl]]])
