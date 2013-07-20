(ns euclidean.math.quaternion-test
  (:require [clojure.test :refer :all]
            [euclidean.test.util :refer (approx=)]
            [euclidean.math.quaternion :as q]
            [euclidean.math.vector :as v]))

(deftest test-data-readers
  (is (= (pr-str (q/quaternion 1 2 3 4))
         "#math/quaternion [1.0 2.0 3.0 4.0]")))

(deftest test-equality
  (is (= (q/quaternion 1 2 3 4) (q/quaternion 1 2 3 4)))
  (is (not= (q/quaternion 1 2 3 4) (q/quaternion 1 2 4 5))))

(deftest test-lookup
  (let [q (q/quaternion 1 2 3 4)]
    (is (= (get q 0) 1.0))
    (is (= (q 3) 4.0))))

(deftest test-mult
  (let [q1 (q/quaternion 0.707 0 0 0.707)
        q2 (q/quaternion 0 -0.707 0 0.707)]
    (is (approx= (q/mult q1 q2)
                 (q/quaternion 0.5 -0.5 -0.5 0.5)))))

(deftest test-rotate
  (let [v (v/vector 1 2 3)]
    (is (= (q/rotate (q/quaternion 0 0 0 1) v) v))
    (is (approx= (q/rotate (q/quaternion 0.707 0 0 0.707) v)
                 (v/vector 1 -3 2)))))
