(ns minesweeper.core-test
  (:require [clojure.test :refer :all]
            [minesweeper.core :as mscore]))

(def game1 [{:coord [0 0], :is-bomb false, :number 0}
            {:coord [0 1], :is-bomb false, :number 1}
            {:coord [0 2], :is-bomb false, :number 1}
            {:coord [1 0], :is-bomb false, :number 0}
            {:coord [1 1], :is-bomb false, :number 1}
            {:coord [1 2], :is-bomb true, :number nil}
            {:coord [2 0], :is-bomb false, :number 0}
            {:coord [2 1], :is-bomb false, :number 1}
            {:coord [2 2], :is-bomb false, :number 1}])

(def game2 [{:coord [0 0], :is-bomb false, :number 0}
            {:coord [0 1], :is-bomb false, :number 0}
            {:coord [0 2], :is-bomb false, :number 0}
            {:coord [0 3], :is-bomb false, :number 0}
            {:coord [0 4], :is-bomb false, :number 0}
            {:coord [0 5], :is-bomb false, :number 2}
            {:coord [0 6], :is-bomb true, :number nil}
            {:coord [0 7], :is-bomb false, :number 2}
            {:coord [0 8], :is-bomb false, :number 0}
            {:coord [1 0], :is-bomb false, :number 0}
            {:coord [1 1], :is-bomb false, :number 1}
            {:coord [1 2], :is-bomb false, :number 1}
            {:coord [1 3], :is-bomb false, :number 1}
            {:coord [1 4], :is-bomb false, :number 0}
            {:coord [1 5], :is-bomb false, :number 2}
            {:coord [1 6], :is-bomb true, :number nil}
            {:coord [1 7], :is-bomb false, :number 2}
            {:coord [1 8], :is-bomb false, :number 0}
            {:coord [2 0], :is-bomb false, :number 0}
            {:coord [2 1], :is-bomb false, :number 1}
            {:coord [2 2], :is-bomb true, :number nil}
            {:coord [2 3], :is-bomb false, :number 1}
            {:coord [2 4], :is-bomb false, :number 0}
            {:coord [2 5], :is-bomb false, :number 1}
            {:coord [2 6], :is-bomb false, :number 1}
            {:coord [2 7], :is-bomb false, :number 1}
            {:coord [2 8], :is-bomb false, :number 0}
            {:coord [3 0], :is-bomb false, :number 1}
            {:coord [3 1], :is-bomb false, :number 3}
            {:coord [3 2], :is-bomb false, :number 3}
            {:coord [3 3], :is-bomb false, :number 2}
            {:coord [3 4], :is-bomb false, :number 0}
            {:coord [3 5], :is-bomb false, :number 0}
            {:coord [3 6], :is-bomb false, :number 0}
            {:coord [3 7], :is-bomb false, :number 0}
            {:coord [3 8], :is-bomb false, :number 0}
            {:coord [4 0], :is-bomb false, :number 1}
            {:coord [4 1], :is-bomb true, :number nil}
            {:coord [4 2], :is-bomb true, :number nil}
            {:coord [4 3], :is-bomb false, :number 2}
            {:coord [4 4], :is-bomb false, :number 0}
            {:coord [4 5], :is-bomb false, :number 1}
            {:coord [4 6], :is-bomb false, :number 1}
            {:coord [4 7], :is-bomb false, :number 1}
            {:coord [4 8], :is-bomb false, :number 0}
            {:coord [5 0], :is-bomb false, :number 1}
            {:coord [5 1], :is-bomb false, :number 3}
            {:coord [5 2], :is-bomb true, :number nil}
            {:coord [5 3], :is-bomb false, :number 2}
            {:coord [5 4], :is-bomb false, :number 0}
            {:coord [5 5], :is-bomb false, :number 2}
            {:coord [5 6], :is-bomb true, :number nil}
            {:coord [5 7], :is-bomb false, :number 2}
            {:coord [5 8], :is-bomb false, :number 0}
            {:coord [6 0], :is-bomb false, :number 0}
            {:coord [6 1], :is-bomb false, :number 1}
            {:coord [6 2], :is-bomb false, :number 1}
            {:coord [6 3], :is-bomb false, :number 1}
            {:coord [6 4], :is-bomb false, :number 0}
            {:coord [6 5], :is-bomb false, :number 3}
            {:coord [6 6], :is-bomb true, :number nil}
            {:coord [6 7], :is-bomb false, :number 3}
            {:coord [6 8], :is-bomb false, :number 0}
            {:coord [7 0], :is-bomb false, :number 0}
            {:coord [7 1], :is-bomb false, :number 1}
            {:coord [7 2], :is-bomb false, :number 1}
            {:coord [7 3], :is-bomb false, :number 1}
            {:coord [7 4], :is-bomb false, :number 0}
            {:coord [7 5], :is-bomb false, :number 2}
            {:coord [7 6], :is-bomb true, :number nil}
            {:coord [7 7], :is-bomb false, :number 2}
            {:coord [7 8], :is-bomb false, :number 0}
            {:coord [8 0], :is-bomb false, :number 0}
            {:coord [8 1], :is-bomb false, :number 1}
            {:coord [8 2], :is-bomb true, :number nil}
            {:coord [8 3], :is-bomb false, :number 1}
            {:coord [8 4], :is-bomb false, :number 0}
            {:coord [8 5], :is-bomb false, :number 1}
            {:coord [8 6], :is-bomb false, :number 1}
            {:coord [8 7], :is-bomb false, :number 1}
            {:coord [8 8], :is-bomb false, :number 0}])

(deftest test-bombs-amount
  (testing "With bad spec"
    (let [spec {:size [12 3] :bombs 75}]
      (is (thrown? IllegalArgumentException (mscore/generate-minefield [9 9] nil)))))
  (testing "With wrong arg type"
    (let [spec {:size [12 3] :bombs 75}]
      (is (thrown? IllegalArgumentException (mscore/generate-minefield [] spec)))))
  (testing "With good spec"
    (let [spec {:size [9 9] :bombs 27}
          bombs (mscore/generate-minefield (:size spec) (:bombs spec))]
      (is (= 27 (count bombs))))))

(deftest test-neighbour-fn
  (testing "Returns non-empty seq"
    (is (= true (seq? (mscore/get-neighbours-wmax [3 7] [3 3])))))
  (testing "Exactly 3 neighbours"
    (is (= 3 (count (mscore/get-neighbours-wmax [2 2] [0 0]))))))

(deftest test-game-generated
  (testing "With bad spec"
    (is (thrown? IllegalArgumentException (mscore/game-start {:wonky "spec"}))))
  (testing "Good spec"
    (is (= (* 9 9) (count (mscore/game-start {:size [9 9] :bombs 27}))))))


(deftest test-open-region
  (testing "Region is opened when hit empty cell and cell with number"
    (let [opened1 (mscore/open-region game1 [1 0])
          opened2 (mscore/open-region game1 [2 2])
          opened3 (mscore/open-region game2 [0 8])]
      (is (= 6 (count opened1)))
      (is (= 1 (count opened2)))
      (is (= 3 (count opened3))))))
