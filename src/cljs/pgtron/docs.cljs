(ns pgtron.docs
  (:require [clojure.string :as str]))

(def fs (js/require "fs"))

(def styles
  [:refentry
   [:indexterm {:$text [1.5 2 :bold]
                :$margin [1 0]
                :display "block"}]
   [:refsect1 {:display "block"
               :$margin [2 0]}]
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
    (.readFileSync fs (str "docs/" fn ".sgml"))))
