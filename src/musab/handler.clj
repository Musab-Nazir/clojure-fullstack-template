(ns musab.handler
  (:require [reitit.ring :as ring]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.coercion.spec :as spec-coercion]
            [muuntaja.core :as m]
            [hiccup.page :refer [include-js include-css html5]]
            [config.core :refer [env]]
            [ring.middleware.cors :refer [wrap-cors]]))

;; create muuntaja instance
(def muuntaja-instance (m/create))

(def mount-target
  [:div#app
   [:h2 "Welcome to react"]
   [:p "please wait while Figwheel/shadow-cljs is waking up ..."]
   [:p "(Check the js console for hints if nothing exciting happens.)"]])

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-css (if (env :dev) "/css/site.css" "/css/site.min.css"))])

(defn loading-page []
  (html5
   (head)
   [:body {:class "body-container"}
    mount-target
    (include-js "/js/app.js")
    [:script "musab.react.core.init_BANG_()"]]))

(defn base-handler [_]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    (loading-page)})

(defn ip-handler [request]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body (:remote-addr request)})

(defn user-handler [_]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "This is the users page"})

(def routes
  [
   ["/" {:get base-handler}]

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
            :middleware [;; Cross origin resourse aka cors
                         [wrap-cors :access-control-allow-origin ["localhost:3001"]
                          :access-control-allow-methods [:get :post :delete :put]]
                         ;; query params and form params
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
                         coercion/coerce-response-middleware]}})
   (ring/routes
    (ring/create-resource-handler {:path "/" :root "/public"})
    (ring/redirect-trailing-slash-handler)
    (ring/create-default-handler))))


