(ns euclidean.math.vector-test
  (:require [clojure.test :refer :all]
            [euclidean.math.vector :as v]))

(deftest test-data-readers
  (is (= (pr-str (v/vector 1 2 3))
         "#math/vector [1 2 3]")))
