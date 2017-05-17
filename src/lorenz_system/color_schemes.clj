(ns lorenz-system.color-schemes
  (:require [helpers.general-helpers :as g]))

(defn- w [c]
  (g/unsafe-wrap c 0 255))

(defn scaled-location [x y z scale]
  (mapv w
    [(* x scale)
     (* y scale)
     (* z scale)]))