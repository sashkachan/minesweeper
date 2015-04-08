(ns minesweeper.core
  (:require [minesweeper.helpers :as help]
            [clojure.string :as str]
            [clojure.data.json :as json]
            [minesweeper.data :as data]))

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

(def coords (partial map (fn [el] (:coord el))))
(def coords-of (fn [board pred] (coords (filter pred board))))

(defn game-won? [board flipped]
  (let [coords-of-board (partial coords-of board)
        mines (coords-of-board #(= true (:is-bomb %)))
        not-mines (coords-of-board #(not (= true (:is-bomb %))))]
    (if (and (empty? (filter (partial help/in? mines) flipped))
             (= (count not-mines) (count flipped)))
      true
      false)))

(defn game-lost?
  "Checks if the game is over one way or another"
  [board move]
  (when (or (has-bomb? board move)
            (nil? (help/find-first #(nil? (:number %)) board)))
    true))

(defn attach-flipped-cells [board flipped]
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

(defn wrap-game
  ([board]
   (wrap-game board (map #(:coord %) board)))
  ([board flipped]
   (map (fn [{dim :coord}]
          (let [board-cell (get-cell board dim)
                flipped (not (nil? (help/find-first #(= dim %) flipped)))
                bomb (if flipped (:is-bomb board-cell) nil)
                number (if flipped (:number board-cell) nil)]
            (cell dim bomb number))) board)))

(defn get-board-size [board]
  (let [coords (apply map vector (map #(:coord %) board))]
    (vector (inc (apply max (first coords))) (inc (apply max (second coords))))))


(defn open-region
  ([board move]
   (open-region [] [] board move))
  ([aggr-opened unprocessed board move]
   (let [is-zero-cell (fn [cell] (= 0 (:number (get-cell board cell))))
         not-in-opened (partial filter (partial help/not-in? aggr-opened))
         neighbours (not-in-opened (get-neighbours-wmax (get-board-size board) move))
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
                            board
                            (first unprocessed))
               (open-region all-open-cells
                            (rest new-unprocessed-zeroes)
                            board
                            (first new-unprocessed-zeroes)))))))


(defn game-start-res [level]
  (if-let [spec ((keyword level) field-options)]
    (let [board (game-start spec)
          uuid (help/get-uuid)]
      (data/store-game uuid board)
      {:uid uuid :field spec})
    {:error "No such level"}))

(defn move-res [uuid move]
  (if-let [board (data/get-game uuid)]
    (let [flipped (data/get-flipped uuid)
          new-flipped (open-region board move)
          all-flipped (concat new-flipped flipped)
          lost? (game-lost? board move)
          won? (game-won? board all-flipped)]
      (data/set-flipped uuid all-flipped)
      (if (or lost? won?)
        {:lost lost?
         :won won?
         :game (wrap-game board)}
        
        {:game (wrap-game board all-flipped)}))))
