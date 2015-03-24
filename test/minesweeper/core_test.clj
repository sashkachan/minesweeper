(ns minesweeper.core-test
  (:require [clojure.test :refer :all]
            [minesweeper.core :as mscore]))

(deftest test-bombs-amount
  (testing "With bad spec"
    (let [spec [{:size [12 3] :bombs 75}]]
      (is (thrown? IllegalArgumentException (mscore/generate-minefield [9 9] spec)))))
  (testing "With wrong arg type"
    (let [spec [{:size [12 3] :bombs 75}]]
      (is (thrown? IllegalArgumentException (mscore/generate-minefield [:some :jubba] spec)))))
  (testing "With good spec"
    (let [spec [{:size [9 9] :bombs 27}]
          bombs (mscore/generate-minefield [9 9] spec)]
      (is (= 27 (count bombs))))))

(deftest test-neighbour-fn
  (testing "Returns non-empty seq"
    (is (= true (seq? (mscore/get-neighbours-wmax [3 7] [3 3])))))
  (testing "Exactly 3 neighbours"
    (is (= 3 (count (mscore/get-neighbours-wmax [2 2] [0 0]))))))
