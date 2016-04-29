(ns pgdocs.core
  (:require [clojure.string :as str]))

(def fs (js/require "fs"))

(defn docs [key]
  (let [fn (str/replace (name key) #"-" "_")]
    (.readFileSync fs (str "docs/ref/" fn ".xml"))))

(defn tip [key]
  (get tips key))


(def catalogs (.parse js/JSON (.readFileSync fs (str "docs/catalogs.json"))))
(def monitoring (.parse js/JSON (.readFileSync fs (str "docs/monitoring.json"))))

(defn catalog-docs [name]
  (let [id (str "catalog-" (str/replace  name #"_" "-"))
        html (aget catalogs id)]
    (when html (.-doc html))))

(defn view-info [name]
  (or
   (aget catalogs (str "view-" (str/replace  name #"_" "-")))
   (aget monitoring (str (str/replace  name #"_" "-") "-view"))))

(defn view-docs [name]
  (when-let [info (view-info name)] (.-doc info)))

(defn catalog-column-docs [name column]
  (let [id (str "catalog-" (str/replace  name #"_" "-"))
        info (aget catalogs id)]
    (when info (aget (aget info "columns") column))))

(defn view-column-docs [name column]
  (let [info (view-info name)]
    (when info (aget (aget info "columns") column))))

(defn arraify [x]
  (.. js/Array -prototype -slice (call x)))

(defn load-xml [pth]
  (.parseFromString (js/DOMParser.)
                    (-> (str (.readFileSync fs (str pth)))
                        (str/replace #"<title" "<h2")
                        (str/replace #"</title" "</h2")
                        (str/replace #"&mdash;" ""))
                    "application/xml"))

(defn load-catalogs []
  (load-xml (str "docs/catalogs.xml")))

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

(defn persist-index [fl idx]
  (.. fs (writeFile fl (to-json idx))))

(defn persist-catalogs-index [idx]
  (persist-index "docs/catalogs.json" idx))

(defn persist-monitoring-index [idx]
  (persist-index "docs/monitoring.json" idx))

(defn load-monitoring []
  (load-xml "docs/monitoring.xml"))

(defn query-one [doc sel]
  (.querySelector doc sel))

(defn query-text [doc sel]
  (when-let [x (.querySelector doc sel)] (.-innerHTML x)))

(defn query [doc sel]
  (arraify (.querySelectorAll doc sel)))

(defn log [x]
  (.log js/console x))

(defn index-monitoring [doc]
  (let [idx #js{}
        current (atom #js{:docs #js[]})]
    (doseq [node (-> doc
                     (query-one "#monitoring-stats-views")
                     (.-children)
                     (arraify))]
      (let [tag (.-tagName node)]
        (cond
          (= "table" tag) (do
                            (let [rows    (query node "row")
                                  columns (reduce (fn [acc x]
                                                    (let [kn (query-one x "structfield")
                                                          cols (query x "entry")
                                                          tp   (query-text (get cols 1) "type")]
                                                      (when-let [k (and kn (.-innerHTML kn))]
                                                        (aset acc k #js{:type tp
                                                                        :details (.-innerHTML (aget cols 2))})))
                                                    acc) #js{} rows)
                                  desc #js{:doc "" :columns columns}]
                              (aset idx (.-id node) desc)
                              (reset! current desc)))
          :else (let [d (or (.-doc @current) "")] 
                  (aset @current "doc" (str d (.-innerHTML node)))))))
    idx))

(comment
  (persist-monitoring-index (index-monitoring (load-monitoring)))

  (persist-catalogs-index (index-catalogs (load-catalogs))))

