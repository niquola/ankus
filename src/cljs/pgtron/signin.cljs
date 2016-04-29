(ns pgtron.signin
  (:require-macros [cljs.core.async.macros :as m :refer [go alt!]])
  (:require [reagent.core :as reagent :refer [atom]]
            [pgtron.layout :as l]
            [pgtron.pg :as pg]
            [pgtron.state :as state]
            [cljs.core.async :refer [>! <!]]
            [chloroform.core :as form]
            [charty.core :as chart]
            [pgtron.style :refer [style icon]]))

(defonce state (atom {}))

(defn prepare-connection-string [s]
  (str "postgres://" (first (clojure.string/split s #"/")) "/"))

(defn $index [params]
  (let [form (form/form-cursor state [:auth] {:connection-string "nicola:nicola@localhost:5432/postgres"})
        submit (fn [& args]
                 (let [cs (get-in @form [:data  :connection-string])]
                   (.log js/console "Connection string" cs)
                   (go
                     (let [conn (str "postgres://" cs)
                          _ (.log js/console "Connecting to " conn)
                          res (<! (pg/raw-exec conn "select version() as version"))]
                      (if (:error res)
                        {:errors {:connection-string ["Could not connect to server"]}}
                        (do
                          (.log js/console "Connection OK" res)
                          (swap! state/state merge {:pg-info (.-version (first res))
                                                    :connection-string (prepare-connection-string cs)})
                          (state/redirect "/dashboard")))))))]
   (fn []
     [l/layout {:hide-menu true}
      [:div#signin
       (style [:#signin {:display ""
                         :margin "10% auto"
                         :$width 70
                         :$padding [1 2]
                         :$color [:light-gray :bg-1]}
               [:label {:display "block"}]
               [:button {:width "100%"}]
               [:.connection-string {:$text [1.3 1.6 300]
                                     :width "100%"
                                     :$padding [0.5 1]
                                     :border "2px solid #555"
                                     :$color [:white :bg-2]}]])
       [form/form form {:class "form" :on-submit submit}
        [:$row {:name :connection-string
                :as :string
                :class "connection-string"
                :placeholder "user:password@localhost:5432/postgres"
                :$required true}]
        #_[:pre (pr-str @form)]
        [:button.btn.btn-success.btn-lg {:type "submit" :on-click submit} "Connect"]]]])))
