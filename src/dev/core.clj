(ns dev.core
  (:require ring.adapter.jetty
            [compojure.core :refer [GET defroutes]]))


(defn run
  [routesfn]
  (def ^:private server (atom
                         (ring.adapter.jetty/run-jetty routesfn {:port 8080 :join? false})))
  server)

(defn cleanup-refresh [routesfn]
  (.stop @server)
  (reset! server nil)
  (run routesfn))
