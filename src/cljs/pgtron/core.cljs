(ns pgtron.core
  (:require [figwheel.client :as figwheel :include-macros true]
            [pgtron.desktop :as d]))

(.log js/console "here")
(enable-console-print!)

(figwheel/watch-and-reload
 :websocket-url (str "ws://localhost:3449/figwheel-ws")
 :jsload-callback d/mount-root)


(d/init!)
