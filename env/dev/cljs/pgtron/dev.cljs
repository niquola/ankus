(ns ^:figwheel-no-load pgtron.dev
  (:require [pgtron.core :as core]
             [figwheel.client :as figwheel :include-macros true]))

(enable-console-print!)

(figwheel/watch-and-reload
 :websocket-url (str "ws://localhost:3449/figwheel-ws")
 :jsload-callback core/mount-root)

(core/init!)

