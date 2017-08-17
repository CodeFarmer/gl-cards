(ns gl-cards.core-test
  (:require [clojure.test :refer :all]
            [gl-cards.core :refer :all]))


(def links-header "<https://gitlab.com/api/v4/projects?archived=false&membership=true&order_by=created_at&owned=false&page=2&per_page=100&simple=false&sort=desc&starred=false&statistics=false&with_issues_enabled=false&with_merge_requests_enabled=false>; rel=\"next\", <https://gitlab.com/api/v4/projects?archived=false&membership=true&order_by=created_at&owned=false&page=1&per_page=100&simple=false&sort=desc&starred=false&statistics=false&with_issues_enabled=false&with_merge_requests_enabled=false>; rel=\"first\", <https://gitlab.com/api/v4/projects?archived=false&membership=true&order_by=created_at&owned=false&page=2&per_page=100&simple=false&sort=desc&starred=false&statistics=false&with_issues_enabled=false&with_merge_requests_enabled=false>; rel=\"last\"")

(deftest parse-link-header-test
  
  (is (= {:next "https://gitlab.com/api/v4/projects?archived=false&membership=true&order_by=created_at&owned=false&page=2&per_page=100&simple=false&sort=desc&starred=false&statistics=false&with_issues_enabled=false&with_merge_requests_enabled=false"
          :first "https://gitlab.com/api/v4/projects?archived=false&membership=true&order_by=created_at&owned=false&page=1&per_page=100&simple=false&sort=desc&starred=false&statistics=false&with_issues_enabled=false&with_merge_requests_enabled=false"
          :last "https://gitlab.com/api/v4/projects?archived=false&membership=true&order_by=created_at&owned=false&page=2&per_page=100&simple=false&sort=desc&starred=false&statistics=false&with_issues_enabled=false&with_merge_requests_enabled=false"}
         
         (link-header-map links-header))))
