(ns euclidean.math.quaternion
  (:require [euclidean.math.vector :as v])
  (:import euclidean.math.vector.Vector3D))

(definterface Coords4D
  (^double getX [])
  (^double getY [])
  (^double getZ [])
  (^double getW []))

(deftype Quaternion [^double x ^double y ^double z ^double w]
  Coords4D
  (getX [_] x)
  (getY [_] y)
  (getZ [_] z)
  (getW [_] w)

  clojure.lang.Counted
  (count [_] 4)

  clojure.lang.Sequential

  clojure.lang.Seqable
  (seq [_] (list x y z w))

  clojure.lang.ILookup
  (valAt [q i]
    (.valAt q i nil))
  (valAt [_ i not-found]
    (case i 0 x 1 y 2 z 3 w not-found))

  clojure.lang.IFn
  (invoke [q i]
    (.valAt q i))

  Object
  (toString [_]
    (str "#math/quaternion [" x " " y  " " z " " w "]"))
  (equals [_ q]
    (and (= (count q) 4)
         (= x (q 0))
         (= y (q 1))
         (= z (q 2))
         (= w (q 3)))))

(defn mult
  "Multiply two quaternions together."
  [^Quaternion q1 ^Quaternion q2]
  (let [x1 (.getX q1), y1 (.getY q1), z1 (.getZ q1), w1 (.getW q1)
        x2 (.getX q2), y2 (.getY q2), z2 (.getZ q2), w2 (.getW q2)]
    [(+ (* x1 w2)     (* y1 z2)     (- (* z1 y2)) (* w1 x2))
     (+ (- (* x1 z2)) (* y1 w2)     (* z1 x2)     (* w1 y2))
     (+ (* x1 y2)     (- (* y1 x2)) (* z1 w2)     (* w1 z2))
     (+ (- (* x1 x2)) (- (* y1 y2)) (- (* z1 z2)) (* w1 w2))]))

(defn rotate
  "Rotate a vector with a quaternion."
  [^Quaternion q ^Vector3D v]
  (let [qx (.getX q), qy (.getY q), qz (.getZ q), qw (.getW q)
        vx (.getX v), vy (.getY v), vz (.getZ v)]
    [(+ (* qw qw vx)     (* 2 qy qw vz) (* -2 qz qw vy)  (* qx qx vx)
        (* 2 qy qx vy)   (* 2 qz qx vz) (- (* qz qz vx)) (- (* qy qy vx)))
     (+ (* 2 qx qy vx)   (* qy qy vy)   (* 2 qz qy vz)   (* 2 qw qz vx)
        (- (* qz qz vy)) (* qw qw vy)   (* -2 qx qw vz)  (- (* qx qx vy)))
     (+ (* 2 qx qz vx)   (* 2 qy qz vy) (* qz qz vz)     (* -2 qw qy vx)
        (- (* qy qy vz)) (* 2 qw qx vy) (- (* qx qx vz)) (* qw qw vz))]))

(defn from-angle-normal-axis
  "Create a quaternion from an angle in radians and a normalized axis vector."
  [^double angle ^Vector3D axis]
  (let [half-angle (* angle 0.5)
        half-sine  (Math/sin half-angle)]
    (Quaternion. (* half-sine (.getX axis))
                 (* half-sine (.getY axis))
                 (* half-sine (.getZ axis))
                 (Math/cos half-angle))))

(defn from-angle-axis
  "Create a quaternion from an angle in radians and an arbitrary axis vector."
  [^double angle ^Vector3D axis]
  (from-angle-normal-axis angle (v/normalize axis)))

(defn pitch
  "Create a quaternion representing a pitch rotation by an angle in radians."
  [^double angle]
  (from-angle-normal-axis angle (v/vector 1 0 0)))

(defn yaw
  "Create a quaternion representing a yaw rotation by an angle in radians."
  [^double angle]
  (from-angle-normal-axis angle (v/vector 0 1 0)))

(defn roll
  "Create a quaternion representing a roll rotation by an angle in radians."
  [^double angle]
  (from-angle-normal-axis angle (v/vector 0 0 1)))

(defn norm
  "Compute the norm of the quaternion."
  [^Quaternion q]
  (let [x (.getX q)
        y (.getY q)
        z (.getZ q)
        w (.getW q)]
    (+ (* x x) (* y y) (* z z) (* w w))))

(defn axes
  "Return the three axes of the quaternion."
  [^Quaternion q]
  (let [n  (norm q),  s  (if (> n 0) (/ 2.0 n) 0.0)
        x  (.getX q), y  (.getY q), z  (.getZ q), w  (.getW q)
        xs (* x s),   ys (* y s),   zs (* z s),   ws (* w s)
        xx (* x xs),  xy (* x ys),  xz (* x zs),  xw (* x ws)
        yy (* y ys),  yz (* y zs),  yw (* y ws)
        zz (* z zs),  zw (* z ws)]
    [(Vector3D. (- 1.0 (+ yy zz)) (+ xy zw) (- xz yw))
     (Vector3D. (- xy zw) (- 1.0 (+ xx zz)) (+ yz xw))
     (Vector3D. (+ xz yw) (- yz xw) (- 1.0 (+ xx yy)))]))

(defn quaternion [^double x ^double y ^double z ^double w]
  (Quaternion. x y z w))

(defn into-quaternion [coll]
  (apply quaternion coll))

(defmethod print-method Quaternion [^Quaternion q ^java.io.Writer w]
  (.write w (.toString q)))
