(ns pgtron.core
  (:require [figwheel.client :as figwheel :include-macros true]
            [pgtron.desktop :as core]))

(enable-console-print!)

(figwheel/watch-and-reload
 :websocket-url (str "ws://localhost:3449/figwheel-ws")
 :jsload-callback core/mount-root)

(core/init!)
