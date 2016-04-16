(ns pgtron.users
  (:require-macros [cljs.core.async.macros :as m :refer [go alt!]])
  (:require [reagent.core :as reagent :refer [atom]]
            [pgtron.layout :as l]
            [pgtron.pg :as pg]
            [cljs.core.async :refer [>! <!]]
            [pgtron.style :refer [style icon]]))

(defn $index [params]
  (let [state (atom {})]
    (pg/query-assoc "postgres"
                    "SELECT * FROM pg_roles"
                    state [:users]
                    identity)
    (fn []
      [l/layout {:bread-crump [{:title "Users" :icon :users}]}
       [:div#users
        (style
         [:#users
          [:.item {:$margin [1 0 0 1]
                   :$padding [0.25 1]
                   :border-bottom "1px solid #111"}
           [:&:hover {:$color [:white :black]}]
           [:.fa {:$color :orange}]
           [:.val {:$text [1.2]
                   :$color :orange}]]])
        (for [x (:users @state)]
          [:div.item {:key (gensym)}
           [:h3 (.-rolname x) " "
            [:span.text-muted " [" (.-oid x) "] "]
            (when (.-rolsuper x) [icon :user])]

           [:p "can: "
            (when (.-rolcreaterole x) "create users, ")
            (when (.-rolcreatedb x) "create db, ")
            (when (.-rolupdate x) "update, ")
            (when (.-rolupdate x) "login, ")
            (when (.-rolreplication x) "replication, ")
            (when-let [stop (.-rolvaliduntil x)] (str "valid until " stop))]
           #_[:pre (.stringify js/JSON x nil " ")]])]])))
