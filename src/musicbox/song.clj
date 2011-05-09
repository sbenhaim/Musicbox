(ns musicbox.song
  (:use musicbox.stepper
        musicbox.clock
        musicbox.theory
        musicbox.utils
        musicbox.music
        musicbox.interactive
        midi))

(def stop-song reset-clock!)

(defn song-change [song bar]
  (when-let [bar-def (song bar)]
    (println bar)
    (when (bar-def :stop) (stop-song))
    (when-let [b (bar-def :before)] (b))
    (when-let [c (bar-def :chord)] (reset! master-chord c))
    (when-let [k (bar-def :key)] (reset! master-key k))
    (when-let [m (bar-def :mode)] (reset! master-mode m))
    (when-let [r (bar-def :root)] (reset! master-root r))
    (when-let [s (bar-def :steppers)] (add-steppers s true))
    (when-let [a (bar-def :after)] (a))
    (when-let [t (bar-def :tempo)] (start-clock t))
    ))

(defn play-song [song]
  (do
    (reset-clock!)
    (add-stepper :song :quarter [{:song-change #(song-change song (% :num))}] true)
    (add-info-stepper)
    (song-change song 0)
    ))

;; (def song
;;   {1  {:key :a
;;        :tempo 120
;;        :steppers #{:one :two}}
;;    4  {:steppers #{:three :four}}
;;    7  (song 1)
;;    10 {:steppers #{:five :six}}})
