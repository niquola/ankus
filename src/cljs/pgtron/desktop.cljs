(ns pgtron.desktop
  (:require [goog.events :as events]
            [goog.history.EventType :as EventType]
            [cljsjs.react]
            [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [pgtron.layout :as l]
            [pgtron.dashboard :as dash]
            [pgtron.database :as db]
            [pgtron.config :as config]
            [pgtron.users :as users]
            [pgtron.query :as query]
            [route-map.core :as rm])
  (:import goog.History))

(def routes {:GET #'dash/$index
             "config" {:GET #'config/$index}
             "users" {:GET #'users/$index}
             "query" {:GET #'query/$index}
             "db" {[:db] {:GET #'db/$index}}})

(defn not-found [path] [l/layout {} [:h1 (str "Page " path " not found")]])

(defn dispatch [event]
  (if-let [m (rm/match [:GET (.-token event)] routes)]
    (let [mws (mapcat :mw (:parents m))
          h   #(session/put! :current-page [(:match m) (:params m)])
          stack (reduce (fn [acc x] (x acc)) h (reverse mws))]
      (stack m))
    (session/put! :current-page [not-found (.-token event)])))

(defn hook-browser-navigation! []
  (doto (History. false nil (.getElementById js/document "_hx"))
    (events/listen EventType/NAVIGATE dispatch) (.setEnabled true)))

(defn current-page []
  [:div (session/get :current-page)])

(defn mount-root []
  (reagent/render
   [current-page]
   (.getElementById js/document "app")))

(defn init! []
  (hook-browser-navigation!)
  (mount-root))
