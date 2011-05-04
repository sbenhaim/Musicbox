(ns musicbox.music
  (:use musicbox.stepper
        musicbox.clock
        musicbox.theory
        musicbox.utils
        midi))

(def *midi-out* (atom nil))
(def _ \_)

(defn skip? [x]
  (or (= x \_) (= x '_) (= x "_")))

(defn note-action [note & {:keys [vel dur prob chan] :or {vel 60 dur 100 prob 100 chan 0}}]
  (fn [_] (maybe prob midi-note @*midi-out* chan note vel dur)))

(defn note-name [n]
  (keyword (str "note" n)))

(defn note-transform [num & args]
  (if (skip? num) {}
      {(note-name num) (apply note-action num args)}))

(defn mono-stepper [transform pattern]
  (into [] (map transform pattern)))

(defn simple-stepper [pattern & args]
  (let [transform (if args #(apply note-transform % args)
                      #(note-transform %))]
    (mono-stepper transform pattern)))

;; (simple-stepper [60 61] :chan 1 :vel 10)

(defn poly-stepper [])
(defn beat-stepper [])

(defn step->action [transform data step]
  (let [step (transform step data)]
    (into {} (map (fn [s] {(s :name) (apply note-action (s :note) (s :args))}) step))))

(defn hit-transform [s data]
  (if (skip? s) [{}]
      (vector {:name (note-name (data :note))
               :note (data :note)
               :args [:chan (data :chan)]})))

(defn hit->action [chan note step]
  (step->action hit-transform {:chan chan :note note} step))

(defn beat-stepper [chan m]
  (letfn [(transform [[note stepper]]
            (into [] (map (partial hit->action chan note) stepper)))]
    (into [] (apply map merge (map transform m)))))

(defn rand-beat-stepper
  ([len] (rand-beat-stepper len "x_"))
  ([len seed] (apply str (repeatedly len #(rand-nth seed)))))

;;;;;;;;;

(defn note-transform [s data]
  (if (skip? s) [{}]
      (vector {:name (note-name s)
               :note s
               :args [:chan (data :chan)]})))

(defn note->action [chan step]
  (step->action note-transform {:chan chan} step))

(defn mode-note-transform [s data]
  (if (skip? s) [{}]
      (let [note (note (data :key) (data :mode) s (data :octave))]
        [{:name (note-name note)
          :note note
          :args [:chan (data :chan)]}])))

(defn mode-note->action [chan key mode octave m]
  (step->action mode-note-transform {:chan chan :key key :mode mode :octave octave}))

(defn note-stepper
  ([chan m]
      (into [] (map (partial note->action chan) m)))
  ([chan key mode octave m]
     (into [] (map (fn [s] (if (skip? s) {} (note->action chan [key mode s octave]))) m))))

(defn chord->action [chan step]
  (if (skip? step) {}
      (let [notes (if (every? number? step) step (apply chord step))]
        (apply merge {} (map (partial note->action chan) notes)))))

(defn chord-stepper
  ([chan m]
     (into [] (map (partial chord->action chan) m)))
  ([chan key mode octave m]
     (into [] (map (fn [s] (if (skip? s) {} (chord->action chan [key mode (first s) (second s) octave]))) m))))

(defn add-info-stepper []
  (add-stepper :info :quarter [{:info #(println %)}]))
