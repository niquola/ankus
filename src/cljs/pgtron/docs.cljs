(ns pgtron.docs
  (:require [clojure.string :as str]))

(def fs (js/require "fs"))

(def styles
  [:.docs
   [:para {:display "block" :$margin [2 0]}]
   [:row {:display "table-row"
          :$text [0.9 1]}
    [:entry {:display "table-cell"
             :padding "5px"}]]
   [:indexterm {:display "none"}]
   [:refsect1 {:display "block" :$margin [2 0]}]
   [:title {:display "block"
            :$text [1.3 2 :bold]
            :$margin [1 0]}]
   [:refmeta {:display "block" :margin [1 0]}]
   [:programlisting {:font-weight "bold"}]
   [:varlistentry {:clear "both" :display "block"}
    [:term {:$margin [1 0 0 0]
            :width "auto"
            :display "block"
            :$text [1 1.5 :bold]}]
    [:listitem {:$margin [0 0 0 5]
                :display "block"
                :vertical-align "top"
                :clear "both"}]]
   [:refsynopsisdiv {:display "block"
                     :$padding [1 2]
                     :$color [:white :bg-2]
                     :white-space "pre"}]])

(defn docs [key]
  (let [fn (str/replace (name key) #"-" "_")]
    (.readFileSync fs (str "docs/ref/" fn ".xml"))))

(def tips {
           :pg_statistic.correlation 
           [:p
            [:b "Correlation "]
            "Statistical correlation between physical row ordering and
         logical ordering of the column values. This ranges from -1 to +1. When
         the value is near -1 or +1, an index scan on the column will be
         estimated to be cheaper than when it is near zero, due to reduction of
         random access to the disk. (This column is null if the column data type
         does not have a < operator.)"]
           
           :pg_statistic.distinct
           [:p
            [:b "Distinct "]
            "If greater than zero, the estimated number of distinct values in the
          column. If less than zero, the negative of the number of distinct values divided
          by the number of rows. (The negated form is used when ANALYZE believes that the
          number of distinct values is likely to increase as the table grows; the positive
          form is used when the column seems to have a fixed number of possible values.)
          For example, -1 indicates a unique column in which the number of distinct values
          is the same as the number of rows."]})

(defn tip [key]
  (get tips key))


(def catalogs (.parse js/JSON (.readFileSync fs (str "docs/catalogs.json"))))

(defn catalog-docs [name]
  (let [id (str "catalog-" (str/replace  name #"_" "-"))
        html (aget catalogs id)]
    (when html (.-doc html))))


(defn catalog-column-docs [name column]
  (let [id (str "catalog-" (str/replace  name #"_" "-"))
        info (aget catalogs id)]
    (when info (aget (aget info "columns") column))))

(defn arraify [x]
  (.. js/Array -prototype -slice (call x)))

(defn load-catalogs []
  (.parseFromString (js/DOMParser.)
                    (-> (str (.readFileSync fs (str "docs/catalogs.xml")))
                        (str/replace #"<title" "<h2")
                        (str/replace #"</title" "</h2")
                        (str/replace #"&mdash;" ""))
                    "application/xml"))

(defn remove-xml-node [x]
  (.. x -parentNode (removeChild x)))

(defn get-html [x]
  (when x (.-innerHTML x)))

(defn sanitize-catalog [x]
  (let [columns #js{}]
    (doseq [xs (arraify (.-children x))]
      (when-let [h2 (.. xs (querySelector "h2"))]
        (when (re-find  #"Columns$" (.-innerHTML h2))
          (let [rows (.. xs (querySelectorAll "row"))]
            (doseq [row (arraify rows)]
              (let [cols (arraify (.-children row))]
                (aset columns (get-html (.querySelector row "structfield"))
                     #js{:type (get-html (.querySelector row "type"))
                         :ref (get-html (aget cols 2))
                         :details (get-html (aget cols 3))}))))
          (remove-xml-node xs))))
    #js{:doc (.-innerHTML x) :columns columns}))

(defn index-catalogs [xml]
  (let [sects (arraify (.. xml (querySelectorAll "sect1")))
        data (reduce (fn [acc x]
                       (aset acc (.-id x)
                             (sanitize-catalog x))
                       acc) #js{} sects)]
    data))

(defn to-json [x] (.stringify js/JSON x nil " "))

(defn persists-catalogs-index [idx]
  (.. fs (writeFile "docs/catalogs.json" (to-json idx))))


(comment 
  (persists-catalogs-index (index-catalogs (load-catalogs)))
  )


