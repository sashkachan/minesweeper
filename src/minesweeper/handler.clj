(ns minesweeper.handler
  (:require [compojure.route :refer [resources not-found]]
            ring.adapter.jetty
            [ring.middleware.params :refer [wrap-params]]
            [compojure.core :refer [GET defroutes ANY]]))

