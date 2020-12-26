(ns musab.web.core
  (:require
   [reagent.core :as reagent :refer [atom]]
   [reagent.dom :as rdom]
   [clerk.core :as clerk]
   [accountant.core :as accountant]
   [reagent.session :as session]
   [reitit.frontend :as reitit]
   [ajax.core :refer [GET]]))

(declare home-page
         ip-page
         math-page
         users-page
         item-page
         items-page)

;; Page state
(def page-state (atom {}))

;; -------------------------
;; Routes

(def router
  (reitit/router
   [["/" :index]
    ["/ip" :ip]
    ["/math" :math]
    ["/users" :users]
    ["/items"
     ["" :items]
     ["/:item-id" :item]]]))

(defn path-for [route & [params]]
  (if params
    (:path (reitit/match-by-name router route params))
    (:path (reitit/match-by-name router route))))


;; -------------------------
;; Translate routes -> page components

(defn page-for [route]
  (case route
    :index home-page
    :math  math-page
    :ip    ip-page
    :users users-page
    :items items-page
    :item  item-page))

;; -------------------------
;; Page mounting component

(defn current-page []
  (fn []
    (let [page (:current-page (session/get :route))]
      [:div
       [:header
        [:p [:a {:href (path-for :index)} "Home"]]]
       [page]])))

;; -------------------------
;; Page components

(defn home-page []
  (fn []
    [:span.main
     [:h1 "Welcome to React"]
     [:ul
      [:li [:a {:href (path-for :items)} "Items of react"]]
      [:li [:a {:href (path-for :users)} "List of users"]]
      [:li [:a {:href (path-for :ip)} "My IP"]]
      [:li [:a {:href (path-for :math)} "1 + 2 (testing query params)"]]]]))

(defn ip-page []
  (GET "http://localhost:8080/ip"
    {:handler (fn [x] (swap! page-state assoc :ip (:ip x)))})
  (fn []
    [:div (str "Your IP Address is " (@page-state :ip))]))


(defn math-page []
  (GET
    "http://localhost:8080/math?x=1&y=2"
    {:handler (fn [x]
                (swap! page-state assoc :total (:total x)))})
  (fn []
    [:div (str "1 + 2 =" (@page-state :total))]))

(defn users-page []
  (GET "http://localhost:8080/users"
    {:handler (fn [res]
                (swap! page-state assoc :users (res :users)))})
  (fn []
    [:div "Total Users:"
     [:table>tbody (map (fn [user]
                          (let [id (:id user)
                                name (:name user)
                                email (:email user)]
                            [:tr {:key id}
                             [:td id]
                             [:td name]
                             [:td email]]))
                        (@page-state :users))]]))

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