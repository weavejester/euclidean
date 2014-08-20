(ns euclidean.math.matrix
  (:require [euclidean.math.vector :as vec])
  (:import (euclidean.math.vector Vector2D Vector3D)))

(defn- add-hashcode [hash x]
  (+ hash (* 37 hash) (Float/floatToIntBits x)))

(deftype Matrix2D [^double m00 ^double m01
                   ^double m10 ^double m11]
  clojure.lang.Counted
  (count [_] 2)

  clojure.lang.Sequential

  clojure.lang.Seqable
  (seq [_] (list (Vector2D. m00 m01) (Vector2D. m10 m11)))

  clojure.lang.ILookup
  (valAt [v i]
    (.valAt v i nil))
  (valAt [_ i not-found]
    (case (int i)
      0 (Vector2D. m00 m01)
      1 (Vector2D. m10 m11)
      not-found))

  clojure.lang.IFn
  (invoke [v i]
    (.valAt v i))

  Object
  (toString [_]
    (str "#math/matrix [" m00 " " m01 " " m10 " " m11 "]"))
  (hashCode [_]
    (-> 17
        (add-hashcode m00)
        (add-hashcode m01)
        (add-hashcode m10)
        (add-hashcode m11)))
  (equals [self v]
    (or (identical? self v)
        (and (instance? Matrix2D v)
             (== m00 (.-m00 ^Matrix2D v))
             (== m01 (.-m01 ^Matrix2D v))
             (== m10 (.-m10 ^Matrix2D v))
             (== m11 (.-m11 ^Matrix2D v)))
        (and (counted? v)
             (= (count v) 2)
             (= (Vector2D. m00 m01) (v 0))
             (= (Vector2D. m10 m11) (v 1))))))

(definterface Coords3D
  (^double getX [])
  (^double getY [])
  (^double getZ []))

(deftype Matrix3D [^double x ^double y ^double z]
  Coords3D
  (getX [_] x)
  (getY [_] y)
  (getZ [_] z)
  
  clojure.lang.Counted
  (count [_] 3)

  clojure.lang.Sequential

  clojure.lang.Seqable
  (seq [_] (list x y z))

  clojure.lang.ILookup
  (valAt [v i]
    (.valAt v i nil))
  (valAt [_ i not-found]
    (case (int i)
      0 x
      1 y
      2 z
      not-found))

  clojure.lang.IFn
  (invoke [v i]
    (.valAt v i))

  Object
  (toString [_]
    (str "#math/matrix [" x " " y " " z "]"))
  (hashCode [_]
    (-> 17 (add-hashcode x)
           (add-hashcode y)
           (add-hashcode z)))
  (equals [self v]
    (or (identical? self v)
        (and (instance? Matrix3D v)
             (= x (.-x ^Matrix3D v))
             (= y (.-y ^Matrix3D v))
             (= z (.-z ^Matrix3D v)))
        (and (counted? v)
             (= (count v) 3)
             (= x (v 0))
             (= y (v 1))
             (= z (v 2))))))

(deftype Matrix4D [^double x ^double y ^double z]
  clojure.lang.Counted
  (count [_] 3)

  clojure.lang.Sequential

  clojure.lang.Seqable
  (seq [_] (list x y z))

  clojure.lang.ILookup
  (valAt [v i]
    (.valAt v i nil))
  (valAt [_ i not-found]
    (case (int i)
      0 x
      1 y
      2 z
      not-found))

  clojure.lang.IFn
  (invoke [v i]
    (.valAt v i))

  Object
  (toString [_]
    (str "#math/matrix [" x " " y " " z "]"))
  (hashCode [_]
    (-> 17 (add-hashcode x)
           (add-hashcode y)
           (add-hashcode z)))
  (equals [self v]
    (or (identical? self v)
        (and (instance? Matrix3D v)
             (= x (.-x ^Matrix3D v))
             (= y (.-y ^Matrix3D v))
             (= z (.-z ^Matrix3D v)))
        (and (counted? v)
             (= (count v) 3)
             (= x (v 0))
             (= y (v 1))
             (= z (v 2))))))

(alter-meta! #'->Matrix2D assoc :no-doc true)
(alter-meta! #'->Matrix3D assoc :no-doc true)

(definline ^:private add-2d
  [^Matrix2D m1 ^Matrix2D m2]
  `(Matrix2D. (+ (.-m00 ~m1) (.-m00 ~m2))
              (+ (.-m01 ~m1) (.-m01 ~m2))
              (+ (.-m10 ~m1) (.-m10 ~m2))
              (+ (.-m11 ~m1) (.-m11 ~m2))))

(definline ^:private sub-2d
  [^Matrix2D m1 ^Matrix2D m2]
  `(Matrix2D. (- (.-m00 ~m1) (.-m00 ~m2))
              (- (.-m01 ~m1) (.-m01 ~m2))
              (- (.-m10 ~m1) (.-m10 ~m2))
              (- (.-m11 ~m1) (.-m11 ~m2))))

(definline ^:private mult-2d
  [^Matrix2D m1 ^Matrix2D m2]
  `(Matrix2D. (+ (* (.-m00 ~m1) (.-m00 ~m2))
                 (* (.-m01 ~m1) (.-m10 ~m2)))

              (+ (* (.-m00 ~m1) (.-m01 ~m2))
                 (* (.-m01 ~m1) (.-m11 ~m2)))
              
              (+ (* (.-m10 ~m1) (.-m00 ~m2))
                 (* (.-m11 ~m1) (.-m10 ~m2)))                          
              
              (+ (* (.-m10 ~m1) (.-m01 ~m2))
                 (* (.-m11 ~m1) (.-m11 ~m2)))))

(definline ^:private negate-2d
  [^Matrix2D m]
  `(Matrix2D. (- (.-m00 ~m)) (- (.-m01 ~m)) (- (.-m10 ~m)) (- (.-m11 ~m))))

(definline ^:private det-2d
  [^Matrix2D m]
  `(- (* (.-m00 ~m) (.-m11 ~m)) (* (.-m10 ~m) (.-m01 ~m))))

(definline ^:private invert-2d
  [^Matrix2D m]
  `(let [det# (det-2d ~m)]
     (when-not (zero? det#)
       (let [det-inv# (/ det#)]
         (Matrix2D. (* (.-m11 ~m) det-inv#)
                    (- (* (.-m01 ~m) det-inv#))
                    (- (* (.-m10 ~m) det-inv#))
                    (* (.-m00 ~m) det-inv#))))))

(definline ^:private transpose-2d
  [^Matrix2D m]
  `(Matrix2D. (.-m00 ~m) (.-m10 ~m) (.-m01 ~m) (.-m11 ~m)))

(defn- add-3d [^Matrix3D m1 ^Matrix3D m2]
  (Matrix3D. (+ (.getX m1) (.getX m2))
             (+ (.getY m1) (.getY m2))
             (+ (.getZ m1) (.getZ m2))))

(defn- sub-3d [^Matrix3D m1 ^Matrix3D m2]
  (Matrix3D. (- (.getX m1) (.getX m2))
             (- (.getY m1) (.getY m2))
             (- (.getZ m1) (.getZ m2))))

(defn- mult-3d [^Matrix3D m1 ^Matrix3D m2]
  (Matrix3D. (* (.getX m1) (.getX m2))
             (* (.getY m1) (.getY m2))
             (* (.getZ m1) (.getZ m2))))

(definline ^:private negate-3d
  [^Matrix3D m]
  )

(definline ^:private invert-3d
  [^Matrix3D m]
  )

(definline ^:private det-3d
  [^Matrix3D m]
  )

(definline ^:private transpose-3d
  [^Matrix3D m]
  )

(definline ^:private add-4d
  [^Matrix2D m1 ^Matrix2D m2]
  `(Matrix2D. (+ (.-m00 ~m1) (.-m00 ~m2))
              (+ (.-m01 ~m1) (.-m01 ~m2))
              (+ (.-m10 ~m1) (.-m10 ~m2))
              (+ (.-m11 ~m1) (.-m11 ~m2))))

(definline ^:private sub-4d
  [^Matrix2D m1 ^Matrix2D m2]
  `(Matrix2D. (- (.-m00 ~m1) (.-m00 ~m2))
              (- (.-m01 ~m1) (.-m01 ~m2))
              (- (.-m10 ~m1) (.-m10 ~m2))
              (- (.-m11 ~m1) (.-m11 ~m2))))

(definline ^:private mult-4d
  [^Matrix2D m1 ^Matrix2D m2]
  `(Matrix2D. (+ (* (.-m00 ~m1) (.-m00 ~m2))
                 (* (.-m01 ~m1) (.-m10 ~m2)))

              (+ (* (.-m00 ~m1) (.-m01 ~m2))
                 (* (.-m01 ~m1) (.-m11 ~m2)))
              
              (+ (* (.-m10 ~m1) (.-m00 ~m2))
                 (* (.-m11 ~m1) (.-m10 ~m2)))                          
              
              (+ (* (.-m10 ~m1) (.-m01 ~m2))
                 (* (.-m11 ~m1) (.-m11 ~m2)))))

(definline ^:private negate-4d
  [^Matrix2D m]
  `(Matrix2D. (- (.-m00 ~m)) (- (.-m01 ~m)) (- (.-m10 ~m)) (- (.-m11 ~m))))

(definline ^:private det-4d
  [^Matrix2D m]
  `(- (* (.-m00 ~m) (.-m11 ~m)) (* (.-m10 ~m) (.-m01 ~m))))

(definline ^:private invert-4d
  [^Matrix2D m]
  `(let [det# (det-4d ~m)]
     (when-not (zero? det#)
       (let [det-inv# (/ det#)]
         (Matrix2D. (* (.-m11 ~m) det-inv#)
                    (- (* (.-m01 ~m) det-inv#))
                    (- (* (.-m10 ~m) det-inv#))
                    (* (.-m00 ~m) det-inv#))))))

(definline ^:private transpose-4d
  [^Matrix2D m]
  `(Matrix2D. (.-m00 ~m) (.-m10 ~m) (.-m01 ~m) (.-m11 ~m)))

(defprotocol Matrix
  (^:no-doc add* [m1 m2] "Add two matrices together.")
  (^:no-doc sub* [m1 m2] "Subtract the second matrix from the first.")
  (^:no-doc mult* [m1 m2] "Multiply one matrix by another.")
  (^:no-doc negate [m] "Negates each component of the input matrix.")
  (^:no-doc invert [m] "Inverts the input matrix.")
  (^:no-doc determinant [m] "Returns the determinant of the input matrix.")
  (^:no-doc transpose [m] "Returns the transpose of the input matrix."))

(defprotocol AdditiveIdentity
  (add-identity [x] "Returns the additive identity of the input."))

(defprotocol MultiplicativeIdentity
  (mult-identity [x] "Returns the multiplicative identity of the input."))

(extend Matrix2D
  Matrix
  {:add* add-2d
   :sub* sub-2d
   :mult* mult-2d
   :negate negate-2d
   :invert invert-2d
   :determinant det-2d
   :transpose transpose-2d}

  AdditiveIdentity
  {:add-identity (constantly (Matrix2D. 0.0 0.0 0.0 0.0))}

  MultiplicativeIdentity
  {:mult-identity (constantly (Matrix2D. 1.0 0.0 0.0 1.0))})

(extend Matrix3D
  Matrix
  {:add* add-3d
   :sub* sub-3d
   :mult* mult-3d
   :negate negate-3d
   :invert invert-3d
   :determinant det-3d
   :transpose transpose-3d}

  AdditiveIdentity
  {:add-identity (constantly (Matrix2D. 0.0 0.0 0.0 0.0))}

  MultiplicativeIdentity
  {:mult-identity (constantly (Matrix2D. 1.0 0.0 0.0 1.0))})

(extend Matrix4D
  Matrix
  {:add* add-4d
   :sub* sub-4d
   :mult* mult-4d
   :negate negate-4d
   :invert invert-4d
   :determinant det-4d
   :transpose transpose-4d}

  AdditiveIdentity
  {:add-identity (constantly (Matrix2D. 0.0 0.0 0.0 0.0))}

  MultiplicativeIdentity
  {:mult-identity (constantly (Matrix2D. 1.0 0.0 0.0 1.0))})

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

(defn mat2
  ([] (Matrix2D. 1.0 0.0 0.0 1.0))
  ([[^double m00 ^double m01] [^double m10 ^double m11]]
     (Matrix2D. m00 m01 m10 m11))
  ([^double m00 ^double m01 ^double m10 ^double m11]
     (Matrix2D. m00 m01 m10 m11)))

(defn mat3
  ([] (Matrix2D. 1.0 0.0 0.0 1.0))
  ([[^double m00 ^double m01] [^double m10 ^double m11]]
     (Matrix2D. m00 m01 m10 m11))
  ([^double m00 ^double m01 ^double m10 ^double m11]
     (Matrix2D. m00 m01 m10 m11)))

(defn mat4
  ([] (Matrix2D. 1.0 0.0 0.0 1.0))
  ([[^double m00 ^double m01] [^double m10 ^double m11]]
     (Matrix2D. m00 m01 m10 m11))
  ([m00 m01 m10 m11 m10 m11 m12 m13 m20 m21 m22 m23 m30 m31 m32 m33]
     (Matrix2D. m00 m01 m10 m11)))

(defn matrix
  "Create a new 2D or 3D math matrix."
  ([[^double m00 ^double m01] [^double m10 ^double m11]]
     (Matrix2D. m00 m01 m10 m11))
  ([[^double m00 ^double m01 ^double m02]
    [^double m10 ^double m11 ^double m12]
    [^double m20 ^double m21 ^double m22]]
     (Matrix3D. m00 m01 m02))
  ([[^double m00 ^double m01 ^double m02 ^double m03]
    [^double m10 ^double m11 ^double m12 ^double m13]
    [^double m20 ^double m21 ^double m22 ^double m23]
    [^double m30 ^double m31 ^double m32 ^double m33]]
     (Matrix3D. m00 m01 m02)))

(defn into-matrix [coll]
  "Turn a collection of numbers into a math matrix."
  (if (or (instance? Matrix2D coll)
          (instance? Matrix3D coll))
    coll
    (apply matrix coll)))

(defmethod print-method Matrix2D [^Matrix2D v ^java.io.Writer w]
  (.write w (.toString v)))

(defmethod print-method Matrix3D [^Matrix3D v ^java.io.Writer w]
  (.write w (.toString v)))

(defmethod print-dup Matrix2D [^Matrix2D v ^java.io.Writer w]
  (.write w (.toString v)))

(defmethod print-dup Matrix3D [^Matrix3D v ^java.io.Writer w]
  (.write w (.toString v)))
