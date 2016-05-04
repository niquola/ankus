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
   :from   [[(assoc hsql :limit 30) :x]]})

(def filters
  {:tables    {:kind "r"
               :modifier "\\t"
               :icon :table}
   :schemata  {:kind "s"
               :modifier "\\s"
               :icon :folder-o}
   :views     {:kind "v"
               :modifier "\\v"
               :icon :eye}
   :functions {:kind "f"
               :modifier "\\f"
               :icon :facebook}
   :indices   {:kind "i"
               :modifier "\\i"
               :icon :search}
   :sequences {:kind "S"
               :modifier "\\S"
               :icon :key}})

(def icons
  (reduce
   (fn [acc [k v]] (assoc acc (:kind v) (:icon v)))
   {} filters))

(defn get-kind [k] (get-in filters [k :kind]))

(def modifiers
  (reduce
   (fn [acc [k v]] (assoc acc (:modifier v) k))
   {} filters))

(defn remove-nils [m]
  (into {} (remove (fn [[k v]] (nil? v)) m)))

(defn where-fn [col ws]
  (let [expr (map
              (fn [w]
                (if (= col :nspname)
                  [:like :nspname (str "%" w "%")]
                  [:or
                   [:like col (str "%" w "%")]
                   [:like :nspname (str "%" w "%")]])) ws)]

    (when-not (empty? expr)
      (into [:and] expr))))

(defn mk-query [q]
  (let [mods (:modifiers q)

        ws (:words q)

        rels {:select [[:c.oid::text :id]
                       [:c.relname :name]
                       [:n.nspname :schema]
                       [:c.relkind :type]]
              :from   [[:pg_class :c]]
              :where  (where-fn :c.relname ws)
              :join   [[:pg_namespace :n] [:= :n.oid :c.relnamespace]]
              :order-by [:relname]}

        procs {:select [[:p.oid::text :id]
                        [:p.proname :name]
                        [:n.nspname :schema]
                        [[:$raw "'function'"] :type]]
               :from [[:pg_proc :p]]
               :join [[:pg_namespace :n] [:= :n.oid :p.pronamespace]]
               :where (where-fn :p.proname ws)
               :order-by [:proname]}

        schems {:select [[:nspname :id]
                         [:nspname :name]
                         [:nspname :schema]
                         [[:$raw "'s'"] :type]]
                :from [:pg_namespace]
                :where (where-fn :nspname ws)
                :order-by [:nspname]}

        kinds (reduce (fn [acc [k v]] (if-let [f (get-kind k)] (conj acc f) acc)) [] mods)

        sql {:select [:x.*]
             :from [[{:union (map remove-nils [rels procs schems])} :x]]
             :order-by [:x.name]
             :limit 50}]

    {:select [[:$raw "row_number() over()"]  :y.*]
     :from [[(if (empty? kinds)
               sql
               (assoc sql :where [:in :x.type kinds])) :y]]}))

(defn modifier? [x] (get modifiers x))

(defn parse-modifiers [q]
  (let [ws (filter identity (str/split q #"\s+"))]
    (reduce
     (fn [acc w]
       (if-let [mod (modifier? w)]
         (update-in acc [:modifiers] assoc mod true)
         (update-in acc [:words] conj w)))
     {:words []} ws)))


(def index-style
  [:#demo {:$padding [2 4]}
   [:.items #_{:-webkit-columns 3}]
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
            :border-radius "4px"
            :overflow "hidden"
            :$padding [0.2 1]}
    [:&.selected {:$color [:white :bg-blue]}]
    [:.title {:display "block" :$margin [0 0 0 2.7]}]
    [:&:hover {:$color [:white :black]}]
    [:.fa {:display "inline-block"
           :$width 2.5
           :$margin [0.3 0]
           :float "left"}]
    [:.schema {:$text [0.7 0.7]}]]
   [:input {:$color [:black :white] :width "100%" :display "block"}]])


(defn $index [params]
  (let [model l/model
        input-ch (a/chan)
        handle (ch/bind-chan input-ch (fn [q]
                                        (swap! model assoc :selection 1)
                                        (swap! model assoc :search q)))
        navigate (fn [ev]
                   (println (.-which ev))
                   (swap! model update-in [:selection]
                          (fn [old]
                            (if old
                              (cond (= 38 (.-which ev)) (- old 1)
                                    (= 40 (.-which ev)) (+ old 1)
                                    :else old)
                              1))))
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
                           (a/put! input-ch search))))
        parsed-ch (ch/fmap (ch/debounce input-ch 400) parse-modifiers)
        query-ch  (ch/fmap parsed-ch (fn [m] (swap! model assoc :query m) m))]

    (ch/bind-query query-ch "sample"  mk-query model [:data])

    (fn []
      (let [data (:data @model)
            selection (:selection @model)
            query (:query @model)]
        [l/page model
        [:div#demo
         (style index-style)
         [:input {:on-change handle :value (:search @model) :on-key-down navigate}]
         [:div.mods
          (for [[k v] modifiers]
            [:a.mod {:key v
                     :on-click (set-modifier v k)
                     :class (when (get-in query [:modifiers v]) "active")}
             [:strong (name k)] " " (str v)])]
         [:div.items
          (when (empty? data) [:h3 ":( nothing to show ..."])
          (for [i data]
            [:a.item {:key (.-id i) :href "#/demo" :class (when (= (.-row_number i) (str selection)) "selected")}
             (icon (or (get icons (.-type i)) :ups))
             [:div.title
              (.-name i)
              " "
              [:span.schema (.-schema i)]]])]
         #_[:pre (.stringify js/JSON data nil " ")]]]))))
