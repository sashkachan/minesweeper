(ns minesweeper.core-test
  (:require [clojure.test :refer :all]
            [minesweeper.core :as mscore]))

(deftest test-bombs-amount
  (testing "With bad spec"
    (let [spec [{:size 53 :bombs 75}]]
      (is (thrown? IllegalArgumentException (mscore/generate-minefield 81 spec)))))
  (testing "iWth good spec"
    (let [spec [{:size 81 :bombs 27}]
          bombs (mscore/generate-minefield 81 spec)]
      (is (= 27 (count bombs))))))


