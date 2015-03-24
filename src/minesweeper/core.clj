(ns minesweeper.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [taoensso.carmine :as car]
            [minesweeper.helpers :as help]))

;; todo: move 
(def conn {:pool {} :spec {:host "127.0.0.1" :port 6379}})
(defmacro wcar* [& body] `(car/wcar conn ~@body))

(def field-options [{:size [3 3] :bombs 3}
                    {:size [9 9] :bombs 27}
                    {:size [16 16] :bombs 99}])

(defroutes app
  (POST "/move" [] "")
  (GET "/startgame" [] "")
  (route/not-found ""))


(defn cell [dim is-bomb]
  {:coord dim
   :is-bomb is-bomb})

;; flipped: [[1 1] [1 2] [1 3] ... ]
;; bombs: [[1 1]]
(defn game-start
  "Returns new field with randomly generated bombs"
  [[x y] spec]
  (if-let [minefield (generate-minefield [x y] spec)]
    (let [field (for [xc (range x) yc (range y)
                      :let [is-bomb (not (nil? (help/find-first #(= [xc yc] %) minefield)))]]
                  (cell [xc yc] is-bomb))]
      )))

(defn get-neighbours [[x y] xmax ymax]
  (for [xc [(- x 1) x (+ x 1)]
        yc [(- y 1) y (+ y 1)]
        :when (and (not (and (= xc x) (= yc y)))
                   (>= xc 0)
                   (>= yc 0)
                   (< xc xmax)
                   (< yc ymax))]
    [xc yc]))

;; [{:coord [0 0]
;;   :bomb true
;;   :number nil
;;   :visible false}
;;  {:coord [1 0]
;;   :bomb true
;;   :number 1
;;   :visible false} ... ]

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
  [dim spec]
  (if-let [chosen (help/find-first #(= (:size %) dim) spec)]
    (help/get-unique-rand-pair-coll (:bombs chosen) (apply max dim))
    (throw (new IllegalArgumentException))))

