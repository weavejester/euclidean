(ns euclidean.math.vector-test
  (:require [clojure.test :refer :all]
            [euclidean.math.vector :as v]))

(deftest test-data-readers
  (is (= (pr-str (v/vector 1 2 3))
         "#math/vector [1 2 3]")))

(deftest test-equality
  (is (= (v/vector 1 2 3) (v/vector 1 2 3)))
  (is (not= (v/vector 1 2 3) (v/vector 1 2 4))))

(deftest test-lookup
  (let [v (v/vector 1 2 3)]
    (is (= (get v 0) 1))
    (is (= (v 1) 2))))

(deftest test-add
  (let [v1 (v/vector 1 2 3)
        v2 (v/vector 4 5 6)]
    (is (= (v/add v1 v2)
           (v/vector 5 7 9)))))
