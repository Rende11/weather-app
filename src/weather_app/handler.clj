(ns weather-app.handler
  (:require [reitit.ring :as r]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params :refer [wrap-params]]
            [weather-app.weather :as weather]))


(def handler
  (r/ring-handler
   (r/router
    ["/weather/{city}" {:get (fn [req]
                               (def r req)

                               (let [city (-> req :path-params :city)
                                     api-key (-> req :ctx :api-key)
                                     weather-info (weather/get-weather
                                                   {:city city
                                                    :api-key api-key})]
                                 {:status 200
                                  :body {:message "OK"
                                         :data weather-info}}))}])
   (r/routes
    (r/create-default-handler
     {:not-found (constantly {:status 404 :body "Not found"})}))))


(def app-handler
  (-> handler
      (wrap-json-response {:pretty true})
      (wrap-keyword-params)
      (wrap-params)))
