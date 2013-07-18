(ns euclidean.math.vector
  (:refer-clojure :exclude [vector]))

(deftype Vector3D [x y z]
  Object
  (toString [_] (str "#math/vector [" x " " y " " z "]")))

(defn vector [x y z]
  (Vector3D. x y z))

(defn into-vector [v]
  (apply vector v))

(defmethod print-method Vector3D [v ^java.io.Writer w]
  (.write w (.toString v)))
