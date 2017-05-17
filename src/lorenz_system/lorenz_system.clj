(ns lorenz-system.lorenz-system
  (:require [lorenz-system.list-lorenz-state :as lst]
            [lorenz-system.protocols.lorenz-state :as lsP]))

(defrecord Lorenz-System [a b c lorenz-state])

(defn new-system [a b c initial-x initial-y initial-z]
  (->Lorenz-System a b c
                   (lst/new-state [initial-x initial-y initial-z])))

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

  ([lorenz-system step]
   (let [{a :a b :b c :c ls :lorenz-state} lorenz-system
         [x y z] (lsP/last-point ls)]
     (next-point a b c x y z step))))

(defn advance-system [lorenz-system step]
  (let [new-point (next-point lorenz-system step)]
    (update lorenz-system :lorenz-state
            #(lsP/add-point % new-point))))

(defn points [lorenz-system]
  (get-in lorenz-system [:lorenz-state :points]))
