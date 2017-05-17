(ns lorenz-system.lorenz-system
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [helpers.general-helpers :as g]))

(def width 2500)
(def height 1500)

(def time-step 0.01)
(def rotation-step 0.01)

(def start-x 1)
(def start-y 2)
(def start-z 3)

(def a 10) ; 10
(def b 28) ; 28
(def c 8/3) ; 8/3

(def line-weight 0.5)

(def rand-gen (g/new-rand-gen 99))

(defn wrap [n min-n max-n]
  (cond
    (< n min-n) (- max-n (- min-n n))
    (> n max-n) (+ min-n (- n max-n))
    :else n))

(defn new-pos [x y z step]
  (let [; a(y - x)
        dx (* a (- y x))

        ; x(b - z) - y
        dy (- (* x (- b z)) y)

        ; xy - cz
        dz (- (* x y) (* c z))]

    [(+ x (* dx step))
     (+ y (* dy step))
     (+ z (* dz step))]))

(defn add-new-pos [state step]
  (let [points (:points state)
        [x y z] (last points)
        new-point (new-pos x y z step)]
    (update state :points #(conj % new-point))))

(defn advance-rotation [state step]
  (update state :rotation #(wrap (+ % step) 0 q/TWO-PI)))

(defn setup-state []
  (q/stroke-weight line-weight)
  (q/no-fill)
  (q/color-mode :hsb)

  {:points [[start-x start-y start-z]]
   :rotation 0})

(defn update-state [state]
  (when (zero? (rem (q/frame-count) 100))
    (println (-> state :points (count))))

  (-> state
      (add-new-pos time-step)
      (advance-rotation rotation-step)))

(defn draw-state [{points :points rotation :rotation}]
  (let [w #(wrap % 0 255)
        h #(rem (+ (* % 3) (* %2 2) (* %3 1)) 255)]
    (q/background 0 100 100)

    (q/with-translation [(/ width 2) (/ height 2)]
      (q/scale 8)
      (q/rotate-y rotation)

      (q/begin-shape)
      (doseq [[x y z :as point] points]
        (q/with-stroke (mapv #(w (* 10 %)) point)
          (q/vertex x y z)))
      (q/end-shape))))

(defn -main []
  (q/defsketch Lorenz-System
               :size [width height]

               :renderer :p3d

               :setup setup-state
               :update update-state
               :draw draw-state

               :middleware [m/fun-mode]
               :features [:keep-on-top]))
