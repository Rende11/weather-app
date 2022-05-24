(ns weather-app.handler
  (:require [reitit.ring :as r]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params :refer [wrap-params]]
            [weather-app.model.meteodata :as meteo]
            [weather-app.weather :as weather]))


(def handler
  (r/ring-handler
   (r/router
    ["/weather/{city}" {:get (fn [{:keys [path-params ctx]}]
                               (let [{:keys [db api-key]} ctx
                                     meteo-data (weather/get-weather
                                                 {:city (:city path-params)
                                                  :api-key api-key})
                                     saved (dissoc (meteo/save db meteo-data) :id)]
                                 {:status 200
                                  :body saved}))}])
   (r/routes
    (r/create-default-handler
     {:not-found (constantly {:status 404 :body "Not found"})}))))


(defn wrap-exception [handler]
  (fn [req]
    (try
      (handler req)
      (catch Throwable e
        (println (ex-message e) (ex-data e))
        {:status 500
         :body "Something went wrong, please try again later"}))))

(def app-handler
  (-> handler
      (wrap-json-response {:pretty true})
      (wrap-keyword-params)
      (wrap-params)
      (wrap-exception)))
