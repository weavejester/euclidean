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
  (testing "2D vector"
    (let [v1 (v/vector 1 2)
          v2 (v/vector 4 5)]
      (is (= (v/add v1 v2)
             (v/vector 5 7)))))
  (testing "3D vector"
    (let [v1 (v/vector 1 2 3)
          v2 (v/vector 4 5 6)]
      (is (= (v/add v1 v2)
             (v/vector 5 7 9)))))
  (testing "one arg"
    (let [v (v/vector 1 2 3)]
      (is (= (v/add v) v))))
  (testing "many args"
    (is (= (v/add (v/vector 1 2) (v/vector 3 4) (v/vector 5 6))
           (v/vector 9 12)))))

(deftest test-sub
  (testing "2D vector"
    (let [v1 (v/vector 4 5)
          v2 (v/vector 3 2)]
      (is (= (v/sub v1 v2)
             (v/vector 1 3)))))
  (testing "3D vector"
    (let [v1 (v/vector 4 5 6)
          v2 (v/vector 3 2 1)]
      (is (= (v/sub v1 v2)
             (v/vector 1 3 5)))))
  (testing "negation"
    (is (= (v/sub (v/vector 1 2 3))
           (v/vector -1 -2 -3))))
  (testing "many args"
    (is (= (v/sub (v/vector 9 9) (v/vector 1 2) (v/vector 3 4))
           (v/vector 5 3)))))

(deftest test-mult
  (testing "one arg"
    (let [v (v/vector 1 2 3)]
      (is (= (v/mult v) v))))
  (testing "two args"
    (let [v1 (v/vector 1 2 3)
          v2 (v/vector 2 3 4)]
      (is (= (v/mult v1 v2)
             (v/vector 2 6 12)))))
  (testing "many args"
    (is (= (v/mult (v/vector 1 2) (v/vector 3 4) (v/vector 5 6))
           (v/vector 15 48)))))

(deftest test-div
  (testing "one arg"
    (is (= (v/div (v/vector 1 2 4))
           (v/vector 1.0 0.5 0.25))))
  (testing "two args"
    (let [v1 (v/vector 1 2 3)
          v2 (v/vector 2 4 5)]
      (is (= (v/div v1 v2)
             (v/vector 0.5 0.5 0.6)))))
  (testing "many args"
    (is (= (v/div (v/vector 12 24) (v/vector 2 3) (v/vector 3 4))
           (v/vector 2 2)))))

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

(deftest test-get
  (testing "2D vector"
    (let [v (v/vector 1 2)]
      (is (= (v/get-x v) 1.0))
      (is (= (v/get-y v) 2.0))))
  (testing "3D vector"
    (let [v (v/vector 1 2 3)]
      (is (= (v/get-x v) 1.0))
      (is (= (v/get-y v) 2.0))
      (is (= (v/get-z v) 3.0)))))
