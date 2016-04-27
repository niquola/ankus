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


(def catalogs (.parseFromString
               (js/DOMParser.)
               (-> (str (.readFileSync fs (str "docs/catalogs.xml")))
                   (str/replace #"<title" "<h2")
                   (str/replace #"</title" "</h2")
                   (str/replace #"&mdash;" ""))
               "application/xml"))

(defn catalog-docs [name]
  (let [id (str "catalog-" (str/replace  name #"_" "-"))
        node (.querySelector catalogs (str "#" id))
        html (and node (.-outerHTML node))]

    (aset js/window "doc" catalogs)
    (.log js/console html)
    html))

(defn prepare-catalogs []
  (let [xml (.parseFromString
            (js/DOMParser.)
            (-> (str (.readFileSync fs (str "docs/catalogs.xml")))
                (str/replace #"<title" "<h2")
                (str/replace #"</title" "</h2")
                (str/replace #"&mdash;" ""))
            "application/xml")
        sects (.. xml (querySelectorAll "sect1"))

        data (reduce (fn [acc x]
                       (aset acc (.-id x) (.outerHTML x))
                       ) #js{} sects)]

  (aset js/window "doc" xml)
  (.. fs (.writeSync "catalogs.json" (.stringify js/Object data nil " ")))))

(comment
  (prepare-catalogs)
  )

