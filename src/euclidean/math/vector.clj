(ns euclidean.math.vector
  (:refer-clojure :exclude [vector]))

(defprotocol Vector
  (add [v1 v2]))

(definterface Coords3D
  (^double getX [])
  (^double getY [])
  (^double getZ []))

(defn- add-3d [^Vector3D v1 ^Vector3D v2]
  (Vector3D. (+ (.getX v1) (.getX v2))
             (+ (.getY v1) (.getY v2))
             (+ (.getZ v1) (.getZ v2))))

(deftype Vector3D [^double x ^double y ^double z]
  Coords3D
  (getX [_] x)
  (getY [_] y)
  (getZ [_] z)

  Vector
  (add [v1 v2] (add-3d v1 v2))

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
