(ns pgtron.layout
  (:require [gardner.core :as css]
            [pgtron.state :as state]
            [pgtron.style :refer [style icon] :as st]
            [reagent.core :as r]))

(defn navigation [glob]
  [:div#nav
   (style [:#nav
           {:$color [:gray :bg-1]}
           [:.brand {:display "inline-block"
                     :$text [1 3 :center]
                     :$color :green
                     :$width 6}
            [:.fa {:$text [1.3 2]}]]
           [:.db {:$text [1 2]
                  :$padding [0.25 1]
                  :$color [:light-gray :bg-0]
                  :border-radius "3px"
                  :cursor "pointer"
                  :border "1px solid #333"}
            [:.fa {:$text [0.8]}]
            [:&:hover {:text-decoration "none";
                       :$color :white}]]
           [:.item {:display "inline-block"
                    :$padding 1}
            [:&:hover {:text-decoration "none"}]
            [:&.current {:$color :light-gray}]]
           [:.signout {:float "right"}]
           [:.logo {:$height 2.5}]])

   [:a.brand {:href "#/dashboard"} [icon :database]]

   (when-let [db (get-in glob [:params :db])]
     [:a.db {:href (str "#/db/" db)} db " "(icon :chevron-down)])
   (for [x (or (:bread-crump glob) [])]
     [:a.item {:key (:title x) :href (:href x) :class (when-not (:href x) "current")}
      (when-let [ic (:icon x)] (icon ic)) " " (:title x)])
   [:a.item.signout {:href "#/"} "Sign Out"]])

(defn footer [glob]
  [:div#footer
   (style [:#footer {:$text [0.8 2] 
                     :$padding [0 1]
                     :$color [:dark-gray :bg-1]
                     :box-shadow "0 -1px -3px #090909, 0 -1px -2px #111"
                     :$absolute [nil 0 0 0]}])
   [:span.text-muted (:connection-string @state/state) " | " (:pg-info @state/state)]])

(defn layout [glob cnt]
  [:div#layout
   (st/main-style)
   (style
    [:#layout {:position "absolute" :top 0 :left 0 :right 0 :bottom 0 :overflow "hidden"}
     [:#nav {:position "absolute"
             :top 0 :left 0 :right 0 :$height 3
             :box-shadow "0 1px 3px #090909, 0 1px 2px #111"
             :z-index 1000}]
     [:#center {:position "absolute"
                :$padding 0 ;;[1 4 1 6]
                :$top 3 :left 0 :right 0 :$bottom 2
                :overflow-x "auto"}]
     [:#footer {:position "absolute" :$height 2 :left 0 :right 0 :bottom 0}]])
   (when-not (:hide-menu glob)
     [navigation glob])
   [:div#center cnt]
   [footer glob]])


(def page-style
  (let [nav-height 3
        sub-nav-width 6
        footer-height 2]
    [:#page {:position "absolute" :top 0 :left 0 :right 0 :bottom 0 :overflow "hidden"}
     [:#nav {:position "absolute"
             :top 0 :left 0 :right 0 :$height nav-height
             :box-shadow "0 1px 3px #090909, 0 1px 2px #111"
             :z-index 1000}]
     [:#sub-nav {:position "absolute"
                 :$top nav-height
                 :z-index 2
                 :left 0
                 :$bottom footer-height
                 :$width sub-nav-width}]
     [:#center {:position "absolute"
                :$padding 0
                :$top nav-height
                :$left sub-nav-width
                :right 0
                :$bottom footer-height
                :overflow-x "auto"}]
     [:#footer {:position "absolute"
                :$height footer-height
                :z-index 4
                :left 0
                :right 0
                :bottom 0}]]))

(defn nav [model]
  [:div#nav
   (style [:#nav
           {:$color [:gray :bg-1]}
           [:.brand {:display "inline-block"
                     :$text [1 3 :center]
                     :$color [:green :black]
                     :$width 6}
            [:.fa {:$text [1.3 2]}]]
           [:.db {:$text [1 2]
                  :$padding [0.25 1]
                  :$color [:light-gray :bg-0]
                  :border-radius "3px"
                  :cursor "pointer"
                  :border "1px solid #333"}
            [:.fa {:$text [0.8]}]
            [:&:hover {:text-decoration "none";
                       :$color :white}]]
           [:.item {:display "inline-block"
                    :$padding 1}
            [:&:hover {:text-decoration "none"}]
            [:&.current {:$color :light-gray}]]
           [:.signout {:float "right"}]
           [:.logo {:$height 2.5}]])

   [:a.brand {:href "#/dashboard"} [icon :database]]
   (for [x @model]
     [:a.item {:key (:title x) :href (:href x) :class (when-not (:href x) "current")}
      (when-let [ic (:icon x)] (icon ic)) " " (:title x)])])

(defn sub-nav [model]
  [:div#sub-nav
   (style [:#sub-nav {:$color [:gray :bg-1]
                      :$padding [1 0]
                      :box-shadow "0 1px 3px #090909, 0 1px 2px #111"}
           [:.item {:display "block"
                    :$color :light-gray
                    :$padding [0.5 0]
                    :$widht 3}
            [:&:hover {:color :white}]
            [:.fa {:display "block" :$text [1.8 2 :center] :margin 0}]
            [:h2 {:$text [0.5 0.8 :center] :margin 0}]
            [:&:hover {:text-decoration "none"}]
            [:&.current {:$color :light-gray}]]])

   (for [i @model]
     [:a.item {:key (:title i) :href (:href i)}
      [icon (:icon i)]
      [:h2 (:title i)]])])

(def model
  (r/atom {:navigation [{:title "MyDb" :icon "database" :href "#/demo"}
                        {:title "MySchema" :icon "folder-o" :href "#/demo"}
                        {:title "MyTable" :icon "table" :href "#/demo"}]
           :popup []
           :sub-navigation [{:icon "search" :title "Queryes" :href "#/demo"}
                            {:icon "gear" :title "Config" :href "#/demo"}
                            {:icon "users" :title "Users" :href "#/demo"}
                            {:icon "floppy-o" :title "Backups" :href "#/demo"}
                            {:icon "bar-chart" :title "Monitoring" :href "#/demo"}]
           :status-line []}))

(defn page [model cnt]
  [:div#page
   (st/main-style)
   [nav (r/cursor model [:navigation])]
   [sub-nav (r/cursor model [:sub-navigation])]
   (style page-style)
   [:div#center cnt]
   [footer (r/cursor model [:footer])]])
