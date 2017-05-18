(ns lorenz-system.main
  (:require [quil.core :as q]
            [quil.middleware :as m]

            [lorenz-system.lorenz-system :as lsy]
            [lorenz-system.control-state :as cs]
            [lorenz-system.color-schemes :as co]
            [lorenz-system.input-handler :as i]

            [helpers.general-helpers :as g]
            [helpers.key-manager :as k]))

; TODO: Make hue-f part of anim-state. Toggle via keys.

(def width 2500)
(def height 1500)

(def time-step 0.01)

(def line-weight 5)

(def start-x 1)
(def start-y 2)
(def start-z 3)

(def a 20) ; 10
(def b 50) ; 28
(def c 1) ; 8/3

; Cool "trumpet": t:0.01 a:20 b:50 c:1

(def rand-gen (g/new-rand-gen 99))

(def hue-f #(co/test1 % %2 %3 (g/random-double 1 1.1 rand-gen) 1.7))

(defrecord Animation-State [lorenz-system control-state key-manager])

(defn default-controls []
  (-> (cs/new-controls)
      (cs/set-rotations 0 0 0)
      (cs/set-scale 20)))

(defn apply-controls
  "Applies the transformations defined by the animation state.
  Does not modify the state. Returns it unchanged so it can be threaded."
  [anim-state]
  (let [{xt :x-translation yt :y-translation
         xr :x-rotation yr :y-rotation zr :z-rotation
         s :scale} (:control-state anim-state)]
    (q/translate xt yt s)

    (q/rotate-x xr)
    (q/rotate-y yr)
    (q/rotate-z zr)

    anim-state))

(defn advance-animation [state step]
  (update state :lorenz-system
          #(lsy/advance-system % step)))

(defn affect-state-with-keys [anim-state]
  (update anim-state :control-state
    #(k/reduce-pressed-keys (:key-manager anim-state)
                            i/key-handler
                            %)))

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

  (->Animation-State (lsy/new-system a b c start-x start-y start-z)
                     (default-controls)
                     (k/new-key-manager)))

(defn update-state [state]
  (when (zero? (rem (q/frame-count) 500))
    (println (-> state :lorenz-system :lorenz-state :points (count))))
  (-> state
      (advance-animation time-step)
      (affect-state-with-keys)))

(defn draw-state [anim-state]
  (let [{ls :lorenz-system} anim-state]

    (q/background 0 0 240)

    (q/with-translation [(/ width 2) (/ height 2)]
      (apply-controls anim-state)

      ; Scaling prevents the "camera" from cutting out the lines early
      (q/scale 3)

      (draw-system ls hue-f))))

(defn key-press-handler [state event]
  (update state :key-manager
          #(k/press-key % (:raw-key event))))

(defn key-release-handler [state]
  (update state :key-manager
          #(k/release-key % (q/raw-key))))

(defn -main []
  (q/defsketch Lorenz-System-Demo
               :size [width height]

               :renderer :p3d

               :setup setup-state
               :update update-state
               :draw draw-state

               :middleware [m/fun-mode]
               :features [:keep-on-top]

               :key-pressed key-press-handler
               :key-released key-release-handler))
