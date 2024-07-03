(ns nn-examples.book.chap2
  (:require [nn.np :refer [zip nparray np+ np- np* np-div np-fdiv dot T]]
            [tech.v3.tensor :as dtt]
            [libpython-clj2.require :refer [require-python]]
            [libpython-clj2.python :refer [as-python as-jvm ->python ->jvm
                                           get-attr call-attr call-attr-kw
                                           get-item run-simple-string
                                           add-module module-dict import-module
                                           python-type py. py.. py.-] :as py]
            [libpython-clj2.python.np-array]))

;; Some initial setup
(def main-globals (-> (add-module "__main__")
                      (module-dict)))
(def builtins (import-module "builtins"))

;; A Single Neuron
;; pg. 26

(def inputs (py/->py-list [1 2 3]))
(def weights (py/->py-list [0.2 0.8 -0.5]))
(def bias 2)

(def output (+ (* (get-item inputs 0) (get-item weights 0))
               (* (get-item inputs 1) (get-item weights 1))
               (* (get-item inputs 2) (get-item weights 2)) bias))

(println output)

(comment
(run-simple-string (str "output = (inputs[0]*weights[0] + inputs[1]*weights[1] + inputs[2]*weights[2] + bias)\n"
                        "print(output)"))
)

(def inputs (py/->py-list [1.0 2.0 3.0 2.5]))
(def weights (py/->py-list [0.2 0.8 -0.5 1.0]))
(def bias 2.0)

(def output (+ (* (get-item inputs 0) (get-item weights 0))
               (* (get-item inputs 1) (get-item weights 1))
               (* (get-item inputs 2) (get-item weights 2))
               (* (get-item inputs 3) (get-item weights 3)) bias))

(println output)

;; A Layer of Neurons
;; pg. 31

(def inputs (py/->py-list [1.0 2.0 3.0 2.5]))
(def weights1 (py/->py-list [0.2 0.8 -0.5 1.0]))
(def weights2 (py/->py-list [0.5 -0.91 0.26 -0.5]))
(def weights3 (py/->py-list [-0.26 -0.27 0.17 0.87]))

(def bias1 2.0)
(def bias2 3.0)
(def bias3 0.5)

(def outputs (py/->py-list
              [(+ (* (get-item inputs 0) (get-item weights1 0))
                  (* (get-item inputs 1) (get-item weights1 1))
                  (* (get-item inputs 2) (get-item weights1 2))
                  (* (get-item inputs 3) (get-item weights1 3)) bias1)
               (+ (* (get-item inputs 0) (get-item weights2 0))
                  (* (get-item inputs 1) (get-item weights2 1))
                  (* (get-item inputs 2) (get-item weights2 2))
                  (* (get-item inputs 3) (get-item weights2 3)) bias2)
               (+ (* (get-item inputs 0) (get-item weights3 0))
                  (* (get-item inputs 1) (get-item weights3 1))
                  (* (get-item inputs 2) (get-item weights3 2))
                  (* (get-item inputs 3) (get-item weights3 3)) bias3)]))

(println outputs)

;; pg. 33

(def inputs (py/->py-list [1.0 2.0 3.0 2.5]))
(def weights (py/->py-list [(py/->py-list [0.2 0.8 -0.5 1.0])
                            (py/->py-list [0.5 -0.91 0.26 -0.5])
                            (py/->py-list [-0.26 -0.27 0.17 0.87])]))
(def biases (py/->py-list [2.0 3.0 0.5]))

;; Using Python
(def layer_outputs (py/->py-list []))

(doseq [pair (py. builtins "zip" weights biases)
        :let [neuron_weights (get-item pair 0)
              neuron_bias (get-item pair 1)
              neuron_output (volatile! 0)]]
  (doseq [iw (py. builtins "zip" inputs neuron_weights)
          :let [n_input (get-item iw 0) weight (get-item iw 1)]]
    (vswap! neuron_output + (* n_input weight)))
  (vswap! neuron_output + neuron_bias)
  (py. layer_outputs append @neuron_output))

(println layer_outputs)

;;Using Clojure
(def layer_outputs (py/->py-list []))
(doseq [[neuron_weights neuron_bias] (zip weights biases)]
  (let [neuron_output (reduce (fn [neuron_output [n_input weight]] (+ neuron_output (* n_input weight)))
                              0 (zipmap inputs neuron_weights))
        neuron_output (+ neuron_output neuron_bias)]
    (py. layer_outputs append neuron_output)))

(println layer_outputs)

;; A Single Neuron with NumPy
;; pg. 40

(def inputs [1.0 2.0 3.0 2.5])
(def weights [0.2 0.8 -0.5 1.0])
(def bias 2.0)

(def outputs (np+ (dot weights inputs) bias))

(println outputs)

;; A Layer of Neurons with NumPy
;; pg. 42

(def inputs [1.0 2.0 3.0 2.5])
(def weights [[0.2 0.8 -0.5 1.0]
              [0.5 -0.91 0.26 -0.5]
              [-0.26 -0.27 0.17 0.87]])
(def biases [2.0 3.0 0.5])

(def layer_outputs (np+ (dot weights inputs) bias))

(println layer_outputs)


;; A Layer of Neurons & Batch of Data w/ NumPy
;; pg. 58

(def inputs [[1.0 2.0 3.0 2.5]
             [2.0 5.0 -1.0 2.0]
             [-1.5 2.7 3.3 -0.8]])
(def weights [[0.2 0.8 -0.5 1.0]
              [0.5 -0.91 0.26 -0.5]
              [-0.26 -0.27 0.17 0.87]])
(def biases [2.0 3.0 0.5])

(def layer_outputs (np+ (dot inputs (T (nparray weights))) biases))

(println layer_outputs)

(println "== END ==")
