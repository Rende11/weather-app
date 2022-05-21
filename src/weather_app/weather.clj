(ns weather-app.weather
  (:require [clj-http.client :as client]))

(def endpoint
  "https://api.openweathermap.org/data/2.5/weather")

(defn weather-req [{:keys [city api-key]}]
  (try
    (client/get endpoint
                {:query-params
                 {:q city
                  :appid api-key
                  :units "metric"}
                 :as :json})
    (catch Exception e
         (throw (ex-info (ex-message e)
                         {:message "Weather search failed"
                          :data {:city city}}
                         e)))))

(defn get-weather [params]
  (let [resp (weather-req params)]
    {:city (-> resp :body :name)
     :temperature (-> resp :body :main :temp)}))
