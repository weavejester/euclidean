(ns euclidean.math.matrix
  (:require [euclidean.math.vector :as vec])
  (:import (euclidean.math.vector Vector2D Vector3D Vector4D)))

(defprotocol Matrix
  (^:no-doc add* [m1 m2] "Add two matrices together.")
  (^:no-doc sub* [m1 m2] "Subtract the second matrix from the first.")
  (^:no-doc mult* [m1 m2] "Multiply one matrix by another.")
  (negate [m] "Negates each component of the input matrix.")
  (invert [m] "Inverts the input matrix.")
  (^double determinant [m] "Returns the determinant of the input matrix.")
  (transpose [m] "Returns the transpose of the input matrix."))

(defprotocol AdditiveIdentity
  (add-identity [x] "Returns the additive identity of the input."))

(defprotocol MultiplicativeIdentity
  (mult-identity [x] "Returns the multiplicative identity of the input."))

(defprotocol TranslateBy
  (translate [v m] "Translate the matrix by the given vector."))

(defmacro ^:private hash-matrix-fields
  [& fields]
  `(-> (int 1)
       ~@(interleave (repeat (count fields) `(unchecked-multiply-int 31))
                     (for [field fields]
                       `(unchecked-add-int
                         (if (zero? ~field)
                           0
                           (Float/floatToIntBits ~field)))))))

(declare identity-mat2 identity-mat3 identity-mat4)

(deftype Matrix2D [^double m00 ^double m01
                   ^double m10 ^double m11]
  clojure.lang.Counted
  (count [_] 2)

  clojure.lang.Sequential

  clojure.lang.Seqable
  (seq [_] (list (Vector2D. m00 m01) (Vector2D. m10 m11)))

  clojure.lang.ILookup
  (valAt [m i]
    (.valAt m i nil))
  (valAt [_ i not-found]
    (case (int i)
      0 (Vector2D. m00 m01)
      1 (Vector2D. m10 m11)
      not-found))

  clojure.lang.Associative
  (equiv [m o]
    (.equals m o))
  (containsKey [_ k]
    (case (int k)
      (0 1) true
      false))
  (entryAt [m k]
    (case (int k)
      0 (clojure.lang.MapEntry. 0 (.valAt m 0))
      1 (clojure.lang.MapEntry. 1 (.valAt m 1))))
  (assoc [m i [x y]]
    (case (int i)
      0 (Matrix2D. x y m10 m11)
      1 (Matrix2D. m00 m01 x y)))

  clojure.lang.IFn
  (invoke [m i]
    (.valAt m i))

  Object
  (toString [_]
    (str "#math/matrix [" [m00  m01] " " [m10 m11] "]"))
  (hashCode [_]
    (hash-matrix-fields m00 m01 m10 m11))
  (equals [self m]
    (or (identical? self m)
        (and (instance? Matrix2D m)
             (== (hash self) (hash m)))
        (and (counted? m)
             (== (count m) 2)
             (= (Vector2D. m00 m01) (m 0))
             (= (Vector2D. m10 m11) (m 1)))))

  Matrix
  (add* [_ m2]
    (let [m2 ^Matrix2D m2]
      (Matrix2D. (+ m00 (.-m00 m2)) (+ m01 (.-m01 m2))
                 (+ m10 (.-m10 m2)) (+ m11 (.-m11 m2)))))
  (sub* [_ m2]
    (let [m2 ^Matrix2D m2]
      (Matrix2D. (- m00 (.-m00 m2)) (- m01 (.-m01 m2))
                 (- m10 (.-m10 m2)) (- m11 (.-m11 m2)))))
  (mult* [_ m2]
    (let [m2 ^Matrix2D m2]
      (Matrix2D. (+ (* m00 (.-m00 m2)) (* m01 (.-m10 m2)))
                 (+ (* m00 (.-m01 m2)) (* m01 (.-m11 m2)))
                 (+ (* m10 (.-m00 m2)) (* m11 (.-m10 m2)))
                 (+ (* m10 (.-m01 m2)) (* m11 (.-m11 m2))))))
  (negate [_]
    (Matrix2D. (- m00) (- m01) (- m10) (- m11)))
  (invert [m]
    (let [det (determinant m)]
      (when-not (zero? det)
        (let [det-inv (/ det)]
          (Matrix2D. (* m11 det-inv) (- (* m01 det-inv))
                     (- (* m10 det-inv)) (* m00 det-inv))))))
  (determinant [_]
    (- (* m00 m11) (* m10 m01)))
  (transpose [_]
    (Matrix2D. m00 m10 m01 m11)))

(deftype Matrix3D [^double m00 ^double m01 ^double m02
                   ^double m10 ^double m11 ^double m12
                   ^double m20 ^double m21 ^double m22]
  clojure.lang.Counted
  (count [_] 3)

  clojure.lang.Sequential

  clojure.lang.Seqable
  (seq [_] (list (Vector3D. m00 m01 m02)
                 (Vector3D. m10 m11 m12)
                 (Vector3D. m20 m21 m22)))

  clojure.lang.ILookup
  (valAt [m i]
    (.valAt m i nil))
  (valAt [_ i not-found]
    (case (int i)
      0 (Vector3D. m00 m01 m02)
      1 (Vector3D. m10 m11 m12)
      2 (Vector3D. m20 m21 m22)
      not-found))

  clojure.lang.Associative
  (equiv [m o] (.equals m o))
  (containsKey [_ k]
    (case (int k)
      (0 1 2) true
      false))
  (entryAt [m k]
    (case (int k)
      0 (clojure.lang.MapEntry. 0 (.valAt m 0))
      1 (clojure.lang.MapEntry. 1 (.valAt m 1))
      2 (clojure.lang.MapEntry. 2 (.valAt m 2))))
  (assoc [m i [x y z]]
    (case (int i)
      0 (Matrix3D. x y z m10 m11 m12 m20 m21 m22)
      1 (Matrix3D. m00 m01 m02 x y z m20 m21 m22)
      2 (Matrix3D. m00 m01 m02 m10 m11 m12 x y z)))

  clojure.lang.IFn
  (invoke [m i]
    (.valAt m i))

  Object
  (toString [_]
    (str "#math/matrix ["
         [m00 m01 m02] " "
         [m10 m11 m12] " "
         [m20 m21 m22] "]"))
  (hashCode [this]
    (hash-matrix-fields m00 m01 m02 m10 m11 m12 m20 m21 m22))
  (equals [self m]
    (or (identical? self m)
        (and (instance? Matrix3D m)
             (== (hash self) (hash m)))
        (and (counted? m)
             (== (count m) 3)
             (= (Vector3D. m00 m01 m02) (m 0))
             (= (Vector3D. m10 m11 m12) (m 1))
             (= (Vector3D. m20 m21 m22) (m 2)))))

  Matrix
  (add* [m1 m2]
    (let [m2 ^Matrix3D m2]
      (Matrix3D. (+ m00 (.-m00 m2)) (+ m01 (.-m01 m2)) (+ m02 (.-m02 m2))
                 (+ m10 (.-m10 m2)) (+ m11 (.-m11 m2)) (+ m12 (.-m12 m2))
                 (+ m20 (.-m20 m2)) (+ m21 (.-m21 m2)) (+ m22 (.-m22 m2)))))
  (sub* [m1 m2]
    (let [m2 ^Matrix3D m2]
      (Matrix3D. (- m00 (.-m00 m2)) (- m01 (.-m01 m2)) (- m02 (.-m02 m2))
                 (- m10 (.-m10 m2)) (- m11 (.-m11 m2)) (- m12 (.-m12 m2))
                 (- m20 (.-m20 m2)) (- m21 (.-m21 m2)) (- m22 (.-m22 m2)))))
  (mult* [m1 m2]
    (let [m2 ^Matrix3D m2]
      (Matrix3D. (+ (* m00 (.-m00 m2)) (* m01 (.-m10 m2)) (* m02 (.-m20 m2)))
                 (+ (* m00 (.-m01 m2)) (* m01 (.-m11 m2)) (* m02 (.-m21 m2)))
                 (+ (* m00 (.-m02 m2)) (* m01 (.-m12 m2)) (* m02 (.-m22 m2)))

                 (+ (* m10 (.-m00 m2)) (* m11 (.-m10 m2)) (* m12 (.-m20 m2)))
                 (+ (* m10 (.-m01 m2)) (* m11 (.-m11 m2)) (* m12 (.-m21 m2)))
                 (+ (* m10 (.-m02 m2)) (* m11 (.-m12 m2)) (* m12 (.-m22 m2)))
                 
                 (+ (* m20 (.-m00 m2)) (* m21 (.-m10 m2)) (* m22 (.-m20 m2)))
                 (+ (* m20 (.-m01 m2)) (* m21 (.-m11 m2)) (* m22 (.-m21 m2)))
                 (+ (* m20 (.-m02 m2)) (* m21 (.-m12 m2)) (* m22 (.-m22 m2))))))
  (negate [_]
    (Matrix3D. (- m00) (- m01) (- m02)
               (- m10) (- m11) (- m12)
               (- m20) (- m21) (- m22)))
  (invert [m]
    (let [det (determinant m)]
      (when-not (zero? det)
        (let [det-inv (/ det)]
          (Matrix3D. (* (- (* m11 m22) (* m21 m12)) det-inv)
                     (* (+ (* (- m01) m22) (* m21 m02)) det-inv)
                     (* (- (* m01 m12) (* m11 m02)) det-inv)
                     
                     (* (+ (* (- m10) m22) (* m20 m12)) det-inv)
                     (* (- (* m00 m22) (* m20 m02)) det-inv)
                     (* (+ (* (- m00) m12) (* m10 m02)) det-inv)
                     
                     (* (- (* m10 m21) (* m20 m11)) det-inv)
                     (* (+ (* (- m00) m21) (* m20 m01)) det-inv)
                     (* (- (* m00 m11) (* m10 m01)) det-inv))))))
  (determinant [_]
    (+ (* m00 (- (* m11 m22) (* m21 m12)))
       (* m10 (- (* m21 m02) (* m01 m22)))
       (* m20 (- (* m01 m12) (* m11 m02)))))
  (transpose [_]
    (Matrix3D. m00 m10 m20 m01 m11 m21 m02 m12 m22)))

(definline det3
  [m00# m10# m20# m01# m11# m21# m02# m12# m22#]
  `(+ (* ~m00# (- (* ~m11# ~m22#) (* ~m21# ~m12#)))
      (* ~m10# (- (* ~m21# ~m02#) (* ~m01# ~m22#)))
      (* ~m20# (- (* ~m01# ~m12#) (* ~m11# ~m02#)))))

(deftype Matrix4D [^double m00 ^double m01 ^double m02 ^double m03
                   ^double m10 ^double m11 ^double m12 ^double m13
                   ^double m20 ^double m21 ^double m22 ^double m23
                   ^double m30 ^double m31 ^double m32 ^double m33]
  clojure.lang.Counted
  (count [_] 4)

  clojure.lang.Sequential

  clojure.lang.Seqable
  (seq [_] (list (Vector4D. m00 m01 m02 m03)
                 (Vector4D. m10 m11 m12 m13)
                 (Vector4D. m20 m21 m22 m23)
                 (Vector4D. m30 m31 m32 m33)))

  clojure.lang.ILookup
  (valAt [m i]
    (.valAt m i nil))
  (valAt [_ i not-found]
    (case (int i)
      0 (Vector4D. m00 m01 m02 m03)
      1 (Vector4D. m10 m11 m12 m13)
      2 (Vector4D. m20 m21 m22 m23)
      3 (Vector4D. m30 m31 m32 m33)
      not-found))

  clojure.lang.Associative
  (equiv [m o]
    (.equals m o))
  (containsKey [_ k]
    (case (int k)
      (0 1 2 3) true
      false))
  (entryAt [m k]
    (case (int k)
      0 (clojure.lang.MapEntry. 0 (.valAt m 0))
      1 (clojure.lang.MapEntry. 1 (.valAt m 1))
      2 (clojure.lang.MapEntry. 2 (.valAt m 2))
      3 (clojure.lang.MapEntry. 2 (.valAt m 3))))
  (assoc [m i v]
    (let [x (.valAt ^clojure.lang.ILookup v 0)
          y (.valAt ^clojure.lang.ILookup v 1)
          z (.valAt ^clojure.lang.ILookup v 2)
          w (.valAt ^clojure.lang.ILookup v 3)]
      (case (int i)
        0 (Matrix4D. x y z w m10 m11 m12 m13 m20 m21 m22 m23 m30 m31 m32 m33)
        1 (Matrix4D. m00 m01 m02 m03 x y z w m20 m21 m22 m23 m30 m31 m32 m33)
        2 (Matrix4D. m00 m01 m02 m03 m10 m11 m12 m13 x y z w m30 m31 m32 m33)
        3 (Matrix4D. m00 m01 m02 m03 m10 m11 m12 m13 m20 m21 m22 m23 x y z w))))

  clojure.lang.IFn
  (invoke [m i]
    (.valAt m i))

  Object
  (toString [_]
    (str "#math/matrix ["
         [m00 m01 m02 m03] " "
         [m10 m11 m12 m13] " "
         [m20 m21 m22 m23] " "
         [m30 m31 m32 m33] "]"))
  (hashCode [_]
    (hash-matrix-fields m00 m01 m02 m03 m10 m11 m12 m13
                        m20 m21 m22 m23 m30 m31 m32 m33))
  (equals [self m]
    (or (identical? self m)
        (and (instance? Matrix4D m)
             (== (hash self) (hash m)))
        (and (counted? m)
             (= (count m) 3)
             (= (Vector4D. m00 m01 m02 m03) (m 0))
             (= (Vector4D. m10 m11 m12 m13) (m 1))
             (= (Vector4D. m20 m21 m22 m23) (m 2))
             (= (Vector4D. m30 m31 m32 m33) (m 3)))))

  Matrix
  (add* [m1 m2]
    (let [m2 ^Matrix4D m2]
      (Matrix4D. (+ m00 (.-m00 m2)) (+ m01 (.-m01 m2))
                 (+ m02 (.-m02 m2)) (+ m03 (.-m03 m2))
                 (+ m10 (.-m10 m2)) (+ m11 (.-m11 m2))
                 (+ m12 (.-m12 m2)) (+ m13 (.-m13 m2))
                 (+ m20 (.-m20 m2)) (+ m21 (.-m21 m2))
                 (+ m22 (.-m22 m2)) (+ m23 (.-m23 m2))
                 (+ m30 (.-m30 m2)) (+ m31 (.-m31 m2))
                 (+ m32 (.-m32 m2)) (+ m33 (.-m33 m2)))))
  (sub* [m1 m2]
    (let [m2 ^Matrix4D m2]
      (Matrix4D. (- m00 (.-m00 m2)) (- m01 (.-m01 m2))
                 (- m02 (.-m02 m2)) (- m03 (.-m03 m2))
                 (- m10 (.-m10 m2)) (- m11 (.-m11 m2))
                 (- m12 (.-m12 m2)) (- m13 (.-m13 m2))
                 (- m20 (.-m20 m2)) (- m21 (.-m21 m2))
                 (- m22 (.-m22 m2)) (- m23 (.-m23 m2))
                 (- m30 (.-m30 m2)) (- m31 (.-m31 m2))
                 (- m32 (.-m32 m2)) (- m33 (.-m33 m2)))))
  (mult* [m1 m2]
    (let [m2 ^Matrix4D m2]
      (Matrix4D. (+ (* m00 (.-m00 m2)) (* m01 (.-m10 m2))
                    (* m02 (.-m20 m2)) (* m03 (.-m30 m2)))
                 (+ (* m00 (.-m01 m2)) (* m01 (.-m11 m2))
                    (* m02 (.-m21 m2)) (* m03 (.-m31 m2)))
                 (+ (* m00 (.-m02 m2)) (* m01 (.-m12 m2))
                    (* m02 (.-m22 m2)) (* m03 (.-m32 m2)))
                 (+ (* m00 (.-m03 m2)) (* m01 (.-m13 m2))
                    (* m02 (.-m23 m2)) (* m03 (.-m33 m2)))
                 (+ (* m10 (.-m00 m2)) (* m11 (.-m10 m2))
                    (* m12 (.-m20 m2)) (* m13 (.-m30 m2)))
                 (+ (* m10 (.-m01 m2)) (* m11 (.-m11 m2))
                    (* m12 (.-m21 m2)) (* m13 (.-m31 m2)))
                 (+ (* m10 (.-m02 m2)) (* m11 (.-m12 m2))
                    (* m12 (.-m22 m2)) (* m13 (.-m32 m2)))
                 (+ (* m10 (.-m03 m2)) (* m11 (.-m13 m2))
                    (* m12 (.-m23 m2)) (* m13 (.-m33 m2)))
                 (+ (* m20 (.-m00 m2)) (* m21 (.-m10 m2))
                    (* m22 (.-m20 m2)) (* m23 (.-m30 m2)))
                 (+ (* m20 (.-m01 m2)) (* m21 (.-m11 m2))
                    (* m22 (.-m21 m2)) (* m23 (.-m31 m2)))
                 (+ (* m20 (.-m02 m2)) (* m21 (.-m12 m2))
                    (* m22 (.-m22 m2)) (* m23 (.-m32 m2)))
                 (+ (* m20 (.-m03 m2)) (* m21 (.-m13 m2))
                    (* m22 (.-m23 m2)) (* m23 (.-m33 m2)))
                 (+ (* m30 (.-m00 m2)) (* m31 (.-m10 m2))
                    (* m32 (.-m20 m2)) (* m33 (.-m30 m2)))
                 (+ (* m30 (.-m01 m2)) (* m31 (.-m11 m2))
                    (* m32 (.-m21 m2)) (* m33 (.-m31 m2)))
                 (+ (* m30 (.-m02 m2)) (* m31 (.-m12 m2))
                    (* m32 (.-m22 m2)) (* m33 (.-m32 m2)))
                 (+ (* m30 (.-m03 m2)) (* m31 (.-m13 m2))
                    (* m32 (.-m23 m2)) (* m33 (.-m33 m2))))))
  (negate [_]
    (Matrix4D. (- m00) (- m01) (- m02) (- m03)
               (- m10) (- m11) (- m12) (- m13)
               (- m20) (- m21) (- m22) (- m23)
               (- m30) (- m31) (- m32) (- m33)))
  (invert [m]
    (let [det (determinant m)]
      (when-not (zero? det)
        (let [det-inv (/ det)
              t00 (det3 m11 m21 m31 m12 m22 m32 m13 m23 m33)
              t01 (- (det3 m01 m21 m31 m02 m22 m32 m03 m23 m33))
              t02 (det3 m01 m11 m31 m02 m12 m32 m03 m13 m33)
              t03 (- (det3 m01 m11 m21 m02 m12 m22 m03 m13 m23))
              t10 (- (det3 m10 m20 m30 m12 m22 m32 m13 m23 m33))
              t11 (det3 m00 m20 m30 m02 m22 m32 m03 m23 m33)
              t12 (- (det3 m00 m10 m30 m02 m12 m32 m03 m13 m33))
              t13 (det3 m00 m10 m20 m02 m12 m22 m03 m13 m23)
              t20 (det3 m10 m20 m30 m11 m21 m31 m13 m23 m33)
              t21 (- (det3 m00 m20 m30 m01 m21 m31 m03 m23 m33))
              t22 (det3 m00 m10 m30 m01 m11 m31 m03 m13 m33)
              t23 (- (det3 m00 m10 m20 m01 m11 m21 m03 m13 m23))
              t30 (- (det3 m10 m20 m30 m11 m21 m31 m12 m22 m32))
              t31 (det3 m00 m20 m30 m01 m21 m31 m02 m22 m32)
              t32 (- (det3 m00 m10 m30 m01 m11 m31 m02 m12 m32))
              t33 (det3 m00 m10 m20 m01 m11 m21 m02 m12 m22)]
          (Matrix4D. (* t00 det-inv) (* t01 det-inv)
                     (* t02 det-inv) (* t03 det-inv)
                     (* t10 det-inv) (* t11 det-inv)
                     (* t12 det-inv) (* t13 det-inv)
                     (* t20 det-inv) (* t21 det-inv)
                     (* t22 det-inv) (* t23 det-inv)
                     (* t30 det-inv) (* t31 det-inv)
                     (* t32 det-inv) (* t33 det-inv))))))
  (determinant [_]
    (- (+ (- (* m00 (- (+ (* m11 m22 m33) (* m21 m32 m13) (* m31 m12 m23))
                       (* m31 m22 m13) (* m11 m32 m23) (* m21 m12 m33)))
             (* m10 (- (+ (* m01 m22 m33) (* m21 m32 m03) (* m31 m02 m23))
                       (* m31 m22 m03) (* m01 m32 m23) (* m21 m02 m33))))
          (* m20 (- (+ (* m01 m12 m33) (* m11 m32 m03) (* m31 m02 m13))
                    (* m31 m12 m03) (* m01 m32 m13) (* m11 m02 m33))))
       (* m30 (- (+ (* m01 m12 m23) (* m11 m22 m03) (* m21 m02 m13))
                 (* m21 m12 m03) (* m01 m22 m13) (* m11 m02 m23)))))
  (transpose [_]
    (Matrix4D. m00 m10 m20 m30
               m01 m11 m21 m31
               m02 m12 m22 m32
               m03 m13 m23 m33)))

(alter-meta! #'->Matrix2D assoc :no-doc true)
(alter-meta! #'->Matrix3D assoc :no-doc true)
(alter-meta! #'->Matrix4D assoc :no-doc true)

(def ^:private identity-mat2
  (Matrix2D. 1.0 0.0 0.0 1.0))

(def ^:private identity-mat3
  (Matrix3D. 1.0 0.0 0.0 0.0 1.0 0.0 0.0 0.0 1.0))

(def ^:private identity-mat4
  (Matrix4D. 1.0 0.0 0.0 0.0
             0.0 1.0 0.0 0.0
             0.0 0.0 1.0 0.0
             0.0 0.0 0.0 1.0))

(extend-type Matrix2D
  AdditiveIdentity
  (add-identity [_] (Matrix2D. 0.0 0.0 0.0 0.0))
  MultiplicativeIdentity
  (mult-identity [_] identity-mat2))

(extend-type Matrix3D
  AdditiveIdentity
  (add-identity [_] (Matrix3D. 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0))
  MultiplicativeIdentity
  (mult-identity [_] identity-mat3))

(extend-type Matrix4D
  AdditiveIdentity
  (add-identity [_] (Matrix4D. 0.0 0.0 0.0 0.0
                               0.0 0.0 0.0 0.0
                               0.0 0.0 0.0 0.0
                               0.0 0.0 0.0 0.0))
  MultiplicativeIdentity
  (mult-identity [_] identity-mat4))

(extend-protocol TranslateBy
  Vector2D
  (translate [v ^Matrix4D m]
    (Matrix4D. (.-m00 m) (.-m01 m) (.-m02 m)
               (+ (.-m03 m) (+ (* (.-m00 m) (.getX v))
                               (* (.-m01 m) (.getY v))))
               
               (.-m10 m) (.-m11 m) (.-m12 m)
               (+ (.-m13 m) (+ (* (.-m10 m) (.getX v))
                               (* (.-m11 m) (.getY v))))
               
               (.-m20 m) (.-m21 m) (.-m22 m)
               (+ (.-m23 m) (+ (* (.-m20 m) (.getX v))
                               (* (.-m21 m) (.getY v))))
               
               (.-m30 m) (.-m31 m) (.-m32 m)
               (+ (.-m33 m) (+ (* (.-m30 m) (.getX v))
                               (* (.-m31 m) (.getY v))))))

  Vector3D
  (translate [v ^Matrix4D m]
    (Matrix4D. (.-m00 m) (.-m01 m) (.-m02 m)
               (+ (.-m03 m) (+ (* (.-m00 m) (.getX v))
                               (* (.-m01 m) (.getY v))
                               (* (.-m02 m) (.getZ v))))
               
               (.-m10 m) (.-m11 m) (.-m12 m)
               (+ (.-m13 m) (+ (* (.-m10 m) (.getX v))
                               (* (.-m11 m) (.getY v))
                               (* (.-m12 m) (.getZ v))))
               
               (.-m20 m) (.-m21 m) (.-m22 m)
               (+ (.-m23 m) (+ (* (.-m20 m) (.getX v))
                               (* (.-m21 m) (.getY v))
                               (* (.-m22 m) (.getZ v))))
               
               (.-m30 m) (.-m31 m) (.-m32 m)
               (+ (.-m33 m) (+ (* (.-m30 m) (.getX v))
                               (* (.-m31 m) (.getY v))
                               (* (.-m32 m) (.getZ v)))))))

(defn add
  "Return the sum of one or more matrixs."
  ([m] m)
  ([m1 m2] (add* m1 m2))
  ([m1 m2 & more] (reduce add* (add* m1 m2) more)))

(defn sub
  "If only one matrix is supplied, return the negation of the matrix. Otherwise
  all subsequent matrixs are subtracted from the first."
  ([m] (negate m))
  ([m1 m2] (sub* m1 m2))
  ([m1 m2 & more] (reduce sub* (sub* m1 m2) more)))

(defn mult
  "Performs matrix multiplication on the input matrices."
  ([m] m)
  ([m1 m2] (mult* m1 m2))
  ([m1 m2 & more] (reduce mult* (mult* m1 m2) more)))

(defn scale
  "Scales the Matrix4D by the given Vector3D."
  [^Matrix4D m ^Vector3D v]
  (Matrix4D. (* (.-m00 m) (.getX v))
             (* (.-m01 m) (.getY v))
             (* (.-m02 m) (.getZ v)) (.-m03 m)
             
             (* (.-m10 m) (.getX v))
             (* (.-m11 m) (.getY v))
             (* (.-m12 m) (.getZ v)) (.-m13 m)
             
             (* (.-m20 m) (.getX v))
             (* (.-m21 m) (.getY v))
             (* (.-m22 m) (.getZ v)) (.-m23 m)
             
             (* (.-m30 m) (.getX v))
             (* (.-m31 m) (.getY v))
             (* (.-m32 m) (.getZ v)) (.-m33 m)))

(defn transform
  "Transforms the input vector by the input matrix."
  [^Matrix4D m ^Vector4D v]
  (Vector4D. (+ (* (.-m00 m) (.getX v)) (* (.-m01 m) (.getY v))
                (* (.-m02 m) (.getZ v)) (* (.-m03 m) (.getW v)))
             
             (+ (* (.-m10 m) (.getX v)) (* (.-m11 m) (.getY v))
                (* (.-m12 m) (.getZ v)) (* (.-m13 m) (.getW v)))
             
             (+ (* (.-m20 m) (.getX v)) (* (.-m21 m) (.getY v))
                (* (.-m22 m) (.getZ v)) (* (.-m23 m) (.getW v)))
             
             (+ (* (.-m30 m) (.getX v)) (* (.-m31 m) (.getY v))
                (* (.-m32 m) (.getZ v)) (* (.-m33 m) (.getW v)))))

(defn rotate-x
  "Returns a rotation matrix for the given angle."
  ([angle]
     (let [angle (Math/toRadians angle)
           cosine (Math/cos angle)
           sine (Math/sin angle)]
       (Matrix4D. 1.0 0.0 0.0 0.0
                  0.0 cosine (- sine) 0.0
                  0.0 sine cosine 0.0
                  0.0 0.0 0.0 1.0)))
  ([mat angle]
     (mult mat (rotate-x angle))))

(defn rotate-y
  "Returns a rotation matrix for the given angle."
  ([angle]
     (let [angle (Math/toRadians angle)
           cosine (Math/cos angle)
           sine (Math/sin angle)]
       (Matrix4D. cosine 0.0 sine 0.0
                  0.0 1.0 0.0 0.0
                  (- sine) 0.0 cosine 0.0
                  0.0 0.0 0.0 1.0)))
  ([mat angle]
     (mult mat (rotate-y angle))))

(defn rotate-z
  "Returns a rotation matrix for the given angle."
  ([angle]
     (let [angle (Math/toRadians angle)
           cosine (Math/cos angle)
           sine (Math/sin angle)]
       (Matrix4D. cosine (- sine) 0.0 0.0
                  sine cosine 0.0 0.0
                  0.0 0.0 1.0 0.0
                  0.0 0.0 0.0 1.0)))
  ([mat angle]
     (mult mat (rotate-z angle))))

(defn rotate
  "Rotate a matrix given an angle and axis of rotation."
  [^Matrix4D m angle ^Vector3D axis]
  (let [angle (Math/toRadians angle)
        cosine (Math/cos angle)
        sine (Math/sin angle)
        icosine (- 1.0 cosine)
        
        x (.getX axis)
        y (.getY axis)
        z (.getZ axis)

        xy (* x y)
        yz (* y z)
        xz (* x z)
        
        xs (* x sine)
        ys (* y sine)
        zs (* z sine)
        
        f00 (+ (* x x icosine) cosine)
        f10 (+ (* xy icosine) zs)
        f20 (- (* xz icosine) ys)
        
        f01 (- (* xy icosine) zs)
        f11 (+ (* y y icosine) cosine)
        f21 (+ (* yz icosine) xs)

        f02 (+ (* xz icosine) ys)
        f12 (- (* yz icosine) xs)
        f22 (+ (* z z icosine) cosine)

        t00 (+ (* (.-m00 m) f00) (* (.-m01 m) f10) (* (.-m02 m) f20))
        t10 (+ (* (.-m10 m) f00) (* (.-m11 m) f10) (* (.-m12 m) f20))
        t20 (+ (* (.-m20 m) f00) (* (.-m21 m) f10) (* (.-m22 m) f20))
        t30 (+ (* (.-m30 m) f00) (* (.-m31 m) f10) (* (.-m32 m) f20))

        t01 (+ (* (.-m00 m) f01) (* (.-m01 m) f11) (* (.-m02 m) f21))
        t11 (+ (* (.-m10 m) f01) (* (.-m11 m) f11) (* (.-m12 m) f21))
        t21 (+ (* (.-m20 m) f01) (* (.-m21 m) f11) (* (.-m22 m) f21))
        t31 (+ (* (.-m30 m) f01) (* (.-m31 m) f11) (* (.-m32 m) f21))

        t02 (+ (* (.-m00 m) f02) (* (.-m01 m) f12) (* (.-m02 m) f22))
        t12 (+ (* (.-m10 m) f02) (* (.-m11 m) f12) (* (.-m12 m) f22))
        t22 (+ (* (.-m20 m) f02) (* (.-m21 m) f12) (* (.-m22 m) f22))
        t32 (+ (* (.-m30 m) f02) (* (.-m31 m) f12) (* (.-m32 m) f22))]
    
    (Matrix4D. t00 t01 t02 (.-m03 m)
               t10 t11 t12 (.-m13 m)
               t20 t21 t22 (.-m23 m)
               t30 t31 t32 (.-m33 m))))

(defn mat2
  "Creates a new Matrix2D."
  ([] identity-mat2)
  ([m] (apply mat2 m))
  ([v1 v2]
     (Matrix2D. (v1 0) (v1 1) (v2 0) (v2 1)))
  ([m00 m01 m10 m11]
     (Matrix2D. m00 m01 m10 m11)))

(defn mat3
  "Creates a new Matrix3D."
  ([] identity-mat3)
  ([m] (apply mat3 m))
  ([v1 v2 v3]
     (Matrix3D. (v1 0) (v1 1) (v1 2)
                (v2 0) (v2 1) (v2 2)
                (v3 0) (v3 1) (v3 2)))
  ([m00 m01 m02 m10 m11 m12 m20 m21 m22]
     (Matrix3D. m00 m01 m02 m10 m11 m12 m20 m21 m22)))

(defn mat4
  "Creates a new Matrix4D."
  ([] identity-mat4)
  ([m] (apply mat4 m))
  ([v1 v2 v3 v4]
     (Matrix4D. (v1 0) (v1 1) (v1 2) (v1 3)
                (v2 0) (v2 1) (v2 2) (v2 3)
                (v3 0) (v3 1) (v3 2) (v3 3)
                (v4 0) (v4 1) (v4 2) (v4 3)))
  ([m00 m01 m02 m03 m10 m11 m12 m13 m20 m21 m22 m23 m30 m31 m32 m33]
     (Matrix4D. m00 m01 m02 m03
                m10 m11 m12 m13
                m20 m21 m22 m23
                m30 m31 m32 m33)))

(defn matrix
  "Creates a new 2D, 3D, or 4D matrix."
  ([v1 v2] (mat2 v1 v2))
  ([v1 v2 v3] (mat3 v1 v2 v3))
  ([v1 v2 v3 v4] (mat4 v1 v2 v3 v4)))

(defn into-matrix [coll]
  "Turn a collection of numbers into a math matrix."
  (if (satisfies? Matrix coll)
    coll
    (apply matrix coll)))

(defmethod print-method Matrix2D [^Matrix2D v ^java.io.Writer w]
  (.write w (.toString v)))

(defmethod print-method Matrix3D [^Matrix3D v ^java.io.Writer w]
  (.write w (.toString v)))

(defmethod print-method Matrix4D [^Matrix4D v ^java.io.Writer w]
  (.write w (.toString v)))

(defmethod print-dup Matrix2D [^Matrix2D v ^java.io.Writer w]
  (.write w (.toString v)))

(defmethod print-dup Matrix3D [^Matrix3D v ^java.io.Writer w]
  (.write w (.toString v)))

(defmethod print-dup Matrix4D [^Matrix4D v ^java.io.Writer w]
  (.write w (.toString v)))
