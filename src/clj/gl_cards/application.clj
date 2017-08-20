(ns gl-cards.application
  (:gen-class)
  (:require [com.stuartsierra.component :as component]
            [system.components.endpoint :refer [new-endpoint]]
            [system.components.handler :refer [new-handler]]
            [system.components.middleware :refer [new-middleware]]
            [system.components.http-kit :refer [new-web-server]]
            [gl-cards.config :refer [config]]
            [gl-cards.routes :refer [home-routes]]
            [gl-cards.core :refer [assoc-project-ids-with-paths get-projects update-cards-with-pipelines]]
            [gl-cards.state :refer [app-state]]))

(defn app-system [config]
  (component/system-map
   :routes     (new-endpoint home-routes)
   :middleware (new-middleware {:middleware (:middleware config)})
   :handler    (-> (new-handler)
                   (component/using [:routes :middleware]))
   :http       (-> (new-web-server (:http-port config))
                   (component/using [:handler]))))

(defn state-poller [state-atom key fn delay-millis]
  "Create a thread that will regularly call a function and update the atom with its result"
  (Thread. (fn []
             (while true
               (swap! state-atom assoc key (fn))
               (println state-atom)
               (Thread/sleep delay-millis)))))

(defn -main [& _]
  (let [config (config)]
    (-> config
        app-system
        component/start)
    
    (println "Started gl-cards on" (str "http://localhost:" (:http-port config)))
    (println "Apologies for the lengthy startup, loading many projects we don't care about because Gitlab...")
    
    (let [{:keys [base_url private_token cards]} config
          projects (get-projects base_url private_token)
          project-cards (assoc-project-ids-with-paths cards projects)]

      (println "Loaded.")
      (swap! app-state assoc :cards project-cards)
      (println app-state)
      (println "Starting pipeline state poller...")
      (.start (state-poller app-state :cards (partial update-cards-with-pipelines base_url private_token project-cards) 5000)))))
