(ns euclidean.math.matrix-test
  (:require [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [euclidean.math.matrix :as m]
            [criterium.core :refer [quick-bench]])
  (:import (org.lwjgl.util.vector Matrix2f Matrix3f Matrix4f
                                  Vector3f Vector4f)))

(defn mat2f
  []
  (Matrix2f.))

(defn mat3f
  []
  (Matrix3f.))

(defn mat4f
  []
  (Matrix4f.))

(deftest test-data-readers
  (is (= (pr-str (m/matrix [1 2] [3 4]))
         "#math/matrix [[1.0 2.0] [3.0 4.0]]"))
  (is (= (pr-str (m/matrix [1 2 3] [4 5 6] [7 8 9]))
         "#math/matrix [[1.0 2.0 3.0] [4.0 5.0 6.0] [7.0 8.0 9.0]]"))
  (is (= (pr-str (m/matrix [1 2 3 4] [5 6 7 8] [9 10 11 12] [13 14 15 16]))
         "#math/matrix [[1.0 2.0 3.0 4.0] [5.0 6.0 7.0 8.0] [9.0 10.0 11.0 12.0] [13.0 14.0 15.0 16.0]]")))

