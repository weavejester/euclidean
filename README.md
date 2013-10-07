# Euclidean

[![Build Status](https://travis-ci.org/weavejester/euclidean.png?branch=master)](https://travis-ci.org/weavejester/euclidean)

A Clojure library for performing calculations suitable for 3D games
using fast, immutable data structures.

Euclidean is written in pure Clojure, but has performance comparable
to vector libraries written in Java, such as [Vectorz][1].

[1]: https://github.com/mikera/vectorz-clj

## Installation

Add the following dependency to your `project.clj` file:

    [euclidean "0.2.0"]

## Usage

Euclidean provides two namespaces, one for constructing vectors, the
other for [quaternions][2]:

```clojure
(require '[euclidean.math.vector :as v])
(require '[euclidean.math.quaternion :as q])
```

[2]: https://en.wikipedia.org/wiki/Quaternion

The library includes data readers for 2D and 3D vectors, and 4D
quaternions:

```clojure
#math/vector [1 2]          ;; 2D vector
#math/vector [1 2 3]        ;; 3D vector
#math/quaternion [0 0 0 1]  ;; Quaternion
```

But you can also create data structures through various contructor
functions:

```clojure
(v/vector 1 2)
(v/vector 1 2 3)
(v/into-vector [1 2])
(v/into-vector [1 2 3])

(q/quaternion 0 0 0 1)
(q/into-quaternion [0 0 0 1])
```

For a full list of functions that manipulate and create vectors and
quaternions, see the API docs linked below.

## Documentation

* [API Docs](http://weavejester.github.io/euclidean/)

## License

Copyright © 2013 James Reeves

Distributed under the Eclipse Public License, the same as Clojure.
