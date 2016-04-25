(defproject pgtron "0.1.0-alpha1"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :source-paths ["src/cljs"]

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [cljsjs/react "15.0.1-0"]
                 [cljsjs/react-with-addons "15.0.1-0"]
                 [cljsjs/nodejs-externs "1.0.4-1"]
                 [org.clojure/clojurescript "1.8.40"]
                 [org.clojure/core.async "0.2.374"]
                 [garden "1.3.2"]
                 [reagent "0.6.0-alpha" :exclusions [cljsjs/react]]
                 [reagent-forms "0.5.22"]
                 [reagent-utils "0.1.7"]
                 [route-map "0.0.3"]]

  :node-dependencies [[pg "latest"]]


  :plugins [[lein-cljsbuild "1.1.3"]
            [lein-asset-minifier "0.2.4" :exclusions [org.clojure/clojure]]]

  :min-lein-version "2.5.3"

  :cljsbuild {:builds {:app {:source-paths ["src/cljs"]
                             :compiler {:main "pgtron.core"
                                        :output-to     "app/js/p/app.js"
                                        :output-dir    "app/js/p/out"
                                        :asset-path    "js/p/out"
                                        :optimizations :none
                                        :pretty-print  true
                                        ;; :externs ["assets/codemirror-externs.js"]
                                        ;; :foreign-libs [{:file "assets/codemirror.min.js"
                                        ;;                 :provides ["CodeMirror"]}]
                                        :cache-analysis true}}}}

  :clean-targets ^{:protect false} [:target-path "out" "app/js/p"]

  :repl-options  {:init-ns pgtron.dev
                  :nrepl-middleware  [cemerick.piggieback/wrap-cljs-repl]}

  :figwheel {:http-server-root "app"
             :nrepl-middleware ["cemerick.piggieback/wrap-cljs-repl"]
             :css-dirs         ["app/css"]}

  :profiles {:dev {:cljsbuild {:builds {:app {:compiler {:source-map true
                                                         :main  "pgtron.core"
                                                         :verbose true}
                                              :figwheel {:on-jsload "pgtron.core/mount-root"}}}}
                   :source-paths ["env/dev/cljs" "env/dev/clj"]

                   :dependencies [[figwheel-sidecar "0.5.2"]
                                  [com.cemerick/piggieback "0.2.1"]]

                   :plugins [[lein-ancient "0.6.8"]
                             [lein-kibit "0.1.2"]
                             [lein-cljfmt "0.4.1"]
                             [lein-figwheel "0.5.2"]]}

             :production {:cljsbuild {:builds {:app {:compiler {:optimizations :advanced
                                                                :main          "pgtron.prod"
                                                                :parallel-build true
                                                                :cache-analysis false
                                                                :closure-defines {"goog.DEBUG" false}
                                                                :externs ["externs/misc.js"]
                                                                :pretty-print false}
                                                     :source-paths ["env/prod/cljs"]}}}}})
