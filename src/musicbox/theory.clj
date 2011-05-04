(ns musicbox.theory)

(def modes
  {:ionian     [0, 2, 4, 5, 7, 9, 11]
   :dorian     [0, 2, 3, 5, 7, 9, 10]
   :phrygian   [0, 1, 3, 5, 7, 8, 10]
   :lydian     [0, 2, 4, 6, 7, 9, 11]
   :mixolydian [0, 2, 4, 5, 7, 9, 10]
   :aeolian    [0, 2, 3, 5, 7, 8, 10]
   :locrian    [0, 1, 3, 5, 6, 8, 10] })

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
  {:M     [4 7]
   :m     [3 7]
   :dim   [3 6]
   :aug   [3 8]
   :M7    [4 7 11]
   :7     [4 7 10]
   :m7    [3 7 10]
   :m7b5  [3 6 10]
   :dim7  [3 6 9]
   :sus4  [5 7]
   :sus2  [2 7]
   :7sus4 [2 7 10]
   :5     [7]
   :5add9 [7 14]
   :6     [4 7 9]
   :m6    [3 7 9]
   :69    [4 7 9 14]
   :m69   [3 7 9 14]
   :add9  [4 7 14]
   :madd9 [3 7 14]
   :M9    [4 7 11 14]
   :mM9   [3 7 11 14]
   :9     [4 7 10 14]
   :m9    [3 7 14]
   :M7#11 [4 7 11 18]
   :11    [4 7 10 14 17]
   :m11   [3 7 10 17]
   :13    [4 7 9 10 14] ;21
   :M13   [4 7 9 11 14] ;21
   :7b5   [4 6 10]
   :M7b5  [4 6 11]
   :7#5   [4 8 10]
   :aug7  [4 8 10]
   :m7#5  [3 8 10]
   :7#9   [4 7 10 15]
   :7b9   [4 7 10 13]
   :9#11  [7 10 14 18]
   :mM7   [3 7 11]
   :augM7 [4 8 11]
   :b5    [4 6]
   :m#5   [3 8]})


(defn parse-integer [str]
    (try (Integer/parseInt str) 
         (catch NumberFormatException nfe 0)))

(defn scale [key mode octave]
  (for [o (iterate inc 0) n (modes mode)] 
	(+ n (* 12 (+ octave o)) (notes key))))

(defn note
  ([sym] (let [[[_ n octave]] (re-seq #"(\D{1,2})(\d+)" (str sym))]
           (note (keyword n) (parse-integer octave))))
  ([name octave] (+ (notes name) (* 12 octave)))
  ([key mode number octave] (+ (* octave 12) (nth (scale key mode) number))))


(defn chord
  ([key chord octave]
	 (let [transp (+ (* 12 octave) (notes key))]
	   (conj
		(map #(+ % transp ) (chords chord))
		transp)))
  ([key mode root notes octave]
	 (let [scale (scale key mode)
		   dropper (+ (* 7 octave) (dec root))
		   scale (drop dropper scale)
		   ns (map (partial * 2) (range notes))]
	   (map (partial nth scale) ns))))
