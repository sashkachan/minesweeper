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
(def field-options {:easy {:size [9 9] :bombs 10}
                    :medium {:size [16 16] :bombs 40}
                    :hard {:size [16 30] :bombs 99}})

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

(defn get-cell [board dim]
  (help/find-first #(= (:coord %) dim) board))

(defn has-bomb? [board dim]
  (= true (:is-bomb (get-cell board dim))))

(defn game-over?
  "Checks if the game is over one way or another"
  [board move]
  (when (has-bomb? board move)
    true))

(defn generate-minefield
  "Returns randomly generated positions of mines on a field"
  [[x y :as dim] bombs-count]
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
             :let [coord [xc yc]]]
         (if (is-bomb? coord)
           (cell coord true nil)
           (cell coord false (->> (get-neighbours coord)
                                  (filter is-bomb?)
                                  (count))))))
     (throw (new IllegalArgumentException)))))

(defn wrap-game [board flipped]
  (map (fn [{dim :coord}]
         (let [board-cell (get-cell board dim)
               flipped (not (nil? (help/find-first #(= dim %) flipped)))
               bomb (if flipped (:is-bomb board-cell) nil)
               number (if flipped (:is-bomb board-cell) nil)]
           (cell dim bomb number))) board))

(defn get-board-max [board]
  (let [coords (map vector (map #(:coord %) board))]
    (vector (apply max (first coords)) (apply max (second coords)))))

(defn open-region
  ([game move]
   (open-region game move []))
  ([game move aggr]
   (let [neighbours (get-neighbours-wmax (get-board-max game) move)
         empty-neighbours (filter #(= 0 (:number %)) neighbours)
         opened-region (filter #(= 0 (:is-bomb %)) neighbours)]
     ;; todo: recursively call open-region with unprocessed neighbouring cells
     )))

(defn handle-move
  ([game]
   (handle-move game '()))
  ([game move]
   (when (game-over? game move)
     (wrap-game game (map #(:coord %) game)))
   
   ))

(defn wrap-response [body]
  (json/write-str {:result body}))

(defresource game-start-res [level]
  :available-media-types ["application/json"]
  :handle-ok (fn [_] (wrap-response
                     (if-let [spec ((keyword level) field-options)]
                       (let [game (game-start spec)
                             uid (help/get-uuid)]
                         (data/store-game uid game)
                         {:uid uid :game (wrap-game game [])})
                       {:error "No such level"}))))

(defroutes app
  (POST "/move" [] (resource :available-media-types ["text/html"]
                             :handle-ok  "Not implemented"))
  (GET "/game-start/:level" [level] (game-start-res level))
  (route/not-found "404"))
