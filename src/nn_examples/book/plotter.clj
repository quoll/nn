(ns nn-examples.book.plotter
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [libpython-clj2.python :refer [->jvm]])
  (:import [java.net Socket]
           [java.io OutputStreamWriter]))

(defn send-data-to-python [data]
  (with-open [socket (Socket. "localhost" 65432)
              out (OutputStreamWriter. (.getOutputStream socket) "UTF-8")]
    (.write out (json/write-str data))
    (.flush out)))

(defn scatter [x y & {:as m}]
  (let [m (reduce-kv (fn [acc k v] (assoc acc k (->jvm v))) {} m)]
    (send-data-to-python (assoc m :x (->jvm x) :y (->jvm y)))))

