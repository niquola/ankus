(ns pgtron.demo
  (:require-macros [cljs.core.async.macros :as am])
  (:require [pgtron.layout :as l]
            [charty.core :as chart]
            [pgtron.chan :as ch]
            [charty.dsl :as dsl]
            [cljs.core.async :as a]
            [clojure.string :as str]
            [pgtron.pg :as pg]
            [reagent.core :as r]
            [pgtron.style :refer [icon style]]))

(defn agg-limit [hsql]
  {:select [[:$call :json_agg :x.*]]
   :from [[(assoc hsql :limit 30) :x]]})

(defn get-kind [k]
  (get {:tables "r"
        :views "v"
        :functions "f"
        :indices "i"
        :sequences "S"} k))

(defn remove-nils [m]
  (into {} (remove (fn [[k v]] (nil? v)) m)))

(defn where-fn [col ws]
  (let [expr (map (fn [w] [:or
                           [:like col (str "%" w "%")]
                           [:like :nspname (str "%" w "%")]]) ws)]

    (when-not (empty? expr)
      (into [:and] expr))))

(defn mk-query [q]
  (println "Query" q)
  (let [mods (:modifiers q)

        ws (:words q)

        rels (-> {:select [[:c.oid::text :id] [:c.relname :name] [:n.nspname :schema] [:c.relkind :type]]
                  :from [[:pg_class :c]]
                  :where (where-fn :c.relname ws)
                  :join [[:pg_namespace :n] [:= :n.oid :c.relnamespace]]
                  :order-by [:relname]}
                 remove-nils)

        procs (-> {:select [[:p.oid::text :id] [:p.proname :name] [:n.nspname :schema] [[:$raw "'function'"] :type]]
                   :from [[:pg_proc :p]]
                   :join [[:pg_namespace :n] [:= :n.oid :p.pronamespace]]
                   :where (where-fn :p.proname ws)
                   :order-by [:proname]}
                  remove-nils)

        kinds (reduce (fn [acc [k v]]
                        (if-let [f (get-kind k)]
                          (conj acc f)
                          acc)) [] mods)
        sql {:select [:x.*]
             :from [[{:union [rels procs]} :x]]
             :order-by [:x.name]
             :limit 30}]
    (if (empty? kinds)
      sql
      (assoc sql :where [:in :x.type kinds]))))

(def modifiers
  {"\\t" :tables
   "\\v" :views

   "\\i" :indices
   "\\S" :sequences
   "\\f" :functions})

(defn modifier? [x]
  (get modifiers x))

(defn parse-modifiers [q]
  (let [ws (filter identity (str/split q #"\s+"))]
    (reduce
     (fn [acc w]
       (if-let [mod (modifier? w)]
         (update-in acc [:modifiers] assoc mod true)
         (update-in acc [:words] conj w))
       ) {:words []} ws)))

(def icons
  {"v" :eye
   "r" :table
   "S" :key
   "f" :facebook
   "i" :search})

(defn $index [params]
  (let [model l/model
        q-ch (a/chan)
        handle (ch/bind-chan q-ch)
        parsed-ch (ch/fmap (ch/debounce q-ch 400) parse-modifiers)
        query-ch  (ch/fmap parsed-ch (fn [m] (swap! model assoc :query m) m))]

    #_(am/go-loop []
      (println "Consume" (a/<! query-ch))
      (recur))

    (ch/bind-query query-ch "sample"  mk-query model [:data])

    (fn []
      (let [data (:data @model)
            query (:query @model)]
        [l/page model
        [:div#demo
         (style [:#demo {:$padding [2 4]}
                 [:.items {:-webkit-columns 3}]
                 [:.mod {:display "inline-block"
                         :$padding [0 1]
                         :border-radius "5px"
                         :$margin [0.3 0 0.4]}
                  [:&.active {:$color [:white :blue]}]]
                 [:.item {:display "block"
                          :$color :light-gray
                          :text-decoration "none"
                          :$padding [0.2 1]}
                  [:&:hover {:$color [:white :black]}]
                  [:.fa {:display "inline-block" :$width 2 :$color :gray}]
                  [:.schema {:$text [0.5 0.7] :$color :gray}]]
                 [:input {:$color [:black :white] :width "100%" :display "block"}]])
         [:input {:on-change handle}]
         [:div.mods
          (for [[k v] modifiers]
            [:a.mod {:key v
                     :class (when (get-in query [:modifiers v]) "active")}
             [:strong (name k)] " " (str v)])]
         [:br]
         [:div.items
          (for [i data]
            [:a.item {:key (.-id i) :href "#/demo"}
             (icon (or (get icons (.-type i)) :ups)) " " (.-name i)
             " "
             [:span.schema (.-schema i)]])]
         
         #_[:pre (.stringify js/JSON data nil " ")]]]))))
