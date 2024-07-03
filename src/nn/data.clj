(ns nn.data
  (:require [nn.config :as config]
            [tech.v3.tensor :as dtt]
            [libpython-clj2.require :refer [require-python]]
            [libpython-clj2.python :refer [as-python as-jvm ->python ->jvm
                                           get-attr call-attr call-attr-kw
                                           get-item initialize!  run-simple-string
                                           add-module module-dict import-module
                                           python-type py. py.. py.-] :as py]
            [libpython-clj2.python.np-array]))

(config/init! "accel")



