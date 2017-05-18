(ns lorenz-system.input-handler
  (:require [lorenz-system.control-state :as cs]
            [helpers.general-helpers :as g]
            [quil.core :as q]))

(def rotation-speed 0.05)
(def scale-speed 0.3)
(def translation-speed 20)

(def pi (* 4 (Math/atan 1)))
(def two-pi (* pi 2))

(defn wrap-angle [angle]
  (g/unsafe-wrap angle 0 two-pi))

(defn key-handler [control-state key]
  (let [u update
        c control-state
        r rotation-speed
        t translation-speed]

    (case key
      \i (u c :y-translation #(+ % t))
      \k (u c :y-translation #(- % t))

      \j (u c :x-translation #(+ % t))
      \l (u c :x-translation #(- % t))

      \a (u c :y-rotation #(- % r))
      \d (u c :y-rotation #(+ % r))

      \w (u c :x-rotation #(- % r))
      \s(u c :x-rotation #(+ % r))

      \q (u c :z-rotation #(- % r))
      \e (u c :z-rotation #(+ % r))

      \space (-> c
                 (cs/set-rotations 0 0 0)
                 (cs/set-translation 0 0))

      \z (u c :scale #(+ % scale-speed))
      \x (u c :scale #(- % scale-speed))

      control-state)))
