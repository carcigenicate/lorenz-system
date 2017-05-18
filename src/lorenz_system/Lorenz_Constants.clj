(ns lorenz-system.Lorenz-Constants
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io])
  (:import (java.io FileNotFoundException)))

(def save-path "./constant-saves/")

(defrecord Lorenz-Constants [a b c step])

(defn save-path-for [save-name]
  (str save-path save-name ".txt"))

(defn save-constants [constants save-name]
  (doto (save-path-for save-name)
    (io/make-parents)
    (spit (pr-str (into {} constants)))))

(defn load-constants [save-name]
  (try
    (map->Lorenz-Constants
        (clojure.edn/read-string
          (slurp (save-path-for save-name))))

    (catch FileNotFoundException e
      (println "Bad file name!")
      (->Lorenz-Constants 1 1 1 0.01))))
