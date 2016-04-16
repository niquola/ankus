(ns pgtron.query
  (:require-macros [cljs.core.async.macros :as m :refer [go alt!]])
  (:require [reagent.core :as reagent :refer [atom]]
            [pgtron.layout :as l]
            [pgtron.pg :as pg]
            [cljs.core.async :refer [>! <!]]
            [chloroform.core :as form]
            [pgtron.style :refer [style icon]]))

(defn $index [params]
  (let [state (atom {})
        handle (fn [ev]
                 (let [sql (:sql @state)]
                   (pg/query-assoc "postgres" sql
                                   state [:result]
                                   identity)))]
    (fn []
      [l/layout {:bread-crump [{:title "Query" :icon :search}]}
       [:div#query
        (style
         [:#query
          [:textarea
           {:$color [:white :bg-0]
            :$padding 1
            :$height 4
            :$text [1.1 1.5]}]])
        #_[:textarea.form-control {:on-key-down handle}]
        [form/codemirror state [:sql] {:theme "railscasts"
                                       :mode "text/x-sql"
                                       :extraKeys {"Ctrl-Enter" handle}}]
        [:div.results
         (for [row (:result @state)]
           [:div {:key (gensym)}
            [:pre (.stringify js/JSON row nil " ")]])]]])))
