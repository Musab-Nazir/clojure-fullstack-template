(ns musab.handler
  (:require [reitit.ring :as ring]))

(declare
 wrap
 base-handler
 user-handler)

(def app
  (ring/ring-handler
   (ring/router
    ["/api" {:middleware [[wrap :api]]}
     ["/ping" {:get base-handler
               :name ::ping}]
     ["/admin" {:middleware [[wrap :admin]]}
      ["/users" {:get user-handler
                 :post user-handler}]]])))

(defn base-handler [_]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "hello HTTP!"})

(defn user-handler [_]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "This is the users page"})

(defn wrap [handler id]
  (fn [request]
    (update (handler request) :wrap (fnil conj '()) id)))


