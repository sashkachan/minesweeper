(ns minesweeper.handler
  (:require [minesweeper.core :as game]
            [compojure.route :as route]
            ring.adapter.jetty
            [ring.middleware.params :refer [wrap-params]]
            [compojure.core :refer [GET POST defroutes]]
            [ring.middleware.params :refer [wrap-params]]
            [clojure.data.json :as json]))

(defn wrap-response [body]
  (json/write-str {:result body}))

(def app
  (->> (defroutes approutes
         (POST "/move/:uuid" {{uuid :uuid :as params} :params}
               (wrap-response (game/move-res uuid (json/read-str (get params "move")))))         
         (GET "/game-start/:level" [level] (wrap-response (game/game-start-res level)))
         (route/resources "/")
         (route/not-found "404"))
       wrap-params))

