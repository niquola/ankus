(ns pgtron.widgets
  (:require [pgtron.style :refer [style icon]]))

(defn table [data]
  [:div.data
   (style
    [:.data {:$color [:white :bg-1]
             :$text [0.8]
             :vertical-align "top"
             :clear "both"
             :$margin [1 0]
             :$padding [1 2]}
     [:.columns {:$color [:white :bg-1]
                 :vertical-align "top"
                 :$margin [0 1 1 0]
                 :float "left"
                 :$padding [1 2]
                 :display "inline-block"}
      [:p.notes {:$margin [1 0 0 0]
                 :width "50em"
                 :$text [0.8]}
       [:b {:$color :orange}]]
      [:td.num {:text-align "right" :$color :blue}]
      [:th {:$color :gray}]
      [:.type {:$color :green}]
      [:.attr {:display "block"
               :$padding 0.1}]]])
   (let [rows data
         one (first rows)
         keys (and one (.keys js/Object one))]
     [:table.table-condensed
      [:thead
       [:tr
        (for [k keys] [:th {:key k} k])]]
      [:tbody
       (for [row data]
         [:tr {:key (.stringify js/JSON row)}
          (for [k keys]
            [:td.value {:key k :title k}
             (let [value (.stringify js/JSON (aget row k) nil " ")]
               (if (< (.-length value) 100)
                 value
                 (str (.substring value 0 100) "...")))])])]])])

(defn tooltip [title content]
  [:span.tt
   (style [:.tt {:display "inline-block"
                 :margin "0 0.5em"
                 :border-radius "50%"
                 :width "20px"
                 :height "20px"
                 :line-height "20px"
                 :cursor "pointer"
                 :font-size "14px"
                 :position "relative"
                 :text-align "center"
                 :$color [:white :green]}
           [:.tt-content
            {:position "absolute"
             :display "none"
             :box-shadow "0 0 4px gray"
             :border-radius "4px"
             :width "40em"
             :text-align "left"
             :$padding [1 2]
             :$color [:white :bg-0]
             :left "-30px"
             :top "30px"}]
           [:&:hover {:background-color "green"}
            [:.tt-content {:display "block"}]]])
   (icon :question)
   [:div.tt-content content]])
