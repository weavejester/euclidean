(ns euclidean.math.quaternion
  (:refer-clojure :exclude [identity])
  (:require [euclidean.math.vector :as v])
  (:import euclidean.math.vector.Vector3D))

(defn- add-hashcode [hash x]
  (+ hash (* 37 hash) (Float/floatToIntBits x)))

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
    (case (int i) 0 x 1 y 2 z 3 w not-found))

  clojure.lang.IFn
  (invoke [q i]
    (.valAt q i))

  Object
  (toString [_]
    (str "#math/quaternion [" x " " y  " " z " " w "]"))
  (hashCode [_]
    (-> 17 (add-hashcode x)
           (add-hashcode y)
           (add-hashcode z)
           (add-hashcode w)))
  (equals [self q]
    (or (identical? self q)
        (and (instance? Quaternion q)
             (= x (.getX ^Quaternion q))
             (= y (.getY ^Quaternion q))
             (= z (.getZ ^Quaternion q))
             (= w (.getW ^Quaternion q)))
        (and (counted? q)
             (= (count q) 4)
             (= x (q 0))
             (= y (q 1))
             (= z (q 2))
             (= w (q 3))))))

(alter-meta! #'->Quaternion assoc :no-doc true)

(defn mult
  "Multiply two quaternions together."
  ([q] q)
  ([^Quaternion q1 ^Quaternion q2]
     (let [x1 (.getX q1), y1 (.getY q1), z1 (.getZ q1), w1 (.getW q1)
           x2 (.getX q2), y2 (.getY q2), z2 (.getZ q2), w2 (.getW q2)]
       (Quaternion.
        (+ (* x1 w2)     (* y1 z2)     (- (* z1 y2)) (* w1 x2))
        (+ (- (* x1 z2)) (* y1 w2)     (* z1 x2)     (* w1 y2))
        (+ (* x1 y2)     (- (* y1 x2)) (* z1 w2)     (* w1 z2))
        (+ (- (* x1 x2)) (- (* y1 y2)) (- (* z1 z2)) (* w1 w2)))))
  ([q1 q2 & more]
     (reduce mult q1 (cons q2 more))))

(defn rotate
  "Rotate a vector with a quaternion."
  [^Quaternion q ^Vector3D v]
  (let [qx (.getX q), qy (.getY q), qz (.getZ q), qw (.getW q)
        vx (.getX v), vy (.getY v), vz (.getZ v)]
    (Vector3D.
     (+ (* qw qw vx)     (* 2 qy qw vz) (* -2 qz qw vy)  (* qx qx vx)
        (* 2 qy qx vy)   (* 2 qz qx vz) (- (* qz qz vx)) (- (* qy qy vx)))
     (+ (* 2 qx qy vx)   (* qy qy vy)   (* 2 qz qy vz)   (* 2 qw qz vx)
        (- (* qz qz vy)) (* qw qw vy)   (* -2 qx qw vz)  (- (* qx qx vy)))
     (+ (* 2 qx qz vx)   (* 2 qy qz vy) (* qz qz vz)     (* -2 qw qy vx)
        (- (* qy qy vz)) (* 2 qw qx vy) (- (* qx qx vz)) (* qw qw vz)))))

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
  (from-angle-normal-axis angle (Vector3D. 1 0 0)))

(defn yaw
  "Create a quaternion representing a yaw rotation by an angle in radians."
  [^double angle]
  (from-angle-normal-axis angle (Vector3D. 0 1 0)))

(defn roll
  "Create a quaternion representing a roll rotation by an angle in radians."
  [^double angle]
  (from-angle-normal-axis angle (Vector3D. 0 0 1)))

(defn norm-squared
  "Compute the square norm of the quaternion, equal to the sum of the squares
   of each of the quaternion `q`'s coefficients."
  [^Quaternion q]
  (let [x (.getX q)
        y (.getY q)
        z (.getZ q)
        w (.getW q)]
    (+ (* x x) (* y y) (* z z) (* w w))))

(defn axes
  "Return the three axes of the quaternion."
  [^Quaternion q]
  (let [n  (norm-squared q),  s  (if (> n 0) (/ 2.0 n) 0.0)
        x  (.getX q), y  (.getY q), z  (.getZ q), w  (.getW q)
        xs (* x s),   ys (* y s),   zs (* z s),   ws (* w s)
        xx (* x xs),  xy (* x ys),  xz (* x zs),  xw (* x ws)
        yy (* y ys),  yz (* y zs),  yw (* y ws)
        zz (* z zs),  zw (* z ws)]
    [(Vector3D. (- 1.0 (+ yy zz)) (+ xy zw) (- xz yw))
     (Vector3D. (- xy zw) (- 1.0 (+ xx zz)) (+ yz xw))
     (Vector3D. (+ xz yw) (- yz xw) (- 1.0 (+ xx yy)))]))

(defn from-axes
  "Create a quaternion from three axis vectors."
  [^Vector3D x-axis ^Vector3D y-axis ^Vector3D z-axis]
  (let [m00 (.getX x-axis), m01 (.getX y-axis), m02 (.getX z-axis)
        m10 (.getY x-axis), m11 (.getY y-axis), m12 (.getY z-axis)
        m20 (.getZ x-axis), m21 (.getZ y-axis), m22 (.getZ z-axis)
        trace (+ m00 m11 m22)]
    (cond
     (>= trace 0)
     (let [s (Math/sqrt (inc trace))
           r (/ 0.5 s)]
       (Quaternion. (* r (- m21 m12))
                    (* r (- m02 m20))
                    (* r (- m10 m01))
                    (* 0.5 s)))
     (and (> m00 m11) (> m00 m22))
     (let [s (Math/sqrt (- (inc m00) m11 m22))
           r (/ 0.5 s)]
       (Quaternion. (* 0.5 s)
                    (* r (+ m10 m01))
                    (* r (+ m02 m20))
                    (* r (- m21 m12))))
     (> m11 m22)
     (let [s (Math/sqrt (- (inc m11) m00 m22))
           r (/ 0.5 s)]
       (Quaternion. (* r (+ m10 m01))
                    (* 0.5 s)
                    (* r (+ m21 m12))
                    (* r (- m02 m20))))
     :else
     (let [s (Math/sqrt (- (inc m22) m00 m11))
           r (/ 0.5 s)]
       (Quaternion. (* r (+ m02 m20))
                    (* r (+ m21 m12))
                    (* 0.5 s)
                    (* r (- m10 m01)))))))

(defn look-at
  "Create a quaternion that is directed at a point specified by a vector."
  [^Vector3D direction ^Vector3D up]
  (let [z-axis (v/normalize direction)
        x-axis (v/normalize (v/cross up direction))
        y-axis (v/normalize (v/cross direction x-axis))]
    (from-axes x-axis y-axis z-axis)))

(defn get-x
  "Get the x component of a quaternion."
  [^Quaternion q]
  (.getX q))

(defn get-y
  "Get the y component of a quaternion."
  [^Quaternion q]
  (.getY q))

(defn get-z
  "Get the z component of a quaternion."
  [^Quaternion q]
  (.getZ q))

(defn get-w
  "Get the w component of a quaternion."
  [^Quaternion q]
  (.getW q))

(defn quaternion
  "Create a new quaternion."
  [^double x ^double y ^double z ^double w]
  (Quaternion. x y z w))

(def identity
  "The identity quaternion."
  (quaternion 0 0 0 1))

(defn into-quaternion
  "Turn a collection of 4 numbers into a quaternion."
  [coll]
  (if (instance? Quaternion coll)
    coll
    (apply quaternion coll)))

(defmethod print-method Quaternion [^Quaternion q ^java.io.Writer w]
  (.write w (.toString q)))

(defmethod print-dup Quaternion [^Quaternion q ^java.io.Writer w]
  (.write w (.toString q)))
