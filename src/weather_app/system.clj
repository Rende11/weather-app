(ns weather-app.system
  (:require [ring.adapter.jetty :as jetty]
            [integrant.core :as ig]
            [weather-app.handler :as handler]))

(def config
  {::server  {:handler (ig/ref ::handler)
              :port (or (some-> (System/getenv "HTTP_PORT")
                            (Integer/parseInt))
                    8080)}
   ::handler {:api-key (System/getenv "API_KEY")}})

(defmethod ig/init-key ::server [_ {:keys [handler port]}]
  (prn (str "Server is launching on port: " port))
  (jetty/run-jetty handler {:port port :join? false}))

(defmethod ig/init-key ::handler [_ {:keys [api-key]}]
  (fn [req]
    (handler/app-handler (assoc req :ctx {:api-key api-key}))))


(defmethod ig/halt-key! ::server [_ server]
  (.stop server))

(defn start []
  (ig/init config))

(defn stop [sys]
  (ig/halt! sys))

(comment
  (def system
    (start))

  (stop system)

  (do
    (stop system)
    (def system
      (start))
    )
  )
