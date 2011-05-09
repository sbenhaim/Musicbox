(ns musicbox.theory
  (:use musicbox.utils))

(def modes   (let [ionian-sequence [2 2 1 2 2 2]
                   ionian-len (count ionian-sequence)
                   rotate-ionian (fn [offset] (drop offset (take (+ ionian-len offset) (cycle ionian-sequence))))]

               {:diatonic          ionian-sequence
                :ionian            (rotate-ionian 0)
                :dorian            (rotate-ionian 1)
                :phrygian          (rotate-ionian 2)
                :lydian            (rotate-ionian 3)
                :mixolydian        (rotate-ionian 4)
                :aeolian           (rotate-ionian 5)
                :lochrian          (rotate-ionian 6)
                :pentatonic        [2 3 2 2]
                :major-pentatonic  [2 2 3 2]
                :minor-pentatonic  [3 2 2 3]
                :blues             [3 2 1 1 3]
                :harmonic-blues    [3 2 1 1 3 1]
                :whole-tone        [2 2 2 2 2]
                :chromatic         [1 1 1 1 1 1 1 1 1 1 1]
                :harmonic-minor    [2 1 2 2 1 3]
                :melodic-minor-asc [2 1 2 2 2 2]
                :hungarian-minor   [2 1 3 1 1 3]
                :octatonic         [2 1 2 1 2 1 2]
                :messiaen1         [2 2 2 2 2]
                :messiaen2         [1 2 1 2 1 2 1]
                :messiaen3         [2 1 1 2 1 1 2 1]
                :messiaen4         [1 1 3 1 1 1 3]
                :messiaen5         [1 4 1 1 4]
                :messiaen6         [2 2 1 1 2 2 1]
                :messiaen7         [1 1 1 2 1 1 1 1 2]}))

(defn get-mode [name]
  (reduce #(conj %1 (+ (last %1) %2)) [0] (modes name)))


(def notes
  {:c  0
   :c# 1
   :db 1
   :d  2
   :d# 3
   :eb 3
   :e  4
   :e# 5
   :fb 4
   :f  5
   :f# 6
   :gb 6
   :g  7
   :ab 8
   :a  9
   :bb 10
   :b  11
   :b# 12
   :cb 11 })


(def chords
  {:M     [0 4 7]
   :m     [0 3 7]
   :dim   [0 3 6]
   :aug   [0 3 8]
   :M7    [0 4 7 11]
   :7     [0 4 7 10]
   :m7    [0 3 7 10]
   :m7b5  [0 3 6 10]
   :dim7  [0 3 6 9]
   :sus4  [0 5 7]
   :sus2  [0 2 7]
   :7sus4 [0 2 7 10]
   :5     [0 7]
   :5add9 [0 7 14]
   :6     [0 4 7 9]
   :m6    [0 3 7 9]
   :69    [0 4 7 9 14]
   :m69   [0 3 7 9 14]
   :add9  [0 4 7 14]
   :madd9 [0 3 7 14]
   :M9    [0 4 7 11 14]
   :mM9   [0 3 7 11 14]
   :9     [0 4 7 10 14]
   :m9    [0 3 7 14]
   :M7#11 [0 4 7 11 18]
   :11    [0 4 7 10 14 17]
   :m11   [0 3 7 10 17]
   :13    [0 4 7 9 10 14] ;21
   :M13   [0 4 7 9 11 14] ;21
   :7b5   [0 4 6 10]
   :M7b5  [0 4 6 11]
   :7#5   [0 4 8 10]
   :aug7  [0 4 8 10]
   :m7#5  [0 3 8 10]
   :7#9   [0 4 7 10 15]
   :7b9   [0 4 7 10 13]
   :9#11  [0 7 10 14 18]
   :mM7   [0 3 7 11]
   :augM7 [0 4 8 11]
   :b5    [0 4 6]
   :m#5   [0 3 8]})


(defn get-scale
  ([key mode] (get-scale key mode 0))
  ([key mode octave num] (take num (get-scale key mode octave)))
  ([key mode octave]
     (for [o (iterate inc octave) n (get-mode mode)] 
       (+ n (* 12 o) (notes key)))))


(defn get-note
  ([sym] (let [[[_ n octave]] (re-seq #"(\D{1,2})(\d+)" (str sym))]
           (get-note (keyword n) (parse-integer octave))))
  ([name octave] (+ (notes name) (* 12 octave)))
  ([key mode number] (get-note key mode number 0))
  ([key mode number octave] (nth (get-scale key mode octave) number)))


(defn get-chord
  ([key chord] (get-chord key chord 0))
  ([key chord octave]
     (let [transp (+ (* 12 octave) (notes key))]
       (map (p + transp) (chords chord)))))

(defn get-chord*
  ([key chord] (get-chord* key chord 0))
  ([key chord octave] (for [o (iterate inc octave) n (get-chord key chord 0)]
                        (+ (* 12 o) n))))

(defn get-mode-chord
  ([key mode root] (get-chord* key mode root 0))
  ([key mode root octave notes] (take notes (get-mode-chord key mode root octave)))
  ([key mode root octave] (keep-indexed
                           (fn [i n] (when (even? i) n))
                           (drop root (get-scale key mode octave)))))
