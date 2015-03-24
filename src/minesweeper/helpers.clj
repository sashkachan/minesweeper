(ns minesweeper.helpers)

(defn find-first [f coll]
  (first (filter f coll)))

(defn get-rand-pair [limit]
  (let [rand-num (partial rand-int limit)]
    (vector (rand-num) (rand-num))))

(defn get-unique-rand-pair-coll
  ([total up-bound]
   (get-unique-rand-pair-coll total up-bound []))
  ([total up-bound aggr]
   (if (= (count aggr) total)
     aggr
     (let [new-pair (get-rand-pair up-bound)]
       (if (not (nil? (find-first #(= new-pair %) aggr)))
         (get-unique-rand-pair-coll total up-bound aggr)
         (get-unique-rand-pair-coll total up-bound (into aggr [new-pair])))))))
