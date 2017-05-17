(ns lorenz-system.protocols.lorenz-state)

(defprotocol Lorenz-State
  (add-point [state point])
  (last-point [state])
  (points [state]))