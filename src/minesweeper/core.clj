(ns minesweeper.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [taoensso.carmine :as car]
            [minesweeper.helpers :as help]
            [clojure.string :as str]
            [clojure.data.json :as json]
            [minesweeper.data :as data]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.resource :refer [wrap-resource]]))

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
  [game move]
  (when (or (has-bomb? game move)
            (nil? (help/find-first #(nil? (:number %)) game)))
    true))

(defn attach-flipped-cells [game flipped]
  (map (fn [el] (assoc el :flipped (help/in? flipped el)))))

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

(defn get-board-size [board]
  (let [coords (apply map vector (map #(:coord %) board))]
    (println coords)
    (vector (inc (apply max (first coords))) (inc (apply max (second coords))))))

(defn open-region
  ([game move]
   (open-region [] [] game move))
  ([aggr-opened unprocessed game move]
   (let [is-zero-cell (fn [cell] (= 0 (:number (get-cell game cell))))
         not-in-opened (partial filter (partial help/not-in? aggr-opened))
         neighbours (not-in-opened (get-neighbours-wmax (get-board-size game) move))
         new-unprocessed-zeroes (concat unprocessed
                                        (filter is-zero-cell neighbours))
         all-open-cells (concat (not-in-opened neighbours)
                              aggr-opened)]
     (cond
       (and (= (count aggr-opened) 0) (not (is-zero-cell move))) (vector move)
       (empty? new-unprocessed-zeroes) all-open-cells
       :else (if (not (is-zero-cell move))
               (open-region aggr-opened
                            (rest unprocessed)
                            game
                            (first unprocessed))
               (open-region all-open-cells
                            (rest new-unprocessed-zeroes)
                            game
                            (first new-unprocessed-zeroes)))))))

(defn wrap-response [body]
  (json/write-str {:result body}))

(defn game-start-res [level]
  (if-let [spec ((keyword level) field-options)]
    (let [game (game-start spec)
          uuid (help/get-uuid)]
      (data/store-game uuid game)
      {:uid uuid
       :field spec})
    {:error "No such level"}))

(defn move-res [uuid move]
  (if-let [initial-game (data/get-game uuid)]
    (if (game-over? initial-game move)
      {:game "over"}
      (let [flipped (data/get-flipped uuid)
            new-flipped (open-region initial-game move)
            all-flipped (concat new-flipped flipped)]        
        (data/set-flipped uuid all-flipped)
        {:game (wrap-game initial-game all-flipped)}))))

(def app
  (->> (defroutes approutes
         (POST "/move/:uuid" {{uuid :uuid :as params} :params}
               (wrap-response (move-res uuid (json/read-str (get params "move")))))
         
         (GET "/game-start/:level" [level] (wrap-response (game-start-res level)))
         (route/resources "/")
         (route/not-found "404"))
       wrap-params))
