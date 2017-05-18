(ns lorenz-system.main
  (:require [quil.core :as q]
            [quil.middleware :as m]

            [lorenz-system.lorenz-system :as lsy]
            [lorenz-system.control-state :as cs]
            [lorenz-system.color-schemes :as co]
            [lorenz-system.input-handler :as i]
            [lorenz-system.Lorenz-Constants :as lc]

            [helpers.general-helpers :as g]
            [helpers.key-manager :as k]))

; TODO: Make hue-f part of anim-state. Toggle via keys.

; Either a string to get a pre-saved set of constants,
;  or a vector as [a b c step]
(def system-type [10 54 0.1 0.01])

(def width 2500)
(def height 1500)

(def line-weight 3)

(def start-x 1)
(def start-y 1)
(def start-z 1)

(declare make-constants)

(def constants (delay (make-constants system-type)))

(def rand-gen (g/new-rand-gen 99))

(def hue-f #(co/test1 % %2 %3 (g/random-double 1 1.05 rand-gen) 1.7))

(defrecord Animation-State [lorenz-system control-state key-manager])

(defn default-controls []
  (cs/new-controls))

(defn make-constants [cons-type]
  (if (string? cons-type)
    (lc/load-constants cons-type)

    (apply lc/->Lorenz-Constants cons-type)))

(defn apply-controls
  "Applies the transformations defined by the controls"
  [controls]
  (let [{[xt yt zt] :translations
         [xr yr zr] :rotations} controls]
    (q/translate xt yt zt)

    (q/rotate-x xr)
    (q/rotate-y yr)
    (q/rotate-z zr)))

(defn advance-animation [state]
  (update state :lorenz-system
          #(lsy/advance-system %)))

(defn affect-state-with-keys [anim-state]
  (update anim-state :control-state
    #(k/reduce-pressed-keys (:key-manager anim-state)
                            i/key-handler
                            %)))

(defmacro try-float-op
  "Convenience macro that handles \"Value out of range\" exceptions by gracefully closing Quil. Otherwise, Quil attempts to catch it, which causes a mess."
  [& body]
  `(try
     ~@body
     (catch IllegalArgumentException e
       (println "Exploded with:" (.getMessage e))
       (q/exit))))

(defn draw-system [lorenz-system hue-f]
  ; This is the function most likely to explode if the numbers get huge suddenly since
  ;  this is the function that deals with Quil the most, which uses floats.
  (try-float-op
    (let [points (lsy/points lorenz-system)]
      (q/begin-shape)
      (doseq [[x y z :as point] points]
        (q/with-stroke (apply hue-f point)
          (q/vertex x y z)))
      (q/end-shape))))

 ; -----

(defn setup-state []
  (q/stroke-weight line-weight)
  (q/no-fill)
  (q/color-mode :hsb)

  (->Animation-State (lsy/new-system @constants start-x start-y start-z)
                     (default-controls)
                     (k/new-key-manager)))

(defn update-state [state]
  (when (zero? (rem (q/frame-count) 500))
    (println (-> state :lorenz-system :state :points (count))))

  (-> state
      (affect-state-with-keys)
      (advance-animation)))


(defn draw-state [anim-state]
  (let [{ls :lorenz-system controls :control-state} anim-state]

    (q/background 0 0 240)

    (q/with-translation [(/ width 2) (/ height 2)]
      (apply-controls controls)

      ; Scaling prevents the "camera" from cutting out the lines early
      (q/scale 10)

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
