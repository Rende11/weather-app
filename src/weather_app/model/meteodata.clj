(ns weather-app.model.meteodata
  (:require [clojure.java.jdbc :as jdbc]))

(defn save [db meteo]
  (try
    (jdbc/insert! db :meteodata meteo)
    (catch Exception e
      (throw (ex-info (ex-message e)
                      {:message "Save request data error"
                       :data meteo}
                      e)))))
