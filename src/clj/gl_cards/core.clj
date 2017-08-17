(ns gl-cards.core
  (:require [clojure.data.json :as json]
            [clojure.string :as str]
            [org.httpkit.client :as http]))


(defn link-header-map [link-header]

  (defn rel-link-to-key-val-pair [rel-link]
    (let [[_ link rel] (re-matches #".*\<(.+)\>.+rel=\"(.+)\"" rel-link)]
      [(keyword rel) link]))

  (if link-header
    (let [links (str/split link-header #",")]
      (apply hash-map (flatten (map rel-link-to-key-val-pair links))))
    {}))

(defn get-paginated-resource
  [url opts]

  (loop [acc []
         url url
         opts opts]

    (println "JGG-DEBUG: (get-paginated-resource " url " " opts ")")
    
    (let [response @(http/get url opts)
          {:keys [status headers body]} response
          link     (:link headers)]

      (if (= 200 status)
        
        (let [resources (concat acc (json/read-str body :key-fn keyword))]
          (if-let [next-url (:next (link-header-map link))]
            (recur resources next-url {:headers (:headers opts)})
            
            resources))
        
        acc))))

(defn get-projects
  [base-url private-token]
  (get-paginated-resource (str base-url "/api/v4/projects")
                          {:query-params {:membership true
                                          :per_page 100}
                           :headers      {"PRIVATE-TOKEN" private-token}}))

(defn get-pipelines
  [base-url private-token project-id]
  (get-paginated-resource (str base-url "/api/v4/projects/" project-id "/pipelines")
                          {:query-params {:per_page 100}
                           :headers {"PRIVATE-TOKEN" private-token}}))

;; (filter #(re-find #"checkout/api" (:path_with_namespace %)) projects)
