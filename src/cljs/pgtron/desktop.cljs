(ns pgtron.desktop
  (:require [goog.events :as events]
            [goog.history.EventType :as EventType]
            [cljsjs.react]
            [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [route-map.core :as rm])
  (:import goog.History))

(defn $index []
  [:div [:h1 "hello"]])

(def routes {:GET  #$index})

(defn not-found [path] [:h1 (str "Page " path " not found")])

(defn dispatch [event]
  (.log js/console "Dispatch:" (.-token event))
  (data/save [:current-url] (str "#" (.-token event)))
  (if-let [m (rm/match [:GET (.-token event)] routes)]
    (let [mws (mapcat :mw (:parents m))
          h   #(session/put! :current-page [(:match m) (:params m)])
          stack (reduce (fn [acc x] (x acc)) h (reverse mws))]
      (println "MW:" mws)
      (stack m))
    (session/put! :current-page [#'not-found (.-token event)])))

(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen EventType/NAVIGATE dispatch) (.setEnabled true)))

(defn current-page []
  [:div (session/get :current-page)])

(defn mount-root []
  (reagent/render
   [current-page]
   (.getElementById js/document "app")))

(defn init! []
  (data/init-state)
  (hook-browser-navigation!)
    (mount-root))
