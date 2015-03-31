(ns minesweeper.core-test
  (:require [clojure.test :refer :all]
            [minesweeper.core :as mscore]))

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
  (testing "Region is opened when hit an empty cell"
    (let [game [{:coord [0 0], :is-bomb false, :number 0}
                {:coord [0 1], :is-bomb false, :number 1}
                {:coord [0 2], :is-bomb false, :number 1}
                {:coord [1 0], :is-bomb false, :number 0}
                {:coord [1 1], :is-bomb false, :number 1}
                {:coord [1 2], :is-bomb true, :number nil}
                {:coord [2 0], :is-bomb false, :number 0}
                {:coord [2 1], :is-bomb false, :number 1}
                {:coord [2 2], :is-bomb false, :number 1}]
          opened (mscore/open-region game [1 0])]
      (is (= 6 (count opened))))))
