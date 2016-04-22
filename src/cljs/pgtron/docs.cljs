(ns pgtron.docs
  (:require [clojure.string :as str]))

(def fs (js/require "fs"))

(def styles
  [:refentry
   [:indexterm {:$text [1.5 2]
                :$margin [1 0]
                :display "block"}]
   [:refsynopsisdiv {:display "block"
                     :white-space "pre"}]])

(defn docs [key]
  (let [fn (str/replace (name key) #"-" "_")]
    (.readFileSync fs (str "docs/" fn ".sgml"))))
