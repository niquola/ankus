(ns pgtron.pg
  (:require-macros [cljs.core.async.macros :as m :refer [go alt!]])
  (:require [reagent.core :as reagent]
            [cljs.core.async :as async]
            [pgtron.state :as state]
            [cljs.nodejs :as node]))

(node/enable-util-print!)

(def pg (node/require "pg"))
(def Client (.-Client pg))

(def conn "postgres://crudtest:crudtest@localhost/")

(defn raw-exec [conn sql]
  (let [ch (async/chan)
        cl (Client. conn)]
    (.connect
     cl
     (fn [err]
       (when err (async/put! ch {:error err}) (.log js/console "Error" err))
       (if (not err)
         (.query cl sql
                 (fn [err res]
                   (if err
                     (.error js/console err)
                     (async/put! ch (.-rows res)))
                   (.end cl)))
         (do (async/put! ch {:error err}) (.log js/console "Error" err)))))
    ch))

(defn exec [db sql]
  (.log js/console "EXEC:" (:connection-string @state/state))
  (let [ch (async/chan)
        cl (Client. (str (:connection-string @state/state) db))]
        (.connect
         cl
         (fn [err]
           (when err
             (.log js/console "Error" err))
           (when (not err)
             (.log js/console sql)
             (.query cl sql
                     (fn [err res]
                       (if err
                         (.error js/console err)
                         (async/put! ch (.-rows res)))
                       (.end cl))))))
        ch))

(defn query-assoc [db sql state path & [proc]]
  (let [proc (or proc identity)]
    (go
      (let [res (<! (exec db sql))]
        (swap! state assoc-in path (proc res))))))


