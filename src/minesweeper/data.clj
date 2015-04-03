 (ns minesweeper.data
   (:require [taoensso.carmine :as car]
             [minesweeper.helpers :as help]
             [clojure.string :as str]
             [clojure.data.json :as json]))

(def conn {:pool {} :spec {:host "127.0.0.1" :port 6379}})
(defmacro wcar* [& body] `(car/wcar conn ~@body))

(def json-pr [:key-fn keyword])

(defn store-game [uuid game]
  (let [game-id (str/join ["game" "-" uuid])]
    (wcar* (car/set game-id (json/write-str game)))
    game-id))

(defn get-game [uuid]
  (let [game-id (str/join ["game" "-" uuid])
        game (wcar* (car/get game-id))]
    (if (not (nil? game))
      (apply json/read-str game json-pr))))

(defn store-game-state [uuid game]
  (let [games-list (str/join ["game-" uuid "-games"])]
    (wcar* (car/lpush games-list (json/write-str game)))))

(defn get-last-game-state [uuid]
  (let [games-list (str/join ["game-" uuid "-games"])]
    (apply json/read-str (wcar* (car/lpop games-list)) json-pr)))


(defn get-flipped [uuid]
  (let [flipped-uuid (str/join ["flipped" "-" uuid])
        flipped-cells (wcar* (car/get flipped-uuid))]
    (if (nil? flipped-cells)
      (apply json/read-str "[]" json-pr)
      (apply json/read-str flipped-cells json-pr))))

(defn set-flipped [uuid flipped]
  (let [flipped-uuid (str/join ["flipped" "-" uuid])]
    (wcar* (car/set flipped-uuid (json/write-str flipped)))))
