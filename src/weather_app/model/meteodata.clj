(ns weather-app.model.meteodata
  (:require [clojure.java.jdbc :as jdbc]))

(defn save [db meteo]
  (try
    (first (jdbc/insert! db :meteodata meteo))
    (catch Exception e
      (throw (ex-info (ex-message e)
                      {:message "Save meteodata error"
                       :data meteo}
                      e)))))
