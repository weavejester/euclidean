(ns euclidean.math.vector
  (:refer-clojure :exclude [vector]))

(deftype Vector3D [x y z]
  clojure.lang.ILookup
  (valAt [v i]
    (.valAt v i nil))
  (valAt [_ i not-found]
    (case i 0 x 1 y 2 z not-found))

  clojure.lang.IFn
  (invoke [v i]
    (.valAt v i))

  Object
  (toString [_]
    (str "#math/vector [" x " " y " " z "]"))
  (equals [_ v]
    (and (= x (v 0))
         (= y (v 1))
         (= z (v 2)))))

(defn vector [x y z]
  (Vector3D. x y z))

(defn add [v1 v2]
  (vector (+ (v1 0) (v2 0))
          (+ (v1 1) (v2 1))
          (+ (v1 2) (v2 2))))

(defn into-vector [v]
  (apply vector v))

(defmethod print-method Vector3D [v ^java.io.Writer w]
  (.write w (.toString v)))
