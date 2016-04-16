(ns pgtron.database
  (:require-macros [cljs.core.async.macros :as m :refer [go alt!]])
  (:require [reagent.core :as reagent :refer [atom]]
            [pgtron.layout :as l]
            [pgtron.pg :as pg]
            [cljs.core.async :refer [>! <!]]
            [pgtron.style :refer [style]]))

(defn $index [{db :db}]
  (let [state (atom {})]
    (go
      (let [res (<! (pg/exec db "SELECT * FROM pg_tables"))]
        (swap! state assoc :items
               (group-by (fn [x] (.-schemaname x)) res))))
    (fn []
      [l/layout {}
       [:div#dbs
        (style
         [:#dbs
          [:.db {:display "block"
                 :$margin [0.2 0 0.2 2]
                 :$color [:white :black]
                 :$padding [0.25 1]
                 :position "relative"
                 :border-left "3px solid #77b300"}
           [:.right {:position "absolute"
                     :right 0 :top 0}]
           [:h3 [:i {:$color [:gray]}]]
           [:span {:display "inline-block"
                   :$padding 0.5}]]])

        [:h1 "Database: " db]

        (for [[sch tbls] (:items @state)]
          [:div {:key sch}
           [:h4 sch]
           (for [db tbls]
             [:div.db {:key (.-tablename db) :href (str  "#/db/" (.-tablename db))}
              [:a {:href "#/"} [:h3 (.-tablename db)]]
              [:span.right [:a {:href "#/"} "@" (.-tableowner db)]]
              #_[:pre (.stringify js/JSON db)]])])]])))
