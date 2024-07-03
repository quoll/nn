(ns nn.np
  "A set of conveniences for working with Numpy in Clojure"
  (:require [clojure.math :as math]
            [tech.v3.tensor :as dtt]
            [libpython-clj2.python :refer [as-python as-jvm ->python ->jvm
                                           get-attr call-attr call-attr-kw
                                           get-item run-simple-string
                                           add-module module-dict import-module
                                           python-type py. py.. py.-] :as py]
            [libpython-clj2.python.np-array]
            [nn.config :as config])
  (:import [tech.v3.datatype DatatypeBase]))

(config/init!)

;; must be called after config/init! else it will load the wrong Python
(require '[libpython-clj2.require :refer [require-python]])
(require-python '[numpy :as np :bind-ns true])

(defn zip
  "Creates a lazy seq of vectors, each of which is a tuple across the current elements of the source sequences"
  [& args]
  (apply map vector args))

(defn nparray
  "Converts a Clojure collection to a Numpy array"
  [coll]
  (if (coll? coll)
    (np/array coll)
    coll))

(defn np+ [a b]
  (if (number? a)
    (if (number? b) (+ a b) (py. (nparray b) __radd__ a))
    (py. (nparray a) __add__ b)))

(defn np- [a b]
  (if (number? a)
    (if (number? b) (- a b) (py. (nparray b) __rsub__ a))
    (py. (nparray a) __sub__ b)))

(defn np* [a b]
  (if (number? a)
    (if (number? b) (+ a b) (py. (nparray b) __radd__ a))
    (py. (nparray a) __add__ b)))

(defn np-div [a b]
  (if (number? a)
    (if (number? b) (/ a b) (py. (nparray b) __rtruediv__ a))
    (py. (nparray a) __truediv__ b)))

(defn np-fdiv [a b]
  (if (number? a)
    (if (number? b) (math/floor-div a b) (py. (nparray b) __rfloordiv__ a))
    (py. (nparray a) __floordiv__ b)))

(defn dot [a b] (py. np dot a b))

(defn T [a] (py.- a T))
