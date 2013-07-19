(ns euclidean.math.vector
  (:refer-clojure :exclude [vector]))

(defprotocol Vector
  (add [v1 v2]))

(definterface Coords2D
  (^double getX [])
  (^double getY []))

(defn- add-2d [^Vector2D v1 ^Vector2D v2]
  (Vector2D. (+ (.getX v1) (.getX v2))
             (+ (.getY v1) (.getY v2))))

(deftype Vector2D [^double x ^double y]
  Coords2D
  (getX [_] x)
  (getY [_] y)

  Vector
  (add [v1 v2] (add-2d v1 v2))

  clojure.lang.Counted
  (count [_] 2)

  clojure.lang.Sequential

  clojure.lang.Seqable
  (seq [_] (list x y))

  clojure.lang.ILookup
  (valAt [v i]
    (.valAt v i nil))
  (valAt [_ i not-found]
    (case i 0 x 1 y not-found))

  clojure.lang.IFn
  (invoke [v i]
    (.valAt v i))

  Object
  (toString [_]
    (str "#math/vector [" x " " y "]"))
  (equals [_ v]
    (and (= (count v) 2)
         (= x (v 0))
         (= y (v 1)))))

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
    (and (= (count v) 3)
         (= x (v 0))
         (= y (v 1))
         (= z (v 2)))))

(defn vector
  ([^double x ^double y]
     (Vector2D. x y))
  ([^double x ^double y ^double z]
     (Vector3D. x y z)))

(defn into-vector [v]
  (apply vector v))

(defmethod print-method Vector2D [v ^java.io.Writer w]
  (.write w (.toString v)))

(defmethod print-method Vector3D [v ^java.io.Writer w]
  (.write w (.toString v)))
