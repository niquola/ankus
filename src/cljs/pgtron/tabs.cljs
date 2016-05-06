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
  (swap! state/state update-in [:tabs] assoc id
         {:id id
          :href "/dashboard"
          :title cs
          :connection-string (str "postgres://" cs)}))

(defn- remove-tab [id]
  (swap! state/state update-in [:tabs]
         (fn [old] (dissoc old id))))

(defn select-tab [id]
  (when-not (= (:current-tab @state/state) id)
    (swap! state/state assoc :current-tab id)
    (aset (.. js/window -location) "hash" (:href @(state/current-tab)))))

(defn- handle-add [scope]
  (fn [ev]
    (let [cs (.. ev -target -value)]
      (when (= 13 (.-which ev))
        (go (let [res (<! (test-connection cs))]
              (if (= :success (:status res))
                (add-tab (str (gensym)) cs)
                (swap! scope assoc :error (:error res)))))))))

(defn- handle-selection [id]
  (fn [ev] (select-tab id)))

(defn- handle-close [id]
  (fn [ev] (remove-tab id)))

(defn tabs []
  (let [scope     (r/atom {:value "nicola:nicola@localhost:5432/postgres"})
        bind      (fn [ev] (swap! scope assoc-in [:value] (.. ev -target -value)))
        bind-href (fn [ev] (swap! (state/current-tab) assoc-in [:href] (.. ev -target -value)))
        apply-href (fn [ev]
                     (when (= 13 (.-which ev))
                       (aset (.. js/window -location) "hash" (.. ev -target -value))))]
    (fn []
      (let [tabs    (vals (:tabs @state/state))
            current (:current-tab @state/state)]
        [:div#tabs
        (style [:#tabs
                {:$color [:light-gray :bg-2]}
                [:.href  {:$text [0.8 1]
                          :width "100%"
                          :display "block"
                          :border-radius 0}]
                [:.tab {:position "relative"
                        :cursor "pointer"
                        :display "inline-block" :$margin [0 1]
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
            [:div.tab {:key (str (:id tab))
                       :class (when (= current (:id tab)) "active")
                       :on-click (handle-selection (:id tab))}
             (:title tab) " "
             [:span.remove {:on-click (handle-close (:id tab))} (icon :close)]])
          [:div.tab
           [:input.form-control {:on-key-down (handle-add scope)
                                 :on-change bind
                                 :value (:value @scope)}]
           (when-let [err (:error @scope)] [:div.error err])]]
         (when-let [current-tab (state/current-tab)]
           [:input.href {:value (:href @current-tab)
                         :on-change bind-href
                         :on-key-down apply-href}])]))))
