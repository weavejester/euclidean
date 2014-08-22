(defproject euclidean "0.2.0"
  :description "Library for fast, immutable 3D math"
  :url "https://github.com/weavejester/euclidean"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]]
  :plugins [[codox "0.8.10"]]  
  :profiles
  {:dev {:dependencies [[org.clojure/clojure "1.6.0"]
                        [org.clojure/test.check "0.5.9"]
                        [org.lwjgl.lwjgl/lwjgl "2.9.1"]
                        [org.lwjgl.lwjgl/lwjgl_util "2.9.1"]
                        [criterium "0.4.2"]]
         :jvm-opts ^:replace []
         :global-vars {*warn-on-reflection* true}}
   :1.5 {:dependencies [[org.clojure/clojure "1.5.1"]]}
   :1.6 {:dependencies [[org.clojure/clojure "1.6.0"]]}
   :1.7 {:dependencies [[org.clojure/clojure "1.7.0-alpha1"]]}})
