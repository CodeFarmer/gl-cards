(ns gl-cards.application
  (:gen-class)
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [compojure.core :refer [GET defroutes]]
            [compojure.route :refer [resources]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.gzip :refer [wrap-gzip]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.middleware.logger :refer [wrap-with-logger]]
            [ring.util.response :refer [response]]
            [environ.core :refer [env]]
            [ring.adapter.jetty :refer [run-jetty]]
            [gl-cards.core :refer [assoc-project-ids-with-paths get-projects update-cards-with-pipelines]]))

(def app-state (atom {}))


(defn state-poller [state-atom key fn delay-millis]
  "Create a thread that will regularly call a function and update the atom with its result"

  (defn polling-loop []
    (while true
      (swap! state-atom assoc key (fn))
      (Thread/sleep delay-millis)))

  (Thread. polling-loop)) ;; for some reason (Thread. (fn [] (while ...))) executes the while loop before creating the thread! But defn works.

(defroutes routes
   
  (GET "/" _
       (-> "public/index.html"
           io/resource
           io/input-stream
           response
           (assoc :headers {"Content-Type" "text/html; charset=utf-8"})))

  (GET "/cards" _
       (assoc (response (json/write-str (:cards @app-state)))
              :headers {"Content-Type" "application/json; charset=utf-8"}))
   
  (resources "/"))

(defn wrap-json-body-keys [handler]
  (wrap-json-body handler {:keywords? true}))

(def http-handler
  (-> routes
      (wrap-defaults api-defaults)
      wrap-json-response
      wrap-json-body-keys
      wrap-with-logger
      wrap-gzip))

(defn -start []
  (let [config (json/read-str (slurp "config.json") :key-fn keyword)]
    
    (println "Apologies for the lengthy startup, loading many projects we don't care about because Gitlab...")
    
    (let [{:keys [base_url private_token cards]} config
          projects (get-projects base_url private_token)
          project-cards (assoc-project-ids-with-paths cards projects)]

      (println "Loaded" (count projects) "projects;" (count project-cards) "cards.")
      (swap! app-state assoc :cards project-cards)
      (println app-state)
      (println "Starting pipeline state poller thread...")
      (.start (state-poller app-state :cards (partial update-cards-with-pipelines base_url private_token project-cards) 5000))
      (println "Started."))))


(defn -main [& [port]]
  
  (let [port (Integer. (or port (env :port) 10555))]
    (run-jetty http-handler {:port port :join? false}))
  
  (-start))
