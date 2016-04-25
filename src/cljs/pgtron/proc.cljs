(ns pgtron.proc
  (:require [reagent.core :as reagent :refer [atom]]
            [pgtron.layout :as l]
            [pgtron.pg :as pg]
            [chloroform.core :as form]
            [pgtron.style :refer [style icon]]))

(defn sql [sch pr]
  ;; TODO use schema
  (str "SELECT prosrc as define FROM pg_proc WHERE proname = '" pr "'"))

(defn $index [{db :db sch :schema pr :proc :as params}]
  (let [state (atom {})]
    (pg/query-assoc db (sql sch pr) state [:define]
                    (fn [xs]
                      (when-let [row (first xs)] (.-define row))))
    (fn []
      [l/layout {:params params
                 :bread-crump [{:title [:span (icon :folder-o) " " sch]
                                :href (str "#/db/" db "/schema/" sch)}
                               {:title [:span (icon :facebook) " " pr]}]}
       [:div#proc
        (style [:#proc {:$padding [1 2]}
                [:.CodeMirror {:height "auto"}]])
        [:h3 "Definition"]
        [form/codemirror state [:define] {:theme "railscasts"
                                          :mode "text/x-sql"
                                          :extraKeys {"Ctrl-Enter" println}}]]])))
