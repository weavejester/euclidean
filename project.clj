(defproject euclidean "0.1.6"
  :description "Library for fast, immutable 3D math"
  :url "https://github.com/weavejester/euclidean"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]]
  :plugins [[codox "0.6.4"]]
  :profiles
  {:dev {:dependencies [[criterium "0.4.1"]]}
   :1.5 {:dependencies [[org.clojure/clojure "1.5.1"]]}})
