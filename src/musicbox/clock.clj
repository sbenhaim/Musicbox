(ns musicbox.clock
  (:require [overtone.time-utils :as time] midi))

(defn stop-and-reset-pool
  [pool now?]
  (let [num-threads (.getCorePoolSize pool)
        new-pool (time/make-pool num-threads)]
    (if now?
      (.shutdownNow pool)
      (.shutdown pool))
    new-pool))


(def clock-pool (atom (time/make-pool)))

(def tick (atom -1))
(def tocks (atom {}))
(def pre-tocks (atom {}))

(defn add-tock [name tock]
  (swap! tocks assoc name tock))

(defn add-pre-tock [name pre-tock]
  (swap! pre-tocks assoc name pre-tock))

(defn remove-tock [name]
  (swap! tocks dissoc name))

(defn add-tocks [tocks]
  (doseq [[name tock] tocks] (add-tock name tock)))

(defn reset-tocks
  ([] (reset! tocks {}))
  ([tocks] (do (reset-tocks)
               (add-tocks tocks))))

(defn tick-tocks [msg]
  (doseq [[_ tock] @tocks]
	(tock msg)))

(defn tick-pre-tocks [msg]
  (doseq [[_ tock] @pre-tocks]
	(tock msg)))

(defn tick-tock []
  (swap! tick inc)
  (let [num @tick
        info (merge {:tick num}
                    (when (= (mod num 96) 0)
                      {:whole (/ num 96)})
                    (when (= (mod num 72) 0)
                      {:d-half (/ num 72)})
                    (when (= (mod num 48) 0)  
                      {:half (/ num 48)})
                    (when (= (mod num 36) 0)
                      {:d-quarter (/ num 36)})
                    (when (= (mod num 24) 0)
                      {:quarter (/ num 24)})
                    (when (= (mod num 12) 0)
                      {:eighth (/ num 12)})
                    (when (= (mod num  8) 0)
                      {:triplet (/ num 8)})
                    (when (= (mod num  6) 0)
                      {:sixteenth (/ num 6)})
                    (when (= (mod num 3) 0)
                      {:thirtysecond (/ num 3)}))]
    (do
      (tick-pre-tocks info)
      (tick-tocks info))))

(defn stop-clock []
  (swap! clock-pool stop-and-reset-pool true))

(defn start-clock [bpm & tocks]
  (stop-clock)
  (let [ms (/ 1000.0 (* 24 (/ bpm 60)))]
	  (time/periodic tick-tock ms 0 @clock-pool))
  (when tocks
	(add-tocks (apply hash-map tocks))))

(defn reset-clock []
  (reset! tick -1))

(defn reset-clock! []
  (reset-clock)
  (reset! tocks {})
  (reset! pre-tocks {})
  (stop-clock))

(defmulti midi-clock-in (fn [msg ts] (:status msg)))
(defmethod midi-clock-in :timing-clock [_ _]
  (tick-tock))
(defmethod midi-clock-in :stop [_ _])
(defmethod midi-clock-in :song-position-pointer [msg _]
  (reset! tick (* 24 (msg :data1))))
(defmethod midi-clock-in nil [_ _])

(defn follow-midi-clock [in & tocks]
  (midi/midi-handle-events in midi-clock-in)
  (when tocks
	(add-tocks (apply hash-map tocks))))
