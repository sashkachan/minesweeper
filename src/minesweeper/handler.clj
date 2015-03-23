(ns minesweeper.handler
  (:require [compojure.route :refer [resources not-found]]
            ring.adapter.jetty
            [ring.middleware.params :refer [wrap-params]]
             
            [liberator.core :refer [resource defresource]]
            [compojure.core :refer [GET defroutes ANY]]))

(defroutes app
  (ANY "/foo" [] (resource :available-media-types ["application/json"]
                           :handle-ok "{var: \"wa\"}")))
(def handler 
  (-> app 
      wrap-params))

