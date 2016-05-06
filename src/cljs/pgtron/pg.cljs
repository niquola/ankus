(ns pgtron.pg
  (:require-macros [cljs.core.async.macros :as m :refer [go alt!]])
  (:require [reagent.core :as reagent]
            [cljs.core.async :as async]
            [clojure.walk :as walk]
            [honeysql.core :as hsql]
            [pgtron.state :as state]
            [cljs.nodejs :as node]))

(node/enable-util-print!)

(def pg (node/require "pg"))
(def Client (.-Client pg))

(def conn "postgres://crudtest:crudtest@localhost/")

(def hsql-macros
  {:$call hsql/call
   :$raw hsql/raw})

(defn honey-macro [hsql]
  (walk/postwalk
   (fn [x]
     (if-let [macro (and (vector? x) (get hsql-macros (first x)))] 
       (apply macro (rest x))
       x))
   hsql))

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


(defn exec [sql]
  (let [cs (:connection-string  @(state/current-tab))
        _ (println "Connect on " cs)
        sql (if (map? sql) (hsql/format (honey-macro sql) :parameterizer :postgresql) [sql])
        ch (async/chan)
        cl (Client. (str cs))]
    (println "SQL:" sql)
    (.connect
     cl
     (fn [err]
       (when err
         (.log js/console "Error" err))
       (when (not err)
         (.log js/console sql)
         (.query cl (first sql) (clj->js (rest sql))
                 (fn [err res]
                   (if err
                     (.error js/console err)
                     (async/put! ch (.-rows res)))
                   (.end cl))))))
    ch))

(defn query-assoc [sql state path & [proc]]
  (let [proc (or proc identity)]
    (go
      (let [res (<! (exec sql))]
        (swap! state assoc-in path (proc res))))))

(defn query-first-assoc [sql state path & [proc]]
  (let [proc (or proc identity)]
    (go
      (let [res (<! (exec sql))]
        (swap! state assoc-in path (proc (first res)))))))

