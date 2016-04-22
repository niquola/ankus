(ns pgtron.dashboard
  (:require-macros [cljs.core.async.macros :as m :refer [go alt!]])
  (:require [reagent.core :as reagent :refer [atom]]
            [pgtron.layout :as l]
            [pgtron.pg :as pg]
            [charty.core :as chart]
            [cljs.core.async :refer [>! <!]]
            [pgtron.style :refer [style icon]]))

(defn dbs [state]
  (fn []
    [:div#dbs
     (style [:#dbs  [:.box.new {:$color :green}
                     [:.fa {:$color :green}]]])
     [:a.box.new {:href "#/new/database"}
      [icon :plus]
      [:h2 "create db"]]

     (for [db (:items @state)]
       [:a.box {:key   (.-datname db)
                :class (when (= true (.-datistemplate db)) "template")
                :href  (str  "#/db/" (.-datname db))}
        [:h2 (.-datname db)]
        [:p.details.text-muted (.-size db)]
        #_(.stringify js/JSON db)])]))

(defn pie [state]
  [chart/pie {:width 400 :height 220}
   (map (fn [x] {:label (.-datname x) :value (.-rawsize x)}) (:items @state))])

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
     [:.fa {:$text [2.5 2.5 :center]
            :$color :light-gray
            :display "block"}]
     [:h2 {:$text [1 1.5 :center]
           :$color :light-gray
           :$margin [0.5 0]}]
     [:.details {:$text [0.8 1 :center]}]
     [:&.template {:border-top "6px solid #777"}]]]))

(defn tools []
  [:div
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
    [:h2 "Logs"]]])

(def connections-sql "SELECT * FROM pg_stat_activity")
(def dbs-sql "SELECT *,pg_size_pretty(pg_database_size(datname)) as size,
              pg_database_size(datname) as rawsize
              FROM pg_database
              WHERE datistemplate = false")

(defn summary [state]
  [:div#summary
   (style
    [:#summary
     [:.card {:$color [:white :bg-1]
              :$padding [1 2]
              :$height 14
              :$margin [0 1]}]
     [:.pie {:$width 50 :display "inline-block"}]
     [:.metrics {:$width 50 :display "inline-block"}]
     [:.metric {:display "inline-block"
                :$color :gray
                :$margin [0 2 0 0]}
      [:.value {:$color :orange
                :margin 0
                :$text [3 3 :center]}]]])
   [:div.pie [pie state]]
   [:div.metrics
    [:div.metric
     [:h3.value (count (:connections @state))]
     [:p "Connections "]]
    [:div.metric
     [:h3.value (count (:items @state))]
     [:p "Databases"]]]])

(defn $index [params]
  (let [state (atom {})]
    (pg/query-assoc "postgres" connections-sql state [:connections] identity)
    (pg/query-assoc "postgres" dbs-sql state [:items] identity)
    (fn []
     [l/layout {}
      [:div#dash
       dash-styles

       [:h3 "Summary"]
       [:div.section [summary state]]

       [:h3 "Tools"]
       [:div.section [tools]]

       [:h3 "Databases:"]
       [:div.section [dbs state]]]])))
