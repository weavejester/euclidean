(ns euclidean.math.quaternion)

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
    (and (= (count q) q)
         (= x (q 0))
         (= y (q 1))
         (= z (q 2))
         (= w (q 3)))))

(defn quaternion [^double x ^double y ^double z ^double w]
  (Quaternion. x y z w))

(defn into-quaternion [coll]
  (apply quaternion coll))

(defmethod print-method Quaternion [^Quaternion q ^java.io.Writer w]
  (.write w (.toString q)))
