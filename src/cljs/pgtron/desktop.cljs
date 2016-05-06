(ns pgtron.desktop
  (:require [goog.events :as events]
            [goog.history.EventType :as EventType]
            [cljsjs.react]
            [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [pgtron.layout :as l]
            [pgtron.index :as index]
            [pgtron.dashboard :as dash]
            [pgtron.database :as db]
            [pgtron.config :as config]
            [pgtron.users :as users]
            [pgtron.query :as query]
            [pgtron.table :as table]
            [pgtron.signin :as signin]
            [pgtron.state :as state]
            [pgtron.create :as create]
            [route-map.core :as rm]
            [reagent.core :as r])

  (:import goog.History))

(def routes {:GET #'signin/$index 
             "dashboard" {:GET #'index/$index}
             "config" {:GET #'config/$index}
             "users" {:GET #'users/$index}
             "query" {:GET #'query/$index}
             "new"   #'create/routes
             "db"    #'db/routes})

(defn not-found [path] [:h1 (str "Page " path " not found")])

(defn hook-browser-navigation! []
  (doto (History. false nil (.getElementById js/document "_hx"))
    (events/listen EventType/NAVIGATE
                   (fn [event]
                     (when-let [tab (state/current-tab)]
                       (swap! tab assoc :href (.-token event)))
                     )) (.setEnabled true)))

(defn test-cmp [tab-id]
  [:h1 tab-id])

(defn root []
  (fn []
    [l/layout
     (when-let [tab-id (:current-tab @state/state)]
       (let [current-tab (get-in @state/state [:tabs tab-id])]
         [:div
          (when-let [m (rm/match [:GET (:href current-tab)] routes)]
            [:div {:key tab-id}
             [(:match m) (r/cursor state/state [:tabs tab-id]) (:params m)]])]))]))

(defn mount-root []
  (reagent/render [root] (.getElementById js/document "app")))

(defn init! []
  (hook-browser-navigation!)
  (mount-root))
