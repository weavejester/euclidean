(ns euclidean.math.vector-test
  (:require [clojure.test :refer :all]
            [euclidean.math.vector :as v]))

(deftest test-data-readers
  (is (= (pr-str (v/vector 1 2 3))
         "#math/vector [1.0 2.0 3.0]")))

(deftest test-equality
  (is (= (v/vector 1 2 3) (v/vector 1 2 3)))
  (is (not= (v/vector 1 2 3) (v/vector 1 2 4))))

(deftest test-lookup
  (let [v (v/vector 1 2 3)]
    (is (= (get v 0) 1.0))
    (is (= (v 1) 2.0))))

(deftest test-add
  (let [v1 (v/vector 1 2 3)
        v2 (v/vector 4 5 6)]
    (is (= (v/add v1 v2)
           (v/vector 5 7 9)))))

(deftest test-mult
  (let [v (v/vector 1 2 3)]
    (is (= (v/mult v 2)
           (v/vector 2 4 6)))))

(deftest test-magnitude
  (is (= (v/magnitude (v/vector 1 0 0)) 1.0))
  (is (= (v/magnitude (v/vector 0 -1 0)) 1.0))
  (is (= (v/magnitude (v/vector 1 2 3))
         (Math/sqrt 14.0))))
