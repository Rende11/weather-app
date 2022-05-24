(ns weather-app.system
  (:require [aero.core :refer (read-config)]
            [clojure.java.io :as io]
            [hikari-cp.core :as cp]
            [integrant.core :as ig]
            [ring.adapter.jetty :as jetty]
            [weather-app.handler :as handler]))


(defmethod aero.core/reader 'ig/ref
  [_opts _tag value]
  (integrant.core/ref value))

(defmethod ig/init-key :app/server [_ {:keys [handler port]}]
  (prn (str "Server is launching on port: " port))
  (jetty/run-jetty handler {:port port :join? false}))

(defmethod ig/init-key :app/handler [_ {:keys [api-key db]}]
  (fn [req]
    (handler/app-handler (assoc req :ctx {:api-key api-key :db db}))))

(defmethod ig/init-key :app/db [_ config]
  {:datasource (cp/make-datasource config)})


(defmethod ig/halt-key! :app/server [_ server]
  (.stop server))

(defmethod ig/halt-key! :app/db [_ db-opts]
  (-> db-opts
      :datasource
      cp/close-datasource))


(defn start []
  (-> (io/resource "config.edn")
      read-config
      ig/init))

(defn stop [sys]
  (ig/halt! sys))


(comment
  (def system
    (start))

  (stop system)
)
