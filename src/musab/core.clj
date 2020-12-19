(ns musab.core
  (:require 
   ;; third party libs
   [org.httpkit.server :as app-server]
   [taoensso.timbre :as timbre :refer [log info warn error fatal]]
   
   ;; app specific
   [musab.handler :as handler])
  (:gen-class))

(defonce app-server-instance (atom nil))

(defn app-server-start
  "Starts the backend server and logs the time of start"
  [http-port]
  (info "Server started")
  (reset! app-server-instance (app-server/run-server 
                               handler/app {:port http-port})))

(defn app-server-stop
  "Stops the backend server after waiting 100ms and logs the time of shutdown."
  []
  (when-not (nil? @app-server-instance)
    (@app-server-instance :timeout 100)
    (reset! app-server-instance nil)
    (warn "Server shutdown")))

(defn app-server-restart
  "Calls app-server-stop, then calls app-server-start if the port provided
   is a positive int"
  [port]
  (app-server-stop)
  (when (pos-int? port)
    (app-server-start port)))

(defn -main
  "Tries to fetch the port from either the porvided args or the system env
   variable. If neither exists defaults to port 8888 and starts the server"
  [& [http-port]]
  (let [port (Integer. (or http-port (System/getenv "PORT") "8888"))]
    (app-server-start port)))


(comment
  ;; start the app
  (-main 8080)
  ;; restart app
  (app-server-restart 8080)
  ;; stop the server
  (app-server-stop))
