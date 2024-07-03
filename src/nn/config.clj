(ns nn.config
  (:require [libpython-clj2.python :as py]
            [clojure.string :as s]
            [clojure.java.io :as io]))

(def QUOLL_DEFAULT "accel")

(defn get-default-conda
  []
  (let [conda-env (or (System/getProperty "CONDA_ENV") QUOLL_DEFAULT)]
    (when (not= "-" conda-env)
      conda-env)))

(defn find-file
  [dir test-fn]
  (first (.listFiles
           dir
           (reify java.io.FilenameFilter
            (accept [this d name]
             (boolean (test-fn d name)))))))

(defn get-subdir
  [dir name]
  (first (.listFiles
           dir
           (reify java.io.FileFilter
            (accept [this f]
              (and (.isDirectory f)
                   (= name (.getName f))))))))

(defn get-paths
  ([] (get-paths nil))
  ([env]
   (let [path (some-> (System/getenv)
                (get "PATH")
                (s/split #":")
                (#(map io/file %)))
         find-conda-python (fn [dir name] (and (s/includes? (.getPath dir) "conda")
                                               (= name "python3")))
         find-python (fn [dir name] (= name "python3"))]
     (if env
       (if-let [conda-exec (some #(find-file % find-conda-python) path)]
         (let [conda-root (some-> conda-exec
                                  (.getParentFile)
                                  (.getParentFile))
               env-dir (if (= env (.getName conda-root))  ;; running in requested environment
                          conda-root
                          (or 
                           (some-> conda-root             ;; running in base environment
                                   (get-subdir "envs")
                                   (get-subdir env))
                           (let [root-parent (.getParentFile conda-root)] ;; running in a different environment
                             (if (= "envs" (.getName root-parent))
                               (get-subdir root-parent env)))))]
           (if env-dir
             (let [bin (get-subdir env-dir "bin")
                   lib (get-subdir env-dir "lib")
                   python (some-> bin (find-file find-python))
                   pythonlib (some-> lib (find-file (fn [_ name] (re-find #"^libpython.*\.(dylib|so)$" name))))]
               (when-not python
                 (throw (ex-info (str "Unable to find Python executable in environment: " env)
                                 {:environment-dir env-dir
                                  :bin-dir bin})))
               (when-not pythonlib
                 (throw (ex-info (str "Unable to find Python library in environment: " env)
                                 {:environment-dir env-dir
                                  :lib-dir lib})))
               {:python-executable (.getCanonicalPath python)
                :library-path (.getCanonicalPath pythonlib)})
             (throw (ex-info (str "conda environment '" env "' not found") {:conda (.getPath conda-root)}))))
         (throw (ex-info (str "No conda found for requested environment: " env) {:env env})))
       ;; no conda
       (if-let [python (some #(find-file % find-python) path)]
         (let [lib (some-> (.getCanonicalFile python)
                           (.getParentFile)
                           (.getParentFile)
                           (get-subdir "lib"))
               pythonlib (some-> lib (find-file (fn [_ name] (re-find #"^libpython.*\.(dylib|so)$" name))))]
               (when-not pythonlib
                 (throw (ex-info "Unable to find Python library" {:python-executable python
                                                                  :lib-dir lib})))
               {:python-executable (.getCanonicalPath python)
                :library-path (.getCanonicalPath pythonlib)})
         (throw (ex-info "No Python3 executable found" {:path (get (System/getenv) "PATH")})))))))

(defn init!
  ([] (init! (get-default-conda)))
  ([env]
   (let [paths (get-paths env)]
     (py/initialize! paths))))



