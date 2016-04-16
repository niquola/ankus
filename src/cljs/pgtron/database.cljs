(ns pgtron.database
  (:require-macros [cljs.core.async.macros :as m :refer [go alt!]])
  (:require [reagent.core :as reagent :refer [atom]]
            [pgtron.layout :as l]
            [pgtron.pg :as pg]
            [cljs.core.async :refer [>! <!]]
            [pgtron.style :refer [style]]))

(defn tables [db]
  (let [state (atom {})]
    (go
      (let [res (<! (pg/exec db "SELECT * FROM pg_tables"))]
        (swap! state assoc :items
               (group-by (fn [x] (.-schemaname x)) res))))
    (fn []
      [:div#tables
       (style
        [:#tables
         [:.tbl {:display "block"
                :$margin [0.2 0 0.2 2]
                :$color [:white :black]
                :$padding [0.25 1]}
          [:h3 [:i {:$color [:gray]}]]
          [:span {:display "inline-block"
                  :$padding 0.5}]]])

       (for [[sch tbls] (:items @state)]
         [:div {:key sch}
          [:h4 sch]
          (for [tbl tbls]
            [:div.tbl {:key (.-tablename tbl)}
             [:a {:href (str  "#/tbl/" (.-tablename tbl))}
              (.-tablename tbl)]
             [:span.right [:a {:href "#/users"} "@" (.-tableowner tbl)]]
             [:pre (.stringify js/JSON tbl)]])])])))

(defn $index [{db :db}]
  [l/layout {:bread-crump [{:title (str "db: " db)}]}
   [:div#database
    (style [:#database {}])
    [tables db]]])
