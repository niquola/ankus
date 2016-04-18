(ns pgtron.dashboard
  (:require-macros [cljs.core.async.macros :as m :refer [go alt!]])
  (:require [reagent.core :as reagent :refer [atom]]
            [pgtron.layout :as l]
            [pgtron.pg :as pg]
            [cljs.core.async :refer [>! <!]]
            [pgtron.style :refer [style icon]]))

(defn dbs [state]
  (fn []
    [:div#dbs
     [:a.box {:href "#/new/database"}
      [icon :plus]
      [:h2 "create db"]]
     (for [db (:items @state)]
       [:a.box {:key   (.-datname db)
               :class (when (= true (.-datistemplate db)) "template")
                :href  (str  "#/db/" (.-datname db))}
        [:h2 (.-datname db)]
        [:p.details.text-muted (.-size db)]
        #_(.stringify js/JSON db)])]))

(def dash-styles
  (style
   [:#dash
    {:$padding [0 1]}
    [:h3 {:$color [:txt-muted]}]
    [:.block {:$padding [1 3]
              :$color [:white :bg-1]}]
    [:.section {:$margin [0 0 2 2]}]
    [:.box {:display "inline-block"
            :$padding [1 3]
            :vertical-align "top"
            :$width 18 
            :$height 7 
            :$margin 0.5
            :border-top "6px solid #777"
            :$color [:white :bg-1]}
     [:&:hover
      {:text-decoration "none"
       :border-top "6px solid white"}
      [:.fa {:$color :white}]
      [:h2 {:$color :white}]]
     [:.fa {:$text [3 3 :center]
            :$color :light-gray
            :display "block"}]
     [:h2 {:$text [1 1.5 :center]
           :$color :light-gray
           :$margin [0.5 0]}]
     [:.details {:$text [0.8 1 :center]}]
     [:&.template {:border-top "6px solid #777"}]]]))


(def summary-q "
  SELECT version() as version,
        (SELECT json_agg(x.*) FROM pg_stat_activity x) as connections
")

(defn $index [params]
  (let [state (atom {})]
    (pg/query-assoc "postgres" summary-q state [:summary] identity)
    (pg/query-assoc "postgres"
                    "SELECT *, pg_size_pretty(pg_database_size(datname)) as size FROM pg_database"
                    state [:items]
                    identity)
    (fn []
     [l/layout {}
      [:div#dash
       dash-styles

       [:h3 "Tools:"]
       [:div.section
        [:a.box {:href "#/query"}
         [icon :search]
         [:h2 "Queries"]]

        [:a.box {:href "#/config"}
         [icon :gear]
         [:h2 "Configuration"]]

        [:a.box {:href "#/users"}
         [icon :users]
         [:h2 "Users"]]

        [:a.box {:href "#/backups"}
         [icon :floppy-o]
         [:h2 "Backups"]]

        [:a.box {:href "#/monitoring"}
         [icon :bar-chart]
         [:h2 "System"]]

        [:a.box {:href "#/logs"}
         [icon :list]
         [:h2 "Logs"]]]



       [:h3 "Databases:"]
       [:div.section [dbs state]]

       [:h3 "Connections:"]
       [:div.section
        (if-let [summary (first (:summary @state))]
          [:div
           [:div.block
            (for [con (.-connections  summary)]
              [:div {:key (str (.-datid con) (.-pid con))}
               [:b (.-application_name con) ":" (.-client_addr con) "->"]
               [:b " "(.-usename con)]
               [:b "@" (.-datname con)]
               [:span " [" (.-state con) ": " (.-query_start con) "]"]
               [:p.text-muted "   " (.-query con)]])]
           [:br]
           [:p.text-muted (.-version summary)]])]]])))
