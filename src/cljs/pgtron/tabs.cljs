(ns pgtron.tabs
  (:require-macros [cljs.core.async.macros :as m :refer [go alt!]])
  (:require [pgtron.state :as state]
            [cljs.core.async :refer [>! <!]]
            [pgtron.pg :as pg]
            [pgtron.style :refer [style icon] :as st]
            [reagent.core :as r]))

(defn log [& args] (.. js/console -log (apply js/console (clj->js args))))

(defn test-connection [connection-string]
  (go
    (let [conn (str "postgres://" connection-string)
          res (<! (pg/raw-exec conn "select version() as version"))]
      (if (:error res)
        {:status :error   :error "Could not connect to server"}
        {:status :success :version (.-version (first res))}))))


(defn- add-tab [id cs]
  (swap! state/state update-in [:tabs] conj {:id id :title cs}))

(defn- remove-tab [id]
  (swap! state/state update-in [:tabs] (fn [old] (remove #(= id (:id %)) old))))

(defn select-tab [id]
  (let [tabs (:tabs @state/state)
        cs   (:title (first (filter #(= id (:id %)) tabs)))]
    (println "Select " cs)
    (swap! state/state merge {:current-tab id
                              :connection-string (str "postgres://" cs)})))

(defn- handle-add [scope]
  (fn [ev]
    (let [cs (.. ev -target -value)]
      (when (= 13 (.-which ev))
        (go (let [res (<! (test-connection cs))]
              (if (= :success (:status res))
                (do (add-tab (gensym) cs)
                    (swap! scope assoc :value ""))
                (swap! scope assoc :error (:error res)))))))))

(defn- handle-selection [id]
  (fn [ev] (select-tab id)))

(defn- handle-close [id]
  (fn [ev] (remove-tab id)))

(defn tabs []
  (let [scope (r/atom {:value "nicola:nicola@localhost:5432/postgres"
                       :href (.. js/window -location -hash)})
        bind      (fn [ev] (swap! scope assoc :value (.. ev -target -value)))
        bind-href (fn [ev] (swap! scope assoc :href (.. ev -target -value)))
        apply-href (fn [ev]
                     (when (= 13 (.-which ev))
                       (aset (.. js/window -location) "hash" (.. ev -target -value))))]
    (fn []
      (let [tabs (:tabs @state/state)
            current (:current-tab @state/state)]
        [:div#tabs
        (style [:#tabs
                {:$color [:black :gray]}
                [:.href  {:$text [0.8 1]
                          :width "100%"
                          :display "block"
                          :border-radius 0}]
                [:.tab {:$color [:black :white]
                        :position "relative"
                        :cursor "pointer"
                        :display "inline-block" :$margin [0 1]
                        :border "1px solid #ddd"
                        :z-index 100
                        :$padding [0.1 1]}
                 [:&.active {:$color [:white :black]}]
                 [:.remove [:&:hover {:$color :red}]]
                 [:input {:$width 40}]
                 [:.error {:position "absolute"
                           :$color [:white :red]
                           :box-shadow "0 0 4px gray"
                           :display "inline-block"
                           :border-radius "4px"
                           :text-align "left"
                           :z-index 100000
                           :$padding [1 2]
                           :left "5px"
                           :top "50px"}]]])
         [:div.tab-list
          (for [tab tabs]
            [:div.tab {:key (:id tab)
                       :class (when (= current (:id tab)) "active")
                       :on-click (handle-selection (:id tab))}
             (:title tab) " "
             [:a.remove {:on-click (handle-close (:id tab))} (icon :close)]])
          [:div.tab
           [:input.form-control {:on-key-down (handle-add scope)
                                 :on-change bind
                                 :value (:value @scope)}]
           (when-let [err (:error @scope)]
             [:div.error err])]]
         [:input.href {:value (:href @scope)
                       :on-change bind-href
                       :on-key-down apply-href}]]))))
