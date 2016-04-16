(ns pgtron.style
  (:require [gardner.core :as css]))


(css/config
 {:colors {:white "white"
           :bg-0 "#161616"
           :violet "#993DC7"
           :gray "gray"
           :light-gray "#A2A2A2"
           :black "#222"
           :green "#7EB26D"
           :blue "#6ED0E0"
           :red "#E24D42"
           :txt-gold "#a47e3c"
           :txt-muted "#555"
           :gold "#262626"
           :brd-blue "#33b5e5"
           :orange "#EAB839"
           :bg-1 "#262626"}
  :vars {:h 10
         :v 18}})

(defn style [g]
  [:style (css/css g)])

(defn icon [nm]
  [:i.fa {:class (str "fa-" (name nm))}])

(def scroll-style
  [[(keyword "::-webkit-scrollbar")
    {:width "12px"
     :background-color  "transparent"
     :border-top  "10px solid transparent" }]
   [(keyword "::-webkit-scrollbar:hover")
    {:background-color "rgba(0, 0, 0, 0.5)"} ]
   [(keyword "::-webkit-scrollbar-thumb:horizontal")
    {:background "rgba(0,0,0,0.2)"
     :border-radius "100px"
     :background-clip "padding-box"
     :border "2px solid transparent"
     :min-width "10px" }]
   [(keyword "::-webkit-scrollbar-thumb:horizontal:active")
    { :-webkit-border-radius "100px"}]
   [(keyword "::-webkit-scrollbar-thumb:vertical")
    {:background "#666" ;;"rgba(0,0,0,0.2)"
     :border-radius "100px"
     :background-clip "padding-box"
     :border "2px solid transparent"
     :min-height "10px" }]
   [(keyword "::-webkit-scrollbar-thumb:vertical:active")
        { :-webkit-border-radius "100px"}]])

(defn main-style []
  (style [[:body {:font-family "\"Helvetica Neue\",Helvetica,Arial,sans-serif"
                  :$text [1 1.5]
                  :$color [:white :bg-0]}
           [:a {:$color [:blue]}
            [:&:hover {:$color [:blue]}]]]
          scroll-style]))
