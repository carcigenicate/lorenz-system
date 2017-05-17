(ns lorenz-system.control-state)

(defrecord Control-State [x-rotation y-rotation z-rotation scale])

; TODO: Default scale? 0? 1?
(defn new-controls []
  (->Control-State 0 0 0 1))

(defn set-rotations [state x y z]
  (assoc state :x-rotation x
               :y-rotation y
               :z-rotation z))

(defn set-scale [state scale]
  (assoc state :scale scale))