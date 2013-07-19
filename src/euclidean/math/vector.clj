(ns euclidean.math.vector
  (:refer-clojure :exclude [vector]))

(definterface Coords2D
  (^double getX [])
  (^double getY []))

(deftype Vector2D [^double x ^double y]
  Coords2D
  (getX [_] x)
  (getY [_] y)

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

(deftype Vector3D [^double x ^double y ^double z]
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

(defn- add-2d [^Vector2D v1 ^Vector2D v2]
  (Vector2D. (+ (.getX v1) (.getX v2))
             (+ (.getY v1) (.getY v2))))

(defn- sub-2d [^Vector2D v1 ^Vector2D v2]
  (Vector2D. (- (.getX v1) (.getY v2))
             (- (.getX v1) (.getY v2))))

(defn- mult-2d [^Vector2D v ^double f]
  (Vector2D. (* (.getX v) f)
             (* (.getY v) f)))

(defn- magnitude-2d [^Vector2D v]
  (let [x (.getX v)
        y (.getY v)]
    (Math/sqrt (+ (* x x) (* y y)))))

(defn- add-3d [^Vector3D v1 ^Vector3D v2]
  (Vector3D. (+ (.getX v1) (.getX v2))
             (+ (.getY v1) (.getY v2))
             (+ (.getZ v1) (.getZ v2))))

(defn- sub-3d [^Vector3D v1 ^Vector3D v2]
  (Vector3D. (- (.getX v1) (.getX v2))
             (- (.getY v1) (.getY v2))
             (- (.getZ v1) (.getZ v2))))

(defn- mult-3d [^Vector3D v ^double f]
  (Vector3D. (* (.getX v) f)
             (* (.getY v) f)
             (* (.getZ v) f)))

(defn- magnitude-3d [^Vector3D v]
  (let [x (.getX v)
        y (.getY v)
        z (.getZ v)]
    (Math/sqrt (+ (* x x) (* y y) (* z z)))))

(defprotocol Vector
  (add [v1 v2] "Add two vectors together.")
  (sub [v1 v2] "Subject the first vector from the second.")
  (mult [v f] "Multiply all values in a vector by a number.")
  (magnitude [v] "The magnitude (length) of the vector."))

(extend-protocol Vector
  Vector2D
  (add [v1 v2] (add-2d v1 v2))
  (sub [v1 v2] (sub-2d v1 v2))
  (mult [v f] (mult-2d v f))
  (magnitude [v] (magnitude-2d v))
  Vector3D
  (add [v1 v2] (add-3d v1 v2))
  (sub [v1 v2] (sub-3d v1 v2))
  (mult [v f] (mult-3d v f))
  (magnitude [v] (magnitude-3d v)))

(defn vector
  "Create a new 2D or 3D math vector."
  ([^double x ^double y]
     (Vector2D. x y))
  ([^double x ^double y ^double z]
     (Vector3D. x y z)))

(defn into-vector [coll]
  "Turn a collection of numbers into a math vector."
  (apply vector coll))

(defmethod print-method Vector2D [v ^java.io.Writer w]
  (.write w (.toString v)))

(defmethod print-method Vector3D [v ^java.io.Writer w]
  (.write w (.toString v)))
