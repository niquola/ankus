(ns pgtron.layout
  (:require [gardner.core :as css]
            [pgtron.state :as state]
            [pgtron.style :refer [style icon] :as st]))

(defn navigation [glob]
  [:div#nav
   (style [:#nav
           {:$color [:gray :bg-1]}
           [:.brand {:display "inline-block"
                     :$text [1 3 :center]
                     :$color :green
                     :$width 6}
            [:.fa {:$text [1.3 2]}]]
           [:.item {:display "inline-block"
                    :$padding 1}]
           [:.signout {:float "right"}]
           [:.logo {:$height 2.5}]])

   [:a.brand {:href "#/dashboard"} [icon :database]]

   (for [x (or (:bread-crump glob) [])]
     [:a.item {:key (:title x)
               :href "#/"}
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


