(ns minesweeper.helpers)

(defn find-first [f coll]
  (first (filter f coll)))

(defn in? [coll el]
  (some #(= % el) coll))

(defn not-in? [coll el]
  (not (in? coll el)))

(defn get-rand-pair [limit]
  (let [rand-num (partial rand-int limit)]
    (cons (vector (rand-num) (rand-num))
          (lazy-seq (get-rand-pair limit)))))

(defn get-unique-rand-pair-coll
  ([total up-bound]
   (get-unique-rand-pair-coll total up-bound []))
  ([total up-bound aggr]
   (if (= (count aggr) total)
     aggr
     (let [new-pair (first (get-rand-pair up-bound))]
       (if (not (nil? (find-first #(= new-pair %) aggr)))
         (get-unique-rand-pair-coll total up-bound aggr)
         (get-unique-rand-pair-coll total up-bound (into aggr [new-pair])))))))

(defn get-uuid []
  (str (java.util.UUID/randomUUID)))


