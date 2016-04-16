(ns pgtron.pg
  (:require-macros [cljs.core.async.macros :as m :refer [go alt!]])
  (:require [reagent.core :as reagent]
            [cljs.core.async :as async]
            [cljs.nodejs :as node]))

(node/enable-util-print!)

(def pg (node/require "pg"))

(def conn "postgres://crudtest:crudtest@localhost/")

(defn exec [db sql]
  (let [ch (async/chan)]
    (.connect pg (str conn db)
              (fn [err cl done]
                (when err (.error js/console err))
                (when (not err)
                  #_(.log js/console sql)
                  (.query cl sql nil
                          (fn [err res]
                            (if err
                              (.error js/console err)
                              (async/put! ch (.-rows res))))))))
    ch))

(defn query-assoc [db sql state path proc]
  (go
    (let [res (<! (exec db sql))]
      (swap! state assoc-in path (proc res)))))


