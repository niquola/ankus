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
