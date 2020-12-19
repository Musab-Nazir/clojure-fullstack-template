(ns musab.handler
  (:require [reitit.ring :as ring]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.coercion.spec :as spec-coercion]
            [muuntaja.core :as m]
            #_[clojure.spec.alpha :as spec]))

;; create muuntaja instance
(def muuntaja-instance (m/create))

(defn base-handler [_]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "hello HTTP!"})

(defn ip-handler [request]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body (:remote-addr request)})

(defn user-handler [_]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "This is the users page"})

(def routes
  ["/api"
   ["/ping" {:get base-handler
             :name ::ping}]

   ["/ip" {:get ip-handler
           :name ::ip}]

   ["/math" {:get {:parameters {:query {:x int?, :y int?}}
                   :responses {200 {:body {:total int?}}}
                   :handler (fn [{{{:keys [x y]} :query} :parameters}]
                              {:status 200
                               :body {:total (+ x y)}})}}]

   ["/admin"
    ["/users" {:get user-handler
               :post user-handler}]]])

(def app
  (ring/ring-handler
   (ring/router
    routes
    {:data {:coercion spec-coercion/coercion
            :muuntaja muuntaja-instance
            :middleware [;; query params and form params
                         parameters/parameters-middleware
                         ;; encoding response body
                         muuntaja/format-response-middleware
                         ;; coerce execeptions
                         coercion/coerce-exceptions-middleware
                         ;; decoding request body
                         muuntaja/format-request-middleware
                         ;; coercing request params
                         coercion/coerce-request-middleware
                         ;; coercing response params
                         coercion/coerce-response-middleware]}})))


