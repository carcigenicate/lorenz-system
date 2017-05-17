(ns lorenz-system.first-try
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [helpers.general-helpers :as g]))

(def width 2500)
(def height 1500)

(def rot-step 0.05)

(defn wrap [n min-n max-n]
  (cond
    (< n min-n) (- max-n (- min-n n))
    (> n max-n) (+ min-n (- n max-n))
    :else n))

(defn setup-state []
  {:rot 0})

(defn update-state [state]
  (update state :rot #(wrap (+ % rot-step) 0 q/TWO-PI)))

(defn draw-state [{rot :rot}]
  (q/background 200 200 200)

  (q/with-fill [100 100 100]
    (q/with-translation [(/ width 2) (/ height 2)]

      (q/rotate-z rot)
      (q/rotate-x rot)

      (q/rotate-y q/PI)
      (q/box 500 100 200))))


(defn -main []
  (q/defsketch Lorenz-System
               :size [width height]
               :renderer :opengl
               :setup setup-state
               :update update-state
               :draw draw-state
               :middleware [m/fun-mode]
               :features [:keep-on-top]))
