(ns nn-examples.book.chap3
  (:require [nn.np :refer [zip tuple all item nparray np+ np- np* np-div np-fdiv dot T]]
            [tech.v3.tensor :as dtt]
            [libpython-clj2.require :refer [require-python]]
            [libpython-clj2.python :refer [as-python as-jvm ->python ->jvm
                                           get-attr call-attr call-attr-kw
                                           get-item run-simple-string
                                           add-module module-dict import-module
                                           python-type py. py.. py.-] :as py]
            [libpython-clj2.python.np-array]))

;; Adding Layers
;; pg. 61

(def inputs [[1.0 2.0 3.0 2.5]
             [2.0 5.0 -1.0 2.0]
             [-1.5 2.7 3.3 -0.8]])
(def weights [[0.2 0.8 -0.5 1.0]
              [0.5 -0.91 0.26 -0.5]
              [-0.26 -0.27 0.17 0.87]])
(def biases [2.0 3.0 0.5])
(def weights2 [[0.1 -0.14 0.5]
               [-0.5 0.12 -0.33]
               [-0.44 0.73 -0.13]])
(def biases2 [-1.0 2.0 -0.5])

(def layer1_outputs (np+ (dot inputs (T (nparray weights))) biases))
(def layer2_outputs (np+ (dot layer1_outputs (T (nparray weights2))) biases2))

(println layer2_outputs)

;; pg.63
(require-python '[nnfs :as nnfs])
(require-python '[nnfs.datasets :refer [spiral_data]])
(require '[nn-examples.book.plotter :as plt])

(nnfs/init)
(let [[X y] (spiral_data :samples 100 :classes 3)]
;;  (plt/scatter (item X 0) (item X 1))
  (plt/scatter (item X :all 0) (item X :all 1) :c y :cmap "brg"))

(println "== END ==")
