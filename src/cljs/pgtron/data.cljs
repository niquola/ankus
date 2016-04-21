(ns pgtron.data
  (:require-macros [cljs.core.async.macros :as m :refer [go alt!]])
  (:require [reagent.core :as reagent :refer [atom]]
            [cljs.core.async :refer [>! <!]]
            [pgtron.pg :as pg]))

(def tables-query "
  SELECT *
        , pg_size_pretty(pg_relation_size(c.oid)) AS size
   FROM pg_tables t
   JOIN pg_class c
     ON c.relname = t.tablename
  JOIN pg_namespace n
    ON n.oid = c.relnamespace AND t.schemaname = n.nspname
  WHERE t.schemaname NOT IN ('information_schema', 'pg_catalog')
  ")

(defn sql [db sql state path]
  (go (let [res (<! (pg/exec db sql))]
        (swap! state assoc-in path res))))

(defn extensions [db state path]
  (go (let [res (<! (pg/exec db "SELECT * FROM pg_extension"))]
      (swap! state assoc-in path res))))
