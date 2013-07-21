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

(deftest test-angle-from-normal-axis
  (is (approx= (q/from-angle-normal-axis (/ Math/PI 2) (v/vector 1 0 0))
               (q/quaternion 0.707 0 0 0.707))))

(deftest test-angle-from-axis
  (is (approx= (q/from-angle-axis (/ Math/PI 2) (v/vector 3 0 0))
               (q/quaternion 0.707 0 0 0.707))))

(deftest test-pitch
  (is (approx= (q/pitch (/ Math/PI 2))
               (q/quaternion 0.707 0 0 0.707))))

(deftest test-yaw
  (is (approx= (q/yaw (/ Math/PI 2))
               (q/quaternion 0 0.707 0 0.707))))

(deftest test-roll
  (is (approx= (q/roll (/ Math/PI 2))
               (q/quaternion 0 0 0.707 0.707))))

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

(deftest test-norm
  (is (= (q/norm (q/quaternion 1 2 3 4))
         30.0)))

(deftest test-axis
  (let [[x y z] (q/axes (q/quaternion 0 0 0 1))]
    (is (= x [1.0 0.0 0.0]))
    (is (= y [0.0 1.0 0.0]))
    (is (= z [0.0 0.0 1.0]))))

(deftest test-from-axes
  (is (= (q/from-axes (v/vector 1 0 0) (v/vector 0 1 0) (v/vector 0 0 1))
         (q/quaternion 0 0 0 1))))
