(ns musab.handler
  (:require [reitit.ring :as ring]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.coercion.spec :as spec-coercion]
            [muuntaja.core :as m]
            [ring.middleware.cors :refer [wrap-cors]]))

;; create muuntaja instance
(def muuntaja-instance 
  (m/create (-> m/default-options
                (assoc :default-format "application/edn"))))

(defn base-handler [_]
  {:status  200
   :headers {"content-type" "text/html"}
   :body    "Hello :)"})

(defn ip-handler [request]
  {:status 200
   :headers {"content-type" "application/edn"}
   :body {:ip (:remote-addr request)}})

(def routes
  [
   ["/" {:get base-handler}]

   ["/ip" {:get ip-handler
           :name ::ip}]

   ["/math" {:get {:parameters {:query {:x int?, :y int?}}
                   :responses {200 {:body {:total int?}}}
                   :handler (fn [{{{:keys [x y]} :query} :parameters}]
                              {:status 200
                               :body {:total (+ x y)}})}}]])

(def app
  (ring/ring-handler
   (ring/router
    routes
    {:data {:coercion spec-coercion/coercion
            :muuntaja muuntaja-instance
            :middleware [;; Cross origin resourse aka cors
                         [wrap-cors
                          :access-control-allow-origin [#".*"]
                          :access-control-allow-methods [:get :put :post :delete]]
                         ;; query params and form params
                         parameters/parameters-middleware
                         muuntaja/format-negotiate-middleware
                         ;; encoding response body
                         muuntaja/format-response-middleware
                         ;; coerce execeptions
                         coercion/coerce-exceptions-middleware
                         ;; decoding request body
                         muuntaja/format-request-middleware
                         ;; coercing request params
                         coercion/coerce-request-middleware
                         ;; coercing response params
                         coercion/coerce-response-middleware]}})
   (ring/routes
    (ring/create-resource-handler {:path "/" :root "/public"})
    (ring/create-default-handler))))


