(ns lorenz-system.control-state)

(defrecord Control-State [translations
                          rotations])

(defn new-controls []
  (->Control-State [0 0 0]
                   [0 0 0]))
