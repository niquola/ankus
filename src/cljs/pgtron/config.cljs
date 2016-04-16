(ns pgtron.config
  (:require-macros [cljs.core.async.macros :as m :refer [go alt!]])
  (:require [reagent.core :as reagent :refer [atom]]
            [pgtron.layout :as l]
            [pgtron.pg :as pg]
            [cljs.core.async :refer [>! <!]]
            [pgtron.style :refer [style]]))

(defn $index [params]
  (let [state (atom {})]
    (pg/query-assoc "postgres"
                    "SELECT * FROM pg_settings"
                    state [:config]
                    (fn [res] (group-by #(.-category %) res)))
    (fn []
      [l/layout {:bread-crump [{:title "Config" :icon :gear}]}
       [:div#config
        (style
         [:#config
          [:.cat {:$margin [1 0]
                  :$text [1 2 600]
                  :border-bottom "1px solid #555"}]
          [:.item {:$margin [1 0 0 1]
                   :$padding [0.25 1]
                   :border-bottom "1px solid #111"}
           [:&:hover {:$color [:white :black]}]
           [:.val {:$text [1.2]
                   :$color :orange}]
           ]])
        (for [[cat kvs] (:config @state)]
          [:div {:key cat}
           [:h4.cat cat]
           (for [kv kvs]
             [:div.item {:key (.-name kv)}
              [:b (.-name kv) ": "] [:b.val (.-setting kv)]
              " "
              [:p.text-muted (.-short_desc kv)]
              #_[:pre (.stringify js/JSON kv nil " ")]])])]])))
