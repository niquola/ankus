(ns pgtron.pg
  (:require-macros [cljs.core.async.macros :as m :refer [go alt!]])
  (:require [reagent.core :as reagent]
            [cljs.core.async :as async]
            [cljs.nodejs :as node]))

(node/enable-util-print!)

(def pg (node/require "pg"))
(def Client (.-Client pg))

(def conn "postgres://crudtest:crudtest@localhost/")

(defn exec [db sql]
  (let [ch (async/chan)
        cl (Client. (str conn db))]
        (.connect
         cl
         (fn [err]
           (when err (.log js/console "Error" err))
           (when (not err)
             (.log js/console sql)
             (.query cl sql
                     (fn [err res]
                       (if err
                         (.error js/console err)
                         (async/put! ch (.-rows res)))
                       (.end cl))))))
        ch))

(defn query-assoc [db sql state path proc]
  (go
    (let [res (<! (exec db sql))]
      (swap! state assoc-in path (proc res)))))


