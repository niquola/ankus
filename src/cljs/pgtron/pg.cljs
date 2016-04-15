(ns pgtron.pg
  (:require [reagent.core :as reagent]
            [cljs.nodejs :as node]))

(node/enable-util-print!)

(def pg (node/require "pg"))

(def conn "postgres://crudtest:crudtest@localhost/crudtest")

(defn exec [sql cb]
  (.connect pg conn
            (fn [err cl done]
              (.log js/console err)
              (when (not err)
                (.log js/console sql)
                (.query cl sql nil (fn [err res] (cb (.-rows res)) (.log js/console res)))))))


