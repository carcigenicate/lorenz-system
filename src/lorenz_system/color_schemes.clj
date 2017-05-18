(ns lorenz-system.color-schemes
  (:require [helpers.general-helpers :as g]))

(defn- w [c]
  (mod c 255))

(defn scaled-location [x y z scale]
  (mapv w
    [(* x scale)
     (* y scale)
     (* z scale)]))

(defn rainbow [x y z scale]
  [(w (rem (* scale (+ x y z)) 255))
   255 255])

(defn test1 [x y z & scales]
  [(apply * (+ x y z) scales)
   200 255])
