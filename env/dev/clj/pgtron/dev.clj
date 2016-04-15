(ns pgtron.dev
  (:require [figwheel-sidecar.repl-api :as ra]))

(def ^:dynamic *server-instance* nil)

(defn cljs [] (ra/cljs-repl "app"))

(defn start-figwheel []
  (ra/start-figwheel!))

(defn start-fw []
  (ra/start-figwheel!)
  (cljs))

(defn stop-fw []
  (ra/stop-figwheel!))
