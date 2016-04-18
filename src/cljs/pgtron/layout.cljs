(ns pgtron.layout
  (:require [gardner.core :as css]
            [pgtron.state :as state]
            [pgtron.style :refer [style icon] :as st]))

(defn navigation [glob]
  [:div#nav
   (style [:#nav
           {:$color [:gray :bg-1]}
           [:.brand {:$margin 0.5
                     :display "inline-block"
                     :padding "10px 16px"
                     :$color [:green :black]
                     :text-align "center"
                     :border-radius "50%"}
            [:.fa {:$text 1.3}]]
           [:.item {:display "inline-block"
                    :$padding 1}]
           [:.signout {:float "right"}]
           [:.logo {:$height 2.5}]])

   [:a.brand {:href "#/dashboard"}
    [icon :database]]
   [:span.text-muted (:connection-string @state/state)]
   (for [x (or (:bread-crump glob) [])]
     [:a.item {:key (:title x)
               :href "#/"}
      (when-let [ic (:icon x)] (icon ic)) " " (:title x)])
   [:a.item.signout {:href "#/"} "Sign Out"]
   ])

(defn footer []
  [:div#footer
   (style [:#footer {:$padding 1
                     :$color [:gray :bg-1]
                     :border-top "1px solid #262626"
                     :$absolute [nil 0 0 0]}])
   "footer"])

(defn layout [glob cnt]
  [:div#layout
   (st/main-style)
   (when-not (:hide-menu glob)
     [navigation glob])
   (style [:#center {:$padding 1}])
   [:div#center cnt]])


