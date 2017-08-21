(defproject gl-cards "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.671" :scope "provided"]
                 [com.cognitect/transit-clj "0.8.300"]
                 [ring "1.6.2"]
                 [ring/ring-defaults "0.3.1"]
                 [ring/ring-json "0.4.0"]
                 [bk/ring-gzip "0.2.1"]
                 [radicalzephyr/ring.middleware.logger "0.6.0"]
                 [compojure "1.6.0"]
                 [environ "1.1.0"]
                 [org.clojure/tools.namespace "0.2.11"]
                 [http-kit "2.2.0"]
                 [org.omcljs/om "1.0.0-alpha48"]
                 [cljs-http "0.1.42"]]

  :plugins [[lein-cljsbuild "1.1.6"]
            [lein-environ "1.1.0"]]

  :min-lein-version "2.6.1"

  :source-paths ["src/clj" "src/cljs" "src/cljc"]

  :test-paths ["test/clj" "test/cljc"]

  :clean-targets ^{:protect false} [:target-path :compile-path "resources/public/js"]

  :uberjar-name "gl-cards.jar"

  :main gl-cards.application

  :repl-options {:init-ns user}

  :cljsbuild {:builds
              [{:id "app"
                :source-paths ["src/cljs" "src/cljc" "dev"]

                :figwheel {:on-jsload "gl-cards.system/reset"}

                :compiler {:main cljs.user
                           :asset-path "js/compiled/out"
                           :output-to "resources/public/js/compiled/gl_cards.js"
                           :output-dir "resources/public/js/compiled/out"
                           :source-map-timestamp true}}

               {:id "test"
                :source-paths ["src/cljs" "test/cljs" "src/cljc" "test/cljc"]
                :compiler {:output-to "resources/public/js/compiled/testable.js"
                           :main gl-cards.test-runner
                           :optimizations :none}}

               {:id "min"
                :source-paths ["src/cljs" "src/cljc"]
                :jar true
                :compiler {:main gl-cards.system
                           :output-to "resources/public/js/compiled/gl_cards.js"
                           :output-dir "target"
                           :source-map-timestamp true
                           :optimizations :advanced
                           :pretty-print false}}]}

  :figwheel {:http-server-root "public"
             
             :css-dirs ["resources/public/css"]
             :init user/lein-figwheel-init
             :ring-handler user/http-handler
             :server-logfile "log/figwheel.log"}

  :doo {:build "test"}

  :profiles {:dev
             {:dependencies [[figwheel "0.5.11"]
                             [figwheel-sidecar "0.5.11"]
                             [com.cemerick/piggieback "0.2.2"]
                             [org.clojure/tools.nrepl "0.2.13"]
                             [lein-doo "0.1.7"]
                             [reloaded.repl "0.2.3"]]

              :plugins [[lein-figwheel "0.5.11"]
                        [lein-doo "0.1.7"]]

              :source-paths ["dev"]
              :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}

             :uberjar
             {:source-paths ^:replace ["src/clj" "src/cljc"]
              :prep-tasks ["compile"
                           ["cljsbuild" "once" "min"]]
              :hooks []
              :omit-source true
              :aot :all}})
