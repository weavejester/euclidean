(ns euclidean.math.vector
  (:refer-clojure :exclude [vector]))

(defn- add-hashcode [hash x]
  (+ hash (* 37 hash) (Float/floatToIntBits x)))

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
  (hashCode [_]
    (-> 17 (add-hashcode x)
           (add-hashcode y)))
  (equals [self v]
    (or (identical? self v)
        (and (instance? Vector2D v)
             (= x (.getX ^Vector2D v))
             (= y (.getY ^Vector2D v)))
        (and (= (count v) 2)
             (= x (v 0))
             (= y (v 1))))))

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
  (hashCode [_]
    (-> 17 (add-hashcode x)
           (add-hashcode y)
           (add-hashcode z)))
  (equals [self v]
    (or (identical? self v)
        (and (instance? Vector3D v)
             (= x (.getX ^Vector3D v))
             (= y (.getY ^Vector3D v))
             (= z (.getZ ^Vector3D v)))
        (and (= (count v) 3)
             (= x (v 0))
             (= y (v 1))
             (= z (v 2))))))

(alter-meta! #'->Vector2D assoc :no-doc true)
(alter-meta! #'->Vector3D assoc :no-doc true)

(defn- add-2d [^Vector2D v1 ^Vector2D v2]
  (Vector2D. (+ (.getX v1) (.getX v2))
             (+ (.getY v1) (.getY v2))))

(defn- sub-2d [^Vector2D v1 ^Vector2D v2]
  (Vector2D. (- (.getX v1) (.getY v2))
             (- (.getX v1) (.getY v2))))

(defn- mult-2d [^Vector2D v1 ^Vector2D v2]
  (Vector2D. (* (.getX v1) (.getX v2))
             (* (.getY v1) (.getY v2))))

(defn- div-2d [^Vector2D v1 ^Vector2D v2]
  (Vector2D. (/ (.getX v1) (.getX v2))
             (/ (.getY v1) (.getY v2))))

(defn- scale-2d [^Vector2D v ^double f]
  (Vector2D. (* (.getX v) f)
             (* (.getY v) f)))

(defn- dot-2d [^Vector2D v1 ^Vector2D v2]
  (+ (* (.getX v1) (.getX v2))
     (* (.getY v1) (.getY v2))))

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

(defn- mult-3d [^Vector3D v1 ^Vector3D v2]
  (Vector3D. (* (.getX v1) (.getX v2))
             (* (.getY v1) (.getY v2))
             (* (.getZ v1) (.getZ v2))))

(defn- div-3d [^Vector3D v1 ^Vector3D v2]
  (Vector3D. (/ (.getX v1) (.getX v2))
             (/ (.getY v1) (.getY v2))
             (/ (.getZ v1) (.getZ v2))))

(defn- scale-3d [^Vector3D v ^double f]
  (Vector3D. (* (.getX v) f)
             (* (.getY v) f)
             (* (.getZ v) f)))

(defn- dot-3d [^Vector3D v1 ^Vector3D v2]
  (+ (* (.getX v1) (.getX v2))
     (* (.getY v1) (.getY v2))
     (* (.getZ v1) (.getZ v2))))

(defn- magnitude-3d [^Vector3D v]
  (let [x (.getX v)
        y (.getY v)
        z (.getZ v)]
    (Math/sqrt (+ (* x x) (* y y) (* z z)))))

(defprotocol Vector
  (add [v1 v2] "Add two vectors together.")
  (sub [v1 v2] "Subject the first vector from the second.")
  (mult [v1 v2] "Multiply one vector by another.")
  (div [v1 v2] "Divide one vector by another.")
  (scale [v f] "Scale a vector by a factor.")
  (dot [v1 v2] "Find the dot-product of two vectors.")
  (magnitude [v] "The magnitude (length) of the vector."))

(extend-protocol Vector
  Vector2D
  (add [v1 v2] (add-2d v1 v2))
  (sub [v1 v2] (sub-2d v1 v2))
  (mult [v1 v2] (mult-2d v1 v2))
  (div [v1 v2] (div-2d v1 v2))
  (scale [v f] (scale-2d v f))
  (dot [v1 v2] (dot-2d v1 v2))
  (magnitude [v] (magnitude-2d v))
  Vector3D
  (add [v1 v2] (add-3d v1 v2))
  (sub [v1 v2] (sub-3d v1 v2))
  (mult [v1 v2] (mult-3d v1 v2))
  (div [v1 v2] (div-3d v1 v2))
  (scale [v f] (scale-3d v f))
  (dot [v1 v2] (dot-3d v1 v2))
  (magnitude [v] (magnitude-3d v)))

(defn normalize
  "Normalize a vector by dividing by its magnitude."
  [v]
  (scale v (/ 1.0 (magnitude v))))

(defn cross
  "Find the cross-product of two 3D vectors."
  [^Vector3D v1 ^Vector3D v2]
  (Vector3D. (- (* (.getY v1) (.getZ v2))
                (* (.getZ v1) (.getY v2)))
             (- (* (.getZ v1) (.getX v2))
                (* (.getX v1) (.getZ v2)))
             (- (* (.getX v1) (.getY v2))
                (* (.getY v1) (.getX v2)))))

(defn vector
  "Create a new 2D or 3D math vector."
  ([^double x ^double y]
     (Vector2D. x y))
  ([^double x ^double y ^double z]
     (Vector3D. x y z)))

(defn into-vector [coll]
  "Turn a collection of numbers into a math vector."
  (if (or (instance? Vector2D coll)
          (instance? Vector3D coll))
    coll
    (apply vector coll)))

(defmethod print-method Vector2D [^Vector2D v ^java.io.Writer w]
  (.write w (.toString v)))

(defmethod print-method Vector3D [^Vector3D v ^java.io.Writer w]
  (.write w (.toString v)))

(defmethod print-dup Vector2D [^Vector2D v ^java.io.Writer w]
  (.write w (.toString v)))

(defmethod print-dup Vector3D [^Vector3D v ^java.io.Writer w]
  (.write w (.toString v)))
