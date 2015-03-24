(ns minesweeper.core-test
  (:require [clojure.test :refer :all]
            [minesweeper.core :as mscore]))

(deftest test-bombs-amount
  (testing "With bad spec"
    (let [spec [{:size [12 3] :bombs 75}]]
      (is (thrown? IllegalArgumentException (mscore/generate-minefield [9 9] spec)))))
  (testing "iWth good spec"
    (let [spec [{:size [9 9] :bombs 27}]
          bombs (mscore/generate-minefield [9 9] spec)]
      (is (= 27 (count bombs))))))


