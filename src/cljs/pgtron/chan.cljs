(ns pgtron.chan
  (:require-macros [cljs.core.async.macros :as am])
  (:require [pgtron.layout :as l]
            [honeysql.core :as hsql]
            [charty.core :as chart]
            [charty.dsl :as dsl]
            [cljs.core.async :as a]
            [clojure.string :as str]
            [pgtron.pg :as pg]
            [reagent.core :as r]
            [pgtron.style :refer [icon style]]))

(defn bind-chan [ch & [cb]]
  (fn [ev] (let [q (.. ev -target -value)]
             (when cb (cb q))
             (am/go (a/>! ch q)))))

(defn bind-query [ch db sql-fn state pth]
  (am/go-loop []
    (let [q (a/<! ch)]
      (pg/query-assoc db (sql-fn q) state pth)) (recur)))

(defn bind-query-first [ch sql-fn state pth]
  (am/go-loop []
    (let [q (a/<! ch)]
      (pg/query-first-assoc "postgres" (sql-fn q) state pth)) (recur)))

(defn debounce [in ms]
  (let [out (a/chan)]
    (am/go-loop [last-val nil]
      (let [val (if (nil? last-val) (<! in) last-val)
            timer (a/timeout ms)
            [new-val ch] (a/alts! [in timer])]
        (condp = ch
          timer (do (a/>! out val) (recur nil))
          in (recur new-val))))
        out))

(defn fmap [in f & args]
  (let [out (a/chan)]
    (am/go-loop []
      (let [q (a/<! in)]
        (a/>! out (apply f q args)))
      (recur))
    out))
