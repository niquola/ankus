(ns chloroform.codemirror)


(def Doc (.-Doc js/CodeMirror))

(defn create-editor
  ([config]
   (js/CodeMirror (.-body js/document) (clj->js config)))
  ([dom config]
   (js/CodeMirror dom (clj->js config))))

(defn fromTextArea
  ([textarea]
   (.fromTextArea js/CodeMirror textarea))
  ([textarea config]
   (.fromTextArea js/CodeMirror textarea (clj->js config))))


(defn get-value
  ([editor] (.getValue editor))
  ([editor sep] (.getValue editor sep)))

(defn set-value
  [editor value] (.setValue editor value))

(defn get-range
  ([editor from to]
   (.getRange editor (clj->js from) (clj->js to)))
  ([editor from to sep]
   (.getRange editor (clj->js from) (clj->js to) sep)))

(defn replace-range
  ([editor string from]
   (.replaceRange editor string (clj->js from)))
  ([editor string from to]
   (.replaceRange editor string (clj->js from) (clj->js to))))

(defn get-line
  [editor n]
  (.getLine editor n))

(defn set-line
  [editor n text]
  (.setLine editor n text))

(defn remove-line
  [editor n]
  (.removeLine editor n))

(defn line-count
  [editor]
  (.lineCount editor))

(defn first-line
  [editor]
  (.firstLine editor))

(defn last-line
  [editor]
  (.lastLine editor))

(defn get-line-handle
  [editor n]
  (.getLineHandle editor n))

(defn get-line-number
  [editor handle]
  (.getLineNumber editor handle))

(defn each-line
  ([editor function]
   (.eachLine editor function))
  ([editor start end function]
   (.eachLine editor start end function)))

(defn mark-clean
  [editor]
  (.markClean editor))

(defn change-generation
  [editor]
  (.changeGeneration editor))

(defn is-clean
  [editor]
  (.isClean editor))

(defn get-selection
  [editor]
  (.getSelection editor))

(defn replace-selection
  [editor replacement]
  (.replaceSelection editor replacement))

(defn get-cursor
  [editor]
  (.getCursor editor))

(defn something-selected
  [editor]
  (.somethingSelected editor))

(defn set-cursor
  [editor pos]
  (.setCursor editor (clj->js pos)))

                                        ; -
(defn set-selection
  [editor anchor]
  (.setSelection editor (clj->js anchor)))

                                        ; -
(defn extend-selection
  [editor from]
  (.extendSelection editor (clj->js from)))

(defn set-extending
  [editor value]
  (.setExtending editor value))

(defn has-focus
  [editor]
  (.hasFocus editor))

(defn find-pos-h [])

(defn find-pos-v [])

(defn set-option
  [editor option value]
  (.setOption editor option value))

(defn get-option
  [editor option]
  (.getOption editor option))

(defn add-key-map [])

(defn remove-key-map [])

(defn add-overlay [])

(defn remove-overlay [])

(defn on [])

(defn off [])

(defn get-doc [editor] (.getDoc editor))

(defn get-editor [doc] (.getEditor doc))

(defn swap-doc [editor doc] (.swapDoc editor doc))

(defn copy [doc] (.copy doc))

(defn linked-doc []) 

(defn unlink-doc []) 

(defn iter-linked-docs [])

(defn undo [editor] (.undo editor))

(defn redo [editor] (.redo editor))

(defn history-size [editor] (.historySize editor))

(defn clear-history [editor] (.clearHistory editor))

(defn get-history [editor] (.getHistory editor))

(defn set-history [])

(defn mark-text [editor from to options]
  (.markText editor
             (clj->js from)
             (clj->js to)
             (clj->js options)))

(defn set-bookmard [])

(defn find-marks-at [])

(defn get-all-marks [])
