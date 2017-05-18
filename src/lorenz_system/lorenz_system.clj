(ns lorenz-system.lorenz-system
  (:require [lorenz-system.list-lorenz-state :as lst]
            [lorenz-system.protocols.lorenz-state :as lsP]
            [lorenz-system.Lorenz-Constants :as lc]))

(defrecord Lorenz-System [constants state])

(defn new-system
  ([constants initial-x initial-y initial-z]
   (->Lorenz-System constants
                    (lst/new-state [initial-x initial-y initial-z])))

  ([a b c step initial-x initial-y initial-z]
   (new-system (lc/->Lorenz-Constants a b c step)
               initial-x initial-y initial-z)))

(defn next-point
  ([a b c x y z step]
   (let [; a(y - x)
         dx (* a (- y x))

         ; x(b - z) - y
         dy (- (* x (- b z)) y)

         ; xy - cz
         dz (- (* x y) (* c z))]

     [(+ x (* dx step))
      (+ y (* dy step))
      (+ z (* dz step))]))

  ([lorenz-system]
   (let [{c :constants ls :state} lorenz-system
         {a :a b :b c :c step :step} c
         [x y z] (lsP/last-point ls)]
     (next-point a b c x y z step))))

(defn advance-system [lorenz-system]
  (let [new-point (next-point lorenz-system)]
    (update lorenz-system :state
            #(lsP/add-point % new-point))))

(defn points [lorenz-system]
  (lsP/points (get lorenz-system :state)))
