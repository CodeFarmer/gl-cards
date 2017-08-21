(ns user
  (:require [gl-cards.application :as app]
            [ring.middleware.reload :refer [wrap-reload]]
            [figwheel-sidecar.repl-api :as figwheel]))

(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
(def http-handler
  (wrap-reload #'gl-cards.application/http-handler))

(defn lein-figwheel-init []
  (app/-start))

(defn run []
  (app/-start)
  (figwheel/start-figwheel!))

(def browser-repl figwheel/cljs-repl)
