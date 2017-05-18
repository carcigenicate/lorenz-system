(ns lorenz-system.control-state)

(defrecord Control-State [x-translation y-translation
                          x-rotation y-rotation z-rotation
                          scale])

(defn new-controls []
  (->Control-State 0 0
                   0 0 0
                   1))

(defn set-translation [state x y]
  (assoc state :x-translation x
               :y-translation y))

(defn set-rotations [state x y z]
  (assoc state :x-rotation x
               :y-rotation y
               :z-rotation z))

(defn set-scale [state scale]
  (assoc state :scale scale))