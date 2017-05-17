(ns lorenz-system.set-lorenz-state
  (:require [lorenz-system.protocols.lorenz-state :as lsP]))

; TODO: Failed since order matters. Need a LinkedHashSet
(defrecord Set-Lorenz-State [points last-point])

(declare add-point)

(defn new-state
  ([starting-point]
   (-> (->Set-Lorenz-State #{} nil)
       (add-point starting-point))))

(defn add-point [state point]
  (-> state
      (update :points #(conj % point))
      (assoc :last-point point)))

(defn last-point [state]
  (:last-point state))

(defn points [state]
  (seq (:points state)))

(extend Set-Lorenz-State
  lsP/Lorenz-State
  {:add-point add-point
   :last-point last-point
   :points points})