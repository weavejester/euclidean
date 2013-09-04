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

(deftest test-sub
  (let [v1 (v/vector 4 5 6)
        v2 (v/vector 3 2 1)]
    (is (= (v/sub v1 v2)
           (v/vector 1 3 5)))))

(deftest test-mult
  (let [v1 (v/vector 1 2 3)
        v2 (v/vector 2 3 4)]
    (is (= (v/mult v1 v2)
           (v/vector 2 6 12)))))

(deftest test-div
  (let [v1 (v/vector 1 2 3)
        v2 (v/vector 2 4 5)]
    (is (= (v/div v1 v2)
           (v/vector 0.5 0.5 0.6)))))

(deftest test-scale
  (is (= (v/scale (v/vector 1 2 3) 2)
         (v/vector 2 4 6))))

(deftest test-dot
  (is (= (v/dot (v/vector 1 2 3) (v/vector 4 5 6))
         32.0)))

(deftest test-cross
  (is (= (v/cross (v/vector 1 2 4) (v/vector 3 5 6))
         (v/vector -8 6 -1))))

(deftest test-magnitude
  (is (= (v/magnitude (v/vector 1 0 0)) 1.0))
  (is (= (v/magnitude (v/vector 0 -1 0)) 1.0))
  (is (= (v/magnitude (v/vector 1 2 3))
         (Math/sqrt 14.0))))

(deftest test-normalize
  (is (= (v/normalize (v/vector 1 0 0))
         (v/vector 1 0 0)))
  (is (= (v/normalize (v/vector 0 3 4))
         (v/vector 0.0 (* 3.0 (/ 1.0 5.0)) (* 4.0 (/ 1.0 5.0))))))
