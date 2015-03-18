(ns minesweeper.handler
  (:require [compojure.route :refer [resources not-found]]
            ring.adapter.jetty
            [compojure.core :refer [GET defroutes]]))

(defroutes site
  (resources "/")
  (not-found "Not Found"))

