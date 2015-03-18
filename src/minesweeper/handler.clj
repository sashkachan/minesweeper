(ns minesweeper.handler
  (:require [clojure.tools.namespace.repl :refer [refresh]]
            [compojure.route :refer [resources not-found]]
            ring.adapter.jetty
            [net.cgrand.enlive-html :as enlive]
            [compojure.core :refer [GET defroutes]]
            [clojure.java.io :as io]))

(defroutes site
  (GET "/" [] "Hello World")
  (not-found "Not Found"))
