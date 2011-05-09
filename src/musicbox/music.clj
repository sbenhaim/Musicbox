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

(defn play-note
  ([note] (play-note note nil))
  ([note {:keys [vel dur chan] :or {vel 60 dur 100 chan 0}}]
     (when (< note 128)
       (midi-note @*midi-out* chan note vel dur))))

(defn play-chord
  ([chord] (play-chord chord nil))
  ([chord args]
     (doseq [note chord]
       (play-note note args))))

(defn note-action [note {prob :prob :or {prob 100} :as args}]
  (fn [_] (maybe prob play-note note args)))

(defn parse-pattern [pattern parser]
  (letfn [(parse [s] (if (skip? s) {} (parser s)))]
    (into [] (map parse pattern))))

(defn note-name [n]
  (keyword (str "note" n)))

(defn note-parser [args s]
  (let [n (if (number? s) s (get-note s))]
    {(note-name n) (note-action n args)}))

(defn chord-parser [args c]
  (into {} (map (p note-parser args) c)))

(defn stepper [parser args pattern]
  (parse-pattern pattern (p parser args)))

(defn note-stepper [args pattern]
  (stepper note-parser args pattern))

(defn chord-stepper [args pattern]
  (stepper chord-parser args pattern))

(defn beat-stepper [args pattern]
  (letfn [(transform [[n p]] (map  #(if (skip? %) % n) p))]
    (into [] (apply map merge (map (fn [x] (note-stepper args (transform x))) pattern)))))

(defn rand-beat-pattern
  ([len] (rand-beat-pattern len "x_"))
  ([len seed] (apply str (repeatedly len #(rand-nth seed)))))

(defn mode-note-parser [key mode octave args n]
  (let [n (get-note key mode n octave)]
    (note-parser args n)))

(defn mode-note-stepper [args key mode octave pattern]
  (stepper (p mode-note-parser key mode octave) args pattern))

(defn mode-chord-parser [key mode octave args n]
  (let [c (get-chord key mode n 4 octave)]
    (chord-parser args c)))

(defn mode-chord-stepper [args key mode octave pattern]
  (stepper (p mode-chord-parser key mode octave) args pattern))

(defn arp-note-parser [chrd args step]
  (let [n (nth chrd (mod step (count chrd)))]
    (note-parser args n)))

(defn arp-stepper [args chrd pattern]
  (stepper (p arp-note-parser chrd) args pattern))
