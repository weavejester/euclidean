(ns euclidean.math.vector
  (:refer-clojure :exclude [vector]))

(definterface Coords3D
  (^double getX [])
  (^double getY [])
  (^double getZ []))

(defprotocol Vector
  (add [v1 v2]))

(deftype Vector3D [^double x ^double y ^double z]
  Coords3D
  (getX [_] x)
  (getY [_] y)
  (getZ [_] z)

  Vector
  (add [_ v]
    (Vector3D. (+ x (.getX ^Coords3D v))
               (+ y (.getY ^Coords3D v))
               (+ z (.getZ ^Coords3D v))))

  clojure.lang.Counted
  (count [_] 3)

  clojure.lang.Sequential

  clojure.lang.Seqable
  (seq [_] (list x y z))

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

(defn vector [^double x ^double y ^double z]
  (Vector3D. x y z))

(defn into-vector [v]
  (apply vector v))

(defmethod print-method Vector3D [v ^java.io.Writer w]
  (.write w (.toString v)))
