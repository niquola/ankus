(ns pgtron.tabs
  (:require-macros [cljs.core.async.macros :as m :refer [go alt!]])
  (:require [pgtron.state :as state]
            [cljs.core.async :refer [>! <!]]
            [pgtron.pg :as pg]
            [chloroform.core :as form]
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
        (style [:#tabs {:$color [:light-gray :bg-1] :padding-top "4px"}
                [:.location {:$color [:white :bg-0]
                             :border-bottom "1px solid #222"
                             :$padding [0.1 1]}
                 [:.href  {:$text [0.8 1]
                           :$margin [0.3 1]
                           :$color [:light-gray :bg-1]
                           :border "none"
                           :width "80%"
                           :$padding [0.2 1]
                           :box-shadow "none"
                           :display "inline-block"}]]
                [:.tab {:position "relative"
                        :cursor "pointer"
                        :display "inline-block"
                        :$text [0.8 1.5]
                        :border-top "2px solid transparent"
                        :$margin [0 1 0 0]
                        :z-index 100
                        :$padding [0 1]}
                 [:.circle {:$color :gray}]
                 [:&.active {:$color [:white :bg-0]
                             :border-color "#6ED0E0"}
                  [:.circle {:$color :blue}]]
                 [:.remove [:&:hover {:$color :red}]]]])
         [:div.tab-list
          (for [tab tabs]
            [:div.tab {:key (str (:id tab))
                       :class (when (= current (:id tab)) "active")
                       :on-click (handle-selection (:id tab))}
             (if-let [i (:icon tab)]
               (icon i) [:span.circle "●"]) "  "
             (:title tab) " "
             (when-not (:non-removable tab)
               [:span.remove {:on-click (handle-close (:id tab))} (icon :close)])])]
         (when-let [current-tab (state/current-tab)]
           (when-not (:no-location @current-tab)
             [:div.location
              [:a {:href "#/dashboard" :title "Hot Key: CTRL-H"} (icon :home)]
              [:input.href {:value (:href @current-tab)
                            :on-change bind-href
                            :on-key-down apply-href}]]))]))))
(defn $signin []
  (let [state (r/atom {})
        form (form/form-cursor state [:auth] {:connection-string "nicola:nicola@localhost:5432/postgres"})
        submit (fn [ev]
                 (let [cs (get-in @form [:data :connection-string])]
                   (println "cs" cs)
                   (go (let [res (<! (test-connection cs))]
                         (if (= :success (:status res))
                           (add-tab (str (gensym)) cs)
                           (swap! state assoc :error (:error res)))))))]
   (fn []
     [:div#signin
      (style [:#signin {:display ""
                        :margin "10% auto"
                        :$width 70
                        :$padding [1 2]
                        :$color [:light-gray :bg-1]}
              [:label {:display "block"}]
              [:button {:width "100%"}]
              [:.connection-string {:$text [1.3 1.6 300]
                                    :width "100%"
                                    :$padding [0.5 1]
                                    :border "2px solid #555"
                                    :$color [:white :bg-2]}]])
      [form/form form {:class "form" :on-submit submit}
       [:$row {:name :connection-string
               :as :string
               :class "connection-string"
               :placeholder "user:password@localhost:5432/postgres"
               :$required true}]
       (when-let [err (:error @state)]
         [:div.alert.alert-dangerous err])
       #_[:pre (pr-str @form)]
       [:button.btn.btn-success.btn-lg {:type "submit"} "Connect"]]])))
