(ns lorenz-system.list-lorenz-state
  (:require [lorenz-system.protocols.lorenz-state :as lsP]))

(defrecord List-Lorenz-State [points])

(defn new-state [starting-point]
  (->List-Lorenz-State [starting-point]))

(defn add-point [state point]
  (update state :points
          #(conj % point)))

(defn points [state]
  (:points state))

(defn last-point [state]
  (last (points state)))

(extend List-Lorenz-State
  lsP/Lorenz-State
  {:add-point add-point
   :last-point last-point
   :points last-point})
