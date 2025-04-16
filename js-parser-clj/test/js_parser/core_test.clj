(ns js-parser.core-test
  (:require [js-parser.core :as sut]
            [clojure.test :refer [deftest testing is are use-fixtures run-tests join-fixtures]]))


(deftest add
  (testing "does add work"
    (is (= 1 (+ 1 1)))))
;;
