(ns gl-cards.config
  (:require [clojure.data.json :as json]
            [environ.core :refer [env]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.gzip :refer [wrap-gzip]]
            [ring.middleware.logger :refer [wrap-with-logger]]))

(defn load-config-file []
  (json/read-str (slurp "config.json") :key-fn keyword))

(defn config []
  (merge {:http-port  (Integer. (or (env :port) 10555))
          :middleware [[wrap-defaults api-defaults]
                       wrap-with-logger
                       wrap-gzip]}
         (load-config-file)))
