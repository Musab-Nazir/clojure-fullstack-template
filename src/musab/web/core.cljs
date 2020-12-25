(ns musab.web.core
  (:require
   [reagent.core :as reagent :refer [atom]]
   [reagent.dom :as rdom]
   [reagent.session :as session]
   [reitit.frontend :as reitit]
   [clerk.core :as clerk]
   [accountant.core :as accountant]
   [ajax.core :refer [GET]]))

;; -------------------------
;; Routes

(def router
  (reitit/router
   [["/" :index]
    ["/ip" :ip]
    ["/math" :math]
    ["/items"
     ["" :items]
     ["/:item-id" :item]]]))

(defn path-for [route & [params]]
  (if params
    (:path (reitit/match-by-name router route params))
    (:path (reitit/match-by-name router route))))

;; -------------------------
;; Page components

(defn home-page []
  (fn []
    [:span.main
     [:h1 "Welcome to react"]
     [:ul
      [:li [:a {:href (path-for :items)} "Items of react"]]
      [:li [:a {:href (path-for :ip)} "My IP"]]
      [:li [:a {:href (path-for :math)} "1 + 2"]]]]))

(defn ip-page []
  #(GET "localhost:8888/ip" {:handler (fn [x] (print x))}))

(defn math-page []
  #(GET "localhost:8888/math?x=1&y2" {:handler (fn [x] 
                                                 (print x)
                                                 [:div "the result should be in the console"])}))

(defn items-page []
  (fn []
    [:span.main
     [:h1 "The items of react"]
     [:ul (map (fn [item-id]
                 [:li {:name (str "item-" item-id) 
                       :key (str "item-" item-id)}
                  [:a {:href 
                       (path-for :item {:item-id item-id})} 
                   "Item: " item-id]])
               (range 1 60))]]))


(defn item-page []
  (fn []
    (let [routing-data (session/get :route)
          item (get-in routing-data [:route-params :item-id])]
      [:span.main
       [:h1 (str "Item " item " of react")]
       [:p [:a {:href (path-for :items)} "Back to the list of items"]]])))


;; -------------------------
;; Translate routes -> page components

(defn page-for [route]
  (case route
    :index #'home-page
    :ip ip-page
    :math math-page
    :items #'items-page
    :item #'item-page))


;; -------------------------
;; Page mounting component

(defn current-page []
  (fn []
    (let [page (:current-page (session/get :route))]
      [:div
       [:header
        [:p [:a {:href (path-for :index)} "Home"] " | "
         [:a {:href (path-for :about)} "About react"]]]
       [page]])))

;; -------------------------
;; Initialize app

(defn mount-root []
  (rdom/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (clerk/initialize!)
  (accountant/configure-navigation!
   {:nav-handler
    (fn [path]
      (let [match (reitit/match-by-path router path)
            current-page (:name (:data  match))
            route-params (:path-params match)]

        (reagent/after-render clerk/after-render!)
        (session/put! :route {:current-page (page-for current-page)
                              :route-params route-params})
        (clerk/navigate-page! path)))
    :path-exists?
    (fn [path]
      (boolean (reitit/match-by-path router path)))})
  (accountant/dispatch-current!)
  (mount-root))