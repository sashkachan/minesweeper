(ns minesweeper.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [taoensso.carmine :as car]
            [minesweeper.helpers :as help]
            [clojure.string :as str]
            [clojure.data.json :as json]
            [minesweeper.data :as data]
            [liberator.core :refer [defresource resource]]))

;; todo: create config
(def field-options {:easy {:size [3 3] :bombs 3}
                    :medium {:size [9 9] :bombs 27}
                    :hard {:size [16 16] :bombs 99}})

(defn cell [dim is-bomb number]
  {:coord dim
   :is-bomb is-bomb
   :number number})

(defn get-neighbours-wmax [[xmax ymax] [x y]]
  (for [xc [(- x 1) x (+ x 1)]
        yc [(- y 1) y (+ y 1)]
        :when (and (not (and (= xc x) (= yc y)))
                   (>= xc 0)
                   (>= yc 0)
                   (< xc xmax)
                   (< yc ymax))]
    [xc yc]))

(defn flip-cells
  "Flips affected cells when a move is made"
  [uid move])

(defn make-a-move
  "Higher level function that calculates changes upon a move"
  [uid move])

(defn game-over
  "Checks if the game is over one way or another"
  [uid move])

(defn generate-minefield
  "Returns randomly generated positions of mines on a field"
  [[x y :as dim] bombs-count]
  (println bombs-count)
  (println dim)
  (if (and (number? bombs-count) (> bombs-count 0) (number? x) (number? y))
    (help/get-unique-rand-pair-coll bombs-count (apply max [x y]))
    (throw (new IllegalArgumentException))))

(defn game-start
  "Returns new field with randomly generated bombs"
  ([{[x y] :size :as spec}]
   (if-let [minefield (generate-minefield [x y] (:bombs spec))]
     (let [get-neighbours (partial get-neighbours-wmax (:size spec))
           is-bomb? (fn [dim] (not (nil? (help/find-first #(= dim %) minefield))))]
       (for [xc (range x) yc (range y)
             :let [dim [xc yc]]]
         (if (is-bomb? dim)
           (cell dim true nil)
           (cell dim false (->> (get-neighbours dim)
                                (filter is-bomb?)
                                (count)))))))))

(defn wrap-response [body]
  (json/write-str {:result body}))

(defresource game-start-res [level]
  :available-media-types ["application/json"]
  :handle-ok (fn [_] (wrap-response 
                     (if-let [spec ((keyword level) field-options)]
                       (game-start spec)
                       {:error "No such level"}))))

(defroutes app
  (POST "/move" [] (resource :available-media-types ["text/html"]
                           :handle-ok  "Not implemented"))
  (GET "/game-start/:level" [level] (game-start-res level))
  (route/not-found "404"))
