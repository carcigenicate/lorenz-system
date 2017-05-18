(ns lorenz-system.input-handler
  (:require [lorenz-system.control-state :as cs]
            [helpers.general-helpers :as g]
            [quil.core :as q]))

(def rotation-speed 0.05)
(def scale-speed 8)
(def translation-speed 8)

(def pi (* 4 (Math/atan 1)))
(def two-pi (* pi 2))

(defn wrap-angle [angle]
  (g/unsafe-wrap angle 0 two-pi))

(defn key-handler [control-state key]
  (let [u update
        c control-state
        r rotation-speed
        t translation-speed

        ; Shortcuts for updating certain dimensions. 0 = x, 1 = y, 2 = z
        ut #(update-in c [:translations %] %2)
        ur #(update-in c [:rotations %] %2)]

    (case key
      \j (ut 0 #(+ % t))
      \l (ut 0 #(- % t))

      \i (ut 1 #(+ % t))
      \k (ut 1 #(- % t))

      \z (ut 2 #(+ % scale-speed))
      \x (ut 2 #(- % scale-speed))

      \w (ur 0 #(- % r))
      \s (ur 0 #(+ % r))

      \a (ur 1 #(- % r))
      \d (ur 1 #(+ % r))

      \q (ur 2 #(- % r))
      \e (ur 2 #(+ % r))

      \space (-> c
                 (assoc :translations [0 0 0])
                 (assoc :rotations [0 0 0]))

      control-state)))
