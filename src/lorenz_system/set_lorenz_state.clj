(ns lorenz-system.set-lorenz-state)

(defrecord Set-Lorenz-State [points last-point])

(defn new-state
  ([starting-point]
   (->Set-Lorenz-State #{} starting-point))

  ([]
   (new-state nil)))

(defn add-point [state point]
  (-> state
      (update :points #(conj % point))
      (assoc :last-point point)))

(defn last-point [state]
  (:last-position state))