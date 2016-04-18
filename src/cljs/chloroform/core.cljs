(ns chloroform.core
  (:require-macros [reagent.ratom :refer [reaction]]
                   [cljs.core.async.macros :as m :refer [go alt!]])
  (:require [clojure.string :as str]
            [clojure.walk :as walk]
            [chloroform.codemirror :as cm]
            [cljs.core.async :as a]
            [reagent.core :as r]))

(defn vectorize-input-name [attrs]
  (update-in attrs [:name] #(if (vector? %) % (vector %))))

(defn form-atom [& [data]] (r/atom {:data (or data {}) :errors {} :touched {} }))

(defn form-cursor [scope path & [data]]
  (swap! scope assoc-in path {:data (or data (get-in @scope (conj path :data)) {}) :errors {} :touched {} })
  (r/cursor scope path))

(defn reset [form & init]
  (swap! form  {:data (or init {}) :errors {} :touched {} } ))

(defn disable [f] (swap! f assoc :disabled true))

(defn enable [f] (swap! f dissoc :disabled))

(defn mark-as-touched [doc key] (swap! doc assoc-in (into [:touched] key) true))


(def validators
  {:$required (fn [cfg x]
                (when (and (not (or (true? x) (false? x)))
                           (or (nil? x) (= x "") (empty? x)))
                  :required))

   :$min-length (fn [min x]
                  (when [min x]
                    (if (or (nil? x) (< (count x) min) (< (.-length x) min))
                      :min-length)))

   :$max-length (fn [max x]
                  (when (and (not (nil? x)) (> (count x) max) (> (.-length x) max))
                    :max-length))})

(defn throttle [t cb]
  (let [a (r/atom nil)]
    (fn [e]
      (.persist e)
      (when @a (.clearTimeout js/window @a))
      (reset! a (.setTimeout js/window  #(cb e) t))
      true)))

(defn bind [doc path & [attrs]]
  (when-not  (get-in @doc path)
    (swap! doc assoc-in path (:value attrs)))
  (merge attrs
         {:on-change #(swap! doc assoc-in path (.. % -target -value))
          :value (get-in @doc path)}))


(defn bind-to [doc key attrs]
  (merge attrs
         {:on-change #(if (= (:as attrs) :checkbox)
                        (swap! doc assoc-in (into [:data] key) (.. % -target -checked))
                        (swap! doc assoc-in (into [:data] key) (.. % -target -value)))

          :value (get-in @doc (into [:data] key))

          :on-focus (fn [] (swap! doc assoc :focused true))
                                        ; :disabled (:disabled @doc)

          :on-blur #(do (mark-as-touched doc key)
                        (swap! doc assoc :focused false))

          :class (str/join " " [(:class attrs)
                                (when (and (get-in @doc [:errors key])
                                           (get-in @doc [:touched key])) "error")])}))

(defn index-of [coll value]
  (first (map first
              (filter #(= (second %) value)
                      (map-indexed vector coll)))))

(defn vec-remove
  "remove elem in coll"
  [coll pos]
  (vec  (concat  (subvec coll 0 pos)  (subvec coll  (inc pos)))))

(defn checked? [doc key value]
  (let [idx (index-of (get-in @doc key) value)]
    (and (>= idx 0) (not (nil? idx)))))

(defn selected? [doc key value]
  (= (get-in @doc key) value))

(defn box-toggle [doc key value]
  (fn []
    (if (empty? (get-in @doc key))
      (swap! doc assoc-in key []))
    (let [c (checked? doc key value)
          d (get-in @doc key)
          i (index-of (get-in @doc key) value) ]
      (swap! doc assoc-in key
             (if c
               (vec-remove d i)
               (conj d value))))))

(defn box-select [doc key value]
  (fn [] (swap! doc assoc-in [:data key] value)))

(def simple-inputs
  {:string     [:input {:type "text"}]
   :password   [:input {:type "password"}]
   :email      [:input {:type "email"}]
   :text       [:textarea {:rows "3"}]
   :json       [:textarea {} ]
   :sql        [:textarea {} ]
   :javascript [:textarea {} ]
   :checkbox   [:input {:type "checkbox"}]})

(def cm-modes
  {:sql "text/x-sql"
   :json "application/json" })

(def default-cm-options
  {:lineNumbers true})

(defn cm-config [opts]
  (merge default-cm-options
         opts))

(defn- bind-codemirror [codemirror doc ks]
  ;; update watcher
  (remove-watch doc :cm-watcher)
  (add-watch doc :cm-watcher
             (fn [key ra old-val new-val]
               (let [input-value (get-in new-val ks)]
                 (if input-value
                   (when (not= (.getValue codemirror) input-value)
                     (.setValue codemirror (.toString input-value)))
                   (.setValue codemirror "" )))))

  ;; erase all 'onchange' listeners (the hard way)
  (if-let [handlers (aget codemirror "_handlers")]
    (aset handlers "change" (js/Array.)))

  ;; new event listener with new keys
  (.on codemirror "change"
       (fn [& _]
         (swap! doc assoc-in ks (.getValue codemirror))))
  ;; setting new value only when 'change' event listener
  ;; was updated
  (.setValue codemirror (str (get-in @doc ks))))

(defn codemirror [doc ks options]
  (let [element-id (str "codemirror_" (gensym))]
    (r/create-class
     {:reagent-render
      (fn [doc ks options] [:textarea {:id element-id}])

      :component-did-mount
      (fn [this]
        #_(.log js/console "CodeMirror arguments:" (clj->js (aget (aget this "props") "argv")))

        (let [argv (aget (aget this "props") "argv")
              [_ doc ks options] argv
              element (.getElementById js/document element-id)
              codemirror (cm/fromTextArea element (merge default-cm-options options))]

          (when (nil? doc)
            (.warn js/console "[codemirror] component invoked with NULL doc argument"))

          (aset element "CodeMirrorInstance" codemirror)
          (bind-codemirror codemirror doc ks)))

      :component-will-receive-props
      (fn [this new-argv]
        (let [[_ doc ks options] new-argv
              [_ _ old-ks _] (aget (aget this "props") "argv")]
          (when (not (= ks old-ks))
            (let [element (.getElementById js/document element-id)
                  codemirror (aget element "CodeMirrorInstance")]

              (when (nil? doc)
                (.warn js/console "[codemirror] component invoked with NULL doc argument"))

              (when (nil? codemirror)
                (.warn js/console "[codemirror] cannot find CM instance for element" element))
              (bind-codemirror codemirror doc ks)))))})))

(defn mk-simple-input [doc {as :as ks :name ek :extraKeys :as attrs}]
  (if (some #(= % (:as attrs)) [:json :sql :javascript])
    [codemirror doc (into [:data] (if (vector? ks) ks [ks]))
     (merge default-cm-options {:extraKeys ek :mode (get cm-modes as)})]

    (let [[tag def-attrs] (get simple-inputs (:as attrs))
          attrs (merge (bind-to doc (:name attrs) (merge def-attrs attrs))
                       {:id (name (str/join "_" (map name (:name attrs))))}) ]
      [tag attrs])))

(defn mk-checkbox [doc {key :name :as attrs}]
  (let [value-fn (or (:value-fn attrs) identity)
        text-fn  (or (:text-fn attrs) identity)
        k (into [:data] key)]

    (into [:div.check-boxes-container]
          (map (fn [x]
                 [:div.check-box
                  {:on-blur #(mark-as-touched doc key)
                   :on-click  (box-toggle doc k  (value-fn x))
                   :class  (when  (checked? doc k  (value-fn x)) "checked") }

                  (if (checked? doc k (value-fn x))
                    (name :checkbox-checked)
                    (name :checkbox-unchecked) )

                  [:a.check-box-label {:href "javascript:void(0)" }
                   (text-fn x)]])
               (:collection attrs)))))

(defn mk-radio-buttons [doc {key :name :as attrs}]
  (let [value-fn (or (:value-fn attrs) identity)
        text-fn  (or (:text-fn attrs) identity)
        k (into [:data] key)]

    (into [:div.radio-buttons-container]
          (map (fn [x]
                 [:div.radio-btn {:on-blur #(mark-as-touched doc key)
                                  :on-click #(swap! doc assoc-in k (value-fn x))
                                  :class (when (selected? doc k (value-fn x)) "checked")}

                  (if (= (get-in @doc k) (value-fn x))
                    (name :radio-checked)
                    (name :radio-unchecked) )

                  [:a.radio-btn-label {:href "javascript:void(0)"}
                   (text-fn x)]])
               (:collection attrs)))))

(defn mk-select [doc {key :name :as attrs}]
  (let [value-fn (or (:value-fn attrs) identity)
        text-fn  (or (:text-fn attrs) identity)
        k (into [:data] key)]
    (into [:select {:on-change #(swap! doc assoc-in k (.. % -target -value))
                    :on-blur #(mark-as-touched doc key)
                    :value (get-in @doc k)
                    :disabled (:disabled attrs)}]
          (map (fn [x]
                 [:option {:value (value-fn x)} (text-fn x)])
               (:collection attrs)))))

(def custom-inputs
  {:check-boxes mk-checkbox
   :select mk-select
   :radio-buttons mk-radio-buttons})

(defn mk-input [doc attrs]
  (let [attrs (vectorize-input-name attrs)]
    (if (contains? simple-inputs (:as attrs))
      [mk-simple-input doc attrs]

      (if-let [custom (get custom-inputs (:as attrs))]
        (custom doc attrs)
        (println "ERROR: do not know how to render" attrs)))))

(defn mk-error [doc attrs]
  (let [*doc @doc
        path (:name attrs)
        errs (->> (get-in *doc (into [:errors] path))
                  (map #(or (get attrs %) (name %)))) ;; TODO: default messages
        srv-errs (or (get-in *doc (into [:server-errors] path)) [])
        errs (concat errs srv-errs)]
    [:div.error-container
     (when (and (or (:submit *doc)
                    (get-in *doc (into [:touched] path)))
                (not (empty? errs)))
       [:div.error (str/join ", " errs)])]))

(defn labelize [k]
  (str/join " " (map str/capitalize (flatten (map (fn [x] (-> x
                                                              name
                                                              (str/split #"_"))) k)))))

(def non-dashed-inputs
  #{:radio-buttons :check-boxes :json :javascript :sql})


(defn mk-row [doc attrs]
  (let [attrs (vectorize-input-name attrs)]

    [:div.form-group {:class (str (name (:as attrs))
                                  (when (and (get-in @doc (into [:errors] (:name attrs)))
                                             (or (get-in @doc (into [:touched] (:name attrs)))
                                                 (:submit @doc)))
                                    " error"))}

     [:label {:for (str/join "_" (map name (:name attrs))) } (or (:label attrs) (labelize (:name attrs)))]
     [mk-input doc attrs]

     (when-not (contains? non-dashed-inputs (:as attrs)) [:div.dash])

     [:div.under-ctrl
      (when (:hint attrs)
        [:div.hint (:hint attrs)])
      (mk-error doc attrs)] ]))

(def inputs
  {:$input    mk-input
   :$row      mk-row
   :$error    mk-error})

(defn extract-validators [attrs]
  (reduce (fn [acc [k v]]
            (if (= 0 (.indexOf (name k) "$"))
              (assoc-in acc [:validators k] v)
              (assoc-in acc [:attrs k] v)))
          {:validators {} :attrs {}} attrs))

(defn mk-inputs [doc cnt]
  (let [validators (atom {})
        dom (walk/postwalk
             (fn [x]
               (if-let [inp (and (vector? x) (get inputs (first x)))]
                 (do
                   (let [attrs (vectorize-input-name (second x))
                         {attrs :attrs
                          vals :validators} (extract-validators attrs)]
                     (when-not (empty? vals) (swap! validators assoc (:name attrs) vals))
                     [inp doc attrs]))
                 x))
             cnt)]
    [@validators dom]))

(defn merge-errors [e1 e2]
  (reduce (fn [acc [k v]]
            (if-let [errs (get acc k)]
              (assoc acc k (concat errs v))
              (assoc acc k v))) e1 e2))

(defn validate [doc form-validators validate-fn]
  (let [errors (reduce (fn [acc [key vs]]
                         (let [errs (reduce (fn [a [rule cfg]]
                                              (if-let [err (apply (rule validators) [cfg (get-in @doc (into [:data] key))])]
                                                (conj a err)
                                                a))
                                            [] vs) ]

                           (if (empty? errs)
                             acc
                             (assoc acc key  errs ))))
                       {} form-validators)

        errors (if-let [validate-errors  (and  validate-fn (validate-fn (:data @doc)))]
                 (merge-errors errors validate-errors)
                 errors)

        valid (empty? errors)

        errors (reduce (fn [acc [k v]] (assoc-in acc k v)) {} errors)]

    (if (not= (:errors @doc ) errors)
      (do (swap! doc assoc :errors errors)
          (swap! doc assoc :valid  valid)))))

(defn valid? [doc]
  (empty? (:errors doc)))

(defn on-submit [doc attrs validatee]
  (let [v (:validators attrs)]
    (fn [ev]
      (.preventDefault ev)
      (swap! doc assoc :submit true)
      (validatee)
      (when (valid? @doc)
        (go
          (when-let [result (a/<! ((:on-submit attrs) (:data @doc)))]
            (swap! doc assoc :server-errors (:errors result))))))))


(defn form-classes [cls doc]
  (str/join " " [cls
                 (when (:focused doc) "focused")
                 (when (:empty doc) "empty")
                 (when (:valid doc) "valid")
                 (when (not (:empty doc)) "dirty")]))

(defn reset-form [fatom]
  (reset! fatom {:data {} :errors {} :touched {} }))

(defn form [doc attrs & cnt]
  (let [[vals dom] (mk-inputs doc cnt)
        validation  #(validate doc vals (:validate attrs))
        form-dom (into [:form (merge attrs
                                     {:on-submit (on-submit doc attrs validation)
                                      :class (form-classes (:class attrs) @doc)})]
                       dom)]
    (validation)
    form-dom))
