(ns gl-cards.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs-http.client :as http]
            [cljs.core :refer [js->clj]]
            [cljs.core.async :refer [<!]]
            [cljs.core.async :refer [<!]]))

(enable-console-print!)

(defonce app-state (atom {}))

(defn poll-cards! []
  (go
    (let [response (<! (http/get "/cards"))
          body (:body response)]
      (swap! app-state assoc :cards (js->clj body)))))

;; FIXME
(poll-cards!)

(defn pipeline-card [data owner]
  (om/component
   (dom/span nil
             (dom/div nil (:path data))
             (dom/div nil (:ref data))
             (dom/div nil (:status (:pipeline data))))))

(defn cards-list [data owner]
  (om/component
   (dom/div nil
            (om/build-all pipeline-card data))))

(defn root-component [data owner]
  (reify
    om/IRender
    (render [_]
      (om/build cards-list (:cards data)))))

(defn render []
  (om/root
   root-component
   app-state
   {:target (js/document.getElementById "app")}))
