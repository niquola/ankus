(ns pgtron.query
  (:require-macros [cljs.core.async.macros :as m :refer [go alt!]])
  (:require [reagent.core :as reagent :refer [atom]]
            [pgtron.layout :as l]
            [pgtron.pg :as pg]
            [cljs.core.async :refer [>! <!]]
            [pgtron.style :refer [style icon]]))

(defn $index [params]
  (let [state (atom {})
        handle (fn [ev]
                 (when (and (.-ctrlKey ev)
                            (= 13 (.-which ev)))
                   (.log js/console (.. ev -target -value) ev)
                   (pg/query-assoc "postgres"
                                   (.. ev -target -value)
                                   state [:result]
                                   identity)))]
    (fn []
      [l/layout {:bread-crump [{:title "Query" :icon :search}]}
       [:div#query
        (style
         [:#query
          [:textarea.form-control
           {:$color [:white :bg-0]
            :$padding 1
            :$text [1.1 1.5]}]])
        [:textarea.form-control {:on-key-down handle}]
        [:div.results
         (for [row (:result @state)]
           [:div {:key (gensym)}
            [:pre (.stringify js/JSON row nil " ")]])]]])))
