 (ns minesweeper.data
   (:require [taoensso.carmine :as car]
             [minesweeper.helpers :as help]
             [clojure.string :as str]
             [clojure.data.json :as json]))

(def conn {:pool {} :spec {:host "127.0.0.1" :port 6379}})
(defmacro wcar* [& body] `(car/wcar conn ~@body))

(defn store-game [game uuid]
  (wcar* (car/set (str/join ["game" "-" uuid]) (json/write-str game))))
