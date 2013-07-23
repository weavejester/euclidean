(defproject euclidean "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]]
  :plugins [[codox "0.6.4"]]
  :profiles
  {:dev {:dependencies [[criterium "0.4.1"]]}
   :1.5 {:dependencies [[org.clojure/clojure "1.5.1"]]}})
