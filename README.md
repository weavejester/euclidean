# Euclidean

[![Build Status](https://travis-ci.org/weavejester/euclidean.png?branch=master)](https://travis-ci.org/weavejester/euclidean)

A Clojure library for performing calculations suitable for 3D games
using fast, immutable data structures.

## Installation

Add the following dependency to your `project.clj` file:

    [euclidean "0.1.4"]

## Example

```clojure
(require '[euclidean.math.vector :as v])
(require '[euclidean.math.quaternion :as q])

(q/rotate (v/vector 0 0 1) (q/yaw Math/PI))
```

## Documentation

* [API Docs](http://weavejester.github.io/euclidean/)

## License

Copyright © 2013 James Reeves

Distributed under the Eclipse Public License, the same as Clojure.
