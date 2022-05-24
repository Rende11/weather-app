(ns weather-app.core
  (:require [weather-app.system :as system]))

(defn -main [& args]
  (system/start))
