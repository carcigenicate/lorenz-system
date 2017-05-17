(ns lorenz-system.main
  (:require [quil.core :as q]
            [quil.middleware :as m]

            [lorenz-system.lorenz-system :as lsy]
            [lorenz-system.control-state :as cs]

            [helpers.general-helpers :as g]))

(def width 2500)
(def height 1500)

(def time-step 0.01)
(def rotation-step 0.01)

(def line-weight 0.5)

(def start-x 1)
(def start-y 2)
(def start-z 3)

(def a 10) ; 10
(def b 5) ; 28
(def c 0.1) ; 8/3

(def rand-gen (g/new-rand-gen 99))

(defrecord Animation-State [lorenz-system control-state])

(defn apply-controls [anim-state step]
  (let [{xr :x-rotation yr :y-rotation zr :z-rotation s :scale} (:control-state anim-state)]
    (q/rotate-x xr)
    (q/rotate-y yr)
    (q/rotate-z zr)

    (q/scale s)))

(defn advance-animation [state step]
  (update state :lorenz-system
          #(lsy/advance-system % step)))

(defn wrap-color [hue]
  (g/unsafe-wrap hue 0 255))

(defn draw-system [lorenz-system hue-f]
  (let [points (lsy/points lorenz-system)]
    (q/begin-shape)
    (doseq [[x y z :as point] points]
      (q/with-stroke (apply hue-f point)
        (q/vertex x y z)))
    (q/end-shape)))

(defn setup-state []
  (q/stroke-weight line-weight)
  (q/no-fill)
  (q/color-mode :hsb)

  ; TODO: Pass a new Lorenz-System and Control-State once written

  (->Animation-State (lsy/new-system a b c start-x start-y start-z)
                     (cs/new-controls)))

(defn update-state [state]
  (when (zero? (rem (q/frame-count) 100))
    (println (-> state :lorenz-system :lorenz-state :points (count)) (q/frame-count)))

  (-> state
      (advance-animation time-step)))

(defn draw-state [anim-state]
  (let [{ls :lorenz-system cs :control-state} anim-state
        w #(g/unsafe-wrap % 0 255)
        h #(rem (+ (* % 3) (* %2 2) (* %3 1)) 255)]
    (q/background 0 100 100)

    (q/with-translation [(/ width 2) (/ height 2)]
      (draw-system ls h))))

(defn -main []
  (q/defsketch Lorenz-System-Demo
               :size [width height]

               :renderer :p3d

               :setup setup-state
               :update update-state
               :draw draw-state

               :middleware [m/fun-mode]
               :features [:keep-on-top]))