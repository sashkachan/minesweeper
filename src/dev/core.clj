(ns dev.core
  (:require ring.adapter.jetty
            [compojure.core :refer [GET defroutes]]))

(defonce server (atom nil))

(defn run
  [routesfn]
  (reset! server (ring.adapter.jetty/run-jetty routesfn {:port 8080 :join? false})))


(defn cleanup-refresh [routesfn]
  (.stop @server)
  (run routesfn))
