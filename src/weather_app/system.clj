(ns weather-app.system
  (:require [hikari-cp.core :as cp]
            [integrant.core :as ig]
            [ring.adapter.jetty :as jetty]
            [weather-app.handler :as handler]))

(def config
  {::server  {:handler (ig/ref ::handler)
              :port    (or (some-> (System/getenv "HTTP_PORT")
                                   (Integer/parseInt))
                           8080)}
   ::handler {:api-key (System/getenv "API_KEY")
              :db      (ig/ref ::db)}
   ::db      {:adapter       "postgresql"
              :database-name (System/getenv "POSTGRES_DB")
              :server-name   (System/getenv "POSTGRES_HOST")
              :username      (System/getenv "POSTGRES_USER")
              :password      (System/getenv "POSTGRES_PASSWORD")
              :port-number   (System/getenv "POSTGRES_PORT")}})

(defmethod ig/init-key ::server [_ {:keys [handler port]}]
  (prn (str "Server is launching on port: " port))
  (jetty/run-jetty handler {:port port :join? false}))

(defmethod ig/init-key ::handler [_ {:keys [api-key db]}]
  (fn [req]
    (handler/app-handler (assoc req :ctx {:api-key api-key
                                          :db db}))))

(defmethod ig/init-key ::db [_ config]
  {:datasource (cp/make-datasource config)})


(defmethod ig/halt-key! ::server [_ server]
  (.stop server))

(defmethod ig/halt-key! ::db [_ db-opts]
  (-> db-opts
      :datasource
      cp/close-datasource))

(defn start []
  (ig/init config))

(defn stop [sys]
  (ig/halt! sys))


(comment
  (def system
    (start))

  (stop system)
)
