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

(def icons
  {"v" :eye
   "r" :table
   "S" :key
   "f" :facebook
   "i" :search})

(defn get-kind [k]
  (get {:tables "r"
        :views "v"
        :functions "f"
        :indices "i"
        :sequences "S"} k))

(def modifiers
  {"\\t" :tables
   "\\v" :views

   "\\i" :indices
   "\\S" :sequences
   "\\f" :functions})

(defn remove-nils [m]
  (into {} (remove (fn [[k v]] (nil? v)) m)))

(defn where-fn [col ws]
  (let [expr (map (fn [w] [:or
                           [:like col (str "%" w "%")]
                           [:like :nspname (str "%" w "%")]]) ws)]

    (when-not (empty? expr)
      (into [:and] expr))))

(defn mk-query [q]
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

        ;;schems {:select [[:oid :id] [:schema_name :name] [:schema_name :schema] [[:$raw "'s'"] :type]]}

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


(def index-style
  [:#demo {:$padding [2 4]}
   [:.items {:-webkit-columns 3}]
   [:.mod {:display "inline-block"
           :$padding [0 1]
           :$color :gray
           :cursor "pointer"
           :border-radius "5px"
           :$margin [0.5 0.5 1]}
    [:&:hover {:text-decoration "none"}]
    [:&.active {:$color [:white :bg-blue]}]]
   [:.item {:display "block"
            :$color :light-gray
            :text-decoration "none"
            :$padding [0.2 1]}
    [:&:hover {:$color [:white :black]}]
    [:.fa {:display "inline-block" :$width 2 :$color :gray}]
    [:.schema {:$text [0.5 0.7] :$color :gray}]]
   [:input {:$color [:black :white] :width "100%" :display "block"}]])

(defn $index [params]
  (let [model l/model
        q-ch (a/chan)
        handle (ch/bind-chan q-ch (fn [q] (swap! model assoc :search q)))
        set-modifier (fn [m k]
                       (fn [ev]
                         (let [enabled (get-in @model [:query :modifiers m])
                               search (:search @model)
                               search (if enabled
                                        (str/replace search k "")
                                        (str search " " k))]
                           (swap! model (fn [old]
                                          (-> old
                                              (assoc-in [:query :modifiers m] (not enabled))
                                              (assoc-in [:search] search))))
                           (a/put! q-ch search))))
        parsed-ch (ch/fmap (ch/debounce q-ch 400) parse-modifiers)
        query-ch  (ch/fmap parsed-ch (fn [m] (swap! model assoc :query m) m))]

    (ch/bind-query query-ch "sample"  mk-query model [:data])

    (fn []
      (let [data (:data @model)
            query (:query @model)]
        [l/page model
        [:div#demo
         (style index-style)
         [:input {:on-change handle :value (:search @model)}]
         [:div.mods
          (for [[k v] modifiers]
            [:a.mod {:key v
                     :on-click (set-modifier v k)
                     :class (when (get-in query [:modifiers v]) "active")}
             [:strong (name k)] " " (str v)])]
         [:div.items
          (when (empty? data) [:h3 ":( nothing to show ..."])
          (for [i data]
            [:a.item {:key (.-id i) :href "#/demo"}
             (icon (or (get icons (.-type i)) :ups)) " " (.-name i)
             " "

             [:span.schema (.-schema i)]])]
         #_[:pre (.stringify js/JSON data nil " ")]]]))))
