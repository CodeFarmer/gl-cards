(ns gl-cards.core
  (:require [clojure.data.json :as json]
            [clojure.string :as str]
            [org.httpkit.client :as http]))

(defn -rel-link-to-key-val-pair [rel-link]
    ;; Now I have two problems
    (let [[_ link rel] (re-matches #".*\<(.+)\>.+rel=\"(.+)\"" rel-link)]
      [(keyword rel) link]))

(defn -link-header-map [link-header]

  (if link-header
    (let [links (str/split link-header #",")]
      (apply hash-map (flatten (map -rel-link-to-key-val-pair links))))
    {}))

(defn -get-paginated-resource
  ;; FIXME need to log errors/truncations somewhere
  ;; Also FIXME this was the first thing I thought of, is it nice?
  [url opts]

  (loop [acc []
         url url
         opts opts]
    
    (let [response @(http/get url opts)
          {:keys [status headers body]} response
          link     (:link headers)]

      (if (= 200 status)
        
        (let [resources (concat acc (json/read-str body :key-fn keyword))]
          (if-let [next-url (:next (-link-header-map link))]
            (recur resources next-url {:headers (:headers opts)})
            
            resources))
        
        acc))))

(defn get-projects
  [base-url private-token]
  (-get-paginated-resource (str base-url "/api/v4/projects")
                          {:query-params {:membership true
                                          :per_page 100}
                           :headers      {"PRIVATE-TOKEN" private-token}}))

(defn get-pipelines
  [base-url private-token project-id]
  (-get-paginated-resource (str base-url "/api/v4/projects/" project-id "/pipelines")
                          {:query-params {:per_page 100}
                           :headers {"PRIVATE-TOKEN" private-token}}))

(defn get-pipeline [base-url private-token project-id pipeline-id]
  ;; FIXME deal with errors better
  (let [url (str base-url "/api/v4/projects/" project-id "/pipelines/" pipeline-id)
        response @(http/get url
                            {:query-params {:per_page 100}
                             :headers {"PRIVATE-TOKEN" private-token}})]
    (json/read-str (:body response) :key-fn keyword)))

(defn get-most-recent-pipeline [base-url private-token project-id ref]
  (let [url (str base-url "/api/v4/projects/" project-id "/pipelines")
        response @(http/get url
                            {:query-params {:per-page 1
                                            :ref ref}
                             :headers {"PRIVATE-TOKEN" private-token}})]
    (first (json/read-str (:body response) :key-fn keyword))))

(defn -first-project-with-path [path projects]
  (first (filter #(= path (:path_with_namespace %)) projects)))

(defn find-projects-with-paths [paths projects]
  (map #(-first-project-with-path % projects) paths))

;; This is boneheaded and serial. FIXME
;; Also FIXME get refs other than master
(defn cards [base-url private-token projects]
  (map (fn [project] {:name (:path_with_namespace project)
                      :status (:status (get-most-recent-pipeline base-url private-token (:id project) "master"))}) projects))

;; TODO now figure out how to pass the asynchronicity all the way through
;; using a nice future mapping :)


;; (use 'gl-cards.core :reload)
;; (def projects (get-projects "http://gitlab.com" "SOME_PRIVATE_TOKEN"))
;; (filter #(re-find #"checkout/api" (:path_with_namespace %)) projects)
;; (get-most-recent-pipeline "http://gitlab.com" "SOME_PRIVATE_TOKEN" 3818378 "master")
;; (filter #(re-find #"checkout/api" (:path_with_namespace %)) projects)
;; (def paths ["seatwave-repos/checkout/api" "seatwave-repos/checkout/app" "seatwave-repos/checkout/ui-library"])
;; (find-projects-with-paths paths projects)
;; (def c (cards "http://gitlab.com" "6kgsrgmhzF1zHftH_vj_" (find-projects-with-paths paths projects)))
