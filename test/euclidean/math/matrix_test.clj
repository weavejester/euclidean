(ns euclidean.math.matrix-test
  (:require [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :as ct :refer [defspec]]
            [euclidean.math.matrix :as m]
            [euclidean.math.vector :as v]
            [euclidean.test.util :refer [approx=]]
            [criterium.core :refer [quick-bench with-progress-reporting]])
  (:import (org.lwjgl.util.vector Matrix Matrix2f Matrix3f Matrix4f)
           (org.lwjgl.util.vector Vector2f Vector3f Vector4f)
           (euclidean.math.matrix Matrix2D Matrix3D Matrix4D)
           (java.nio FloatBuffer)))

(def ^:const n-tests 100)

(defn ^FloatBuffer float-buffer
  [m]
  (java.nio.FloatBuffer/wrap (float-array (flatten m))))

(defmulti load-matrix count)
(defmulti store-matrix class)

(defmethod load-matrix 4
  [m]
  (.loadTranspose (Matrix2f.) (float-buffer m)))

(defmethod load-matrix 9
  [m]
  (.loadTranspose (Matrix3f.) (float-buffer m)))

(defmethod load-matrix 16
  [m]
  (.loadTranspose (Matrix4f.) (float-buffer m)))

(defn store-transpose
  [^Matrix m size]
  (let [buf (java.nio.FloatBuffer/allocate size)
        dst (float-array size)]
    (.storeTranspose m buf)
    (doto buf
      (.flip)
      (.get dst))
    dst))

(defmethod store-matrix Matrix2f
  [m]
  (store-transpose m 4))

(defmethod store-matrix Matrix3f
  [m]
  (store-transpose m 9))

(defmethod store-matrix Matrix4f
  [m]
  (store-transpose m 16))

(def gen-dim (gen/choose 2 4))

(def gen-vec3 (gen/fmap v/into-vector
                        (gen/vector gen/int 3)))

(def gen-mat4 (gen/fmap (juxt (partial apply m/mat4) load-matrix)
                        (gen/vector gen/int 16)))

(defn gen-matrix
  [dim]
  (gen/fmap (juxt (partial apply (case (int dim) 2 m/mat2 3 m/mat3 4 m/mat4))
                  load-matrix)
            (gen/vector (gen/fmap double gen/ratio) (* dim dim))))

(def gen-matrices (gen/bind gen-dim gen-matrix))

(defn lwjgl-add
  [m1 m2]
  (condp = (class m1)
    Matrix2f (Matrix2f/add m1 m2 (Matrix2f.))
    Matrix3f (Matrix3f/add m1 m2 (Matrix3f.))
    Matrix4f (Matrix4f/add m1 m2 (Matrix4f.))))

(defn lwjgl-sub
  [m1 m2]
  (condp = (class m1)
    Matrix2f (Matrix2f/sub m1 m2 (Matrix2f.))
    Matrix3f (Matrix3f/sub m1 m2 (Matrix3f.))
    Matrix4f (Matrix4f/sub m1 m2 (Matrix4f.))))

(defn lwjgl-mult
  [m1 m2]
  (condp = (class m1)
    Matrix2f (Matrix2f/mul m1 m2 (Matrix2f.))
    Matrix3f (Matrix3f/mul m1 m2 (Matrix3f.))
    Matrix4f (Matrix4f/mul m1 m2 (Matrix4f.))))

(defn lwjgl-invert
  [m]
  (condp = (class m)
    Matrix2f (Matrix2f/invert m (Matrix2f. m))
    Matrix3f (Matrix3f/invert m (Matrix3f.))
    Matrix4f (Matrix4f/invert m (Matrix4f. m))))

(defn lwjgl-transpose
  [m]
  (condp = (class m)
    Matrix2f (Matrix2f/transpose m (Matrix2f. m))
    Matrix3f (Matrix3f/transpose m (Matrix3f.))
    Matrix4f (Matrix4f/transpose m (Matrix4f. m))))

(defmethod approx= Matrix
  [xs ys]
  (let [xs (store-matrix xs)]
    (approx= ys (partition (Math/sqrt (count xs)) xs))))

(defspec test-add n-tests
  (prop/for-all [[m1 m1'] gen-matrices]
    (let [[m2 m2'] (first (gen/sample (gen-matrix (count m1)) 1))]
      (is (approx= (lwjgl-add m1' m2') (m/add m1 m2))))))

(defspec test-sub n-tests
  (prop/for-all [[m1 m1'] gen-matrices]
    (let [[m2 m2'] (first (gen/sample (gen-matrix (count m1)) 1))]
      (is (approx= (lwjgl-sub m1' m2') (m/sub m1 m2))))))

(defspec test-mult n-tests
  (prop/for-all [[m1 m1'] gen-matrices]
    (let [[m2 m2'] (first (gen/sample (gen-matrix (count m1)) 1))]
      (is (approx= (lwjgl-mult m1' m2') (m/mult m1 m2))))))

(defspec test-invert n-tests
  (prop/for-all [[m m'] gen-matrices]
    (is (approx= (lwjgl-invert m') (m/invert m)))))

(defspec test-det n-tests
  (prop/for-all [[m ^Matrix m'] gen-matrices]
    (is (approx= (m/determinant m) (.determinant m')))))

(defspec test-transpose n-tests
  (prop/for-all [[m m'] gen-matrices]
    (is (approx= (lwjgl-transpose m') (m/transpose m)))))

(defspec test-scale n-tests
  (prop/for-all [[m m'] gen-mat4
                 [x y z :as v] gen-vec3]
    (let [v' (Vector3f. x y z)]
      (is (approx= (.scale ^Matrix4f m' v') (m/scale m v))))))

(defspec test-translate n-tests
  (prop/for-all [[m m'] gen-mat4
                 [x y z :as v3] gen-vec3]
    (let [v2 (v/vector z x)
          v3' (Vector3f. x y z)
          v2' (Vector2f. z x)]
      (is (approx= (.translate ^Matrix4f m' v2' (Matrix4f. m'))
                   (m/translate v2 m)))
      (is (approx= (.translate ^Matrix4f m' v3' (Matrix4f. m'))
                   (m/translate v3 m))))))

(defspec test-rotate n-tests
  (prop/for-all [[m m'] gen-mat4
                 [x y z :as axis] gen-vec3
                 angle gen/pos-int]
    (let [^Vector3f axis'  (try (.normalise (Vector3f. x y z))
                                (catch Throwable t
                                  (.normalise (Vector3f. 1 1 1))))
          axis (v/vector (.-x axis') (.-y axis') (.-z axis'))]
      (is (approx= (.rotate ^Matrix4f m' (Math/toRadians angle) axis')
                   (m/rotate m angle axis))))))

(defn benchmark-add
  []
  (with-progress-reporting
    (let [[m1 m1'] (last (gen/sample gen-matrices))]
      (let [[m2 m2'] (last (gen/sample (gen-matrix (count m1))))]
        (quick-bench (m/add m1 m2))
        (quick-bench (lwjgl-add m1' m2'))))))

(defn benchmark-sub
  []
  (with-progress-reporting
    (let [[m1 m1'] (last (gen/sample gen-matrices))]
      (let [[m2 m2'] (last (gen/sample (gen-matrix (count m1))))]
        (quick-bench (m/sub m1 m2))
        (quick-bench (lwjgl-sub m1' m2'))))))

(defn benchmark-mult
  []
  (with-progress-reporting
    (let [[m1 m1'] (last (gen/sample gen-matrices))]
      (let [[m2 m2'] (last (gen/sample (gen-matrix (count m1))))]
        (quick-bench (m/mult m1 m2))
        (quick-bench (lwjgl-mult m1' m2'))))))

(defn benchmark-invert
  []
  (with-progress-reporting
    (let [[m m'] (last (gen/sample gen-matrices))]
      (quick-bench (m/invert m))
      (quick-bench (lwjgl-invert m')))))

(defn benchmark-determinant
  []
  (with-progress-reporting
    (let [[m m'] (last (gen/sample gen-matrices))]
      (quick-bench (m/determinant m))
      (quick-bench (.determinant ^Matrix m')))))

(defn benchmark-transpose
  []
  (with-progress-reporting
    (let [[m m'] (last (gen/sample gen-matrices))]
      (quick-bench (m/transpose m))
      (quick-bench (lwjgl-transpose m')))))
