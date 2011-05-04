(ns musicbox.scratch
  (:use musicbox.stepper
        musicbox.music
        musicbox.clock
        musicbox.theory
        musicbox.utils
        midi))

(reset! *midi-out* (midi-out "MidiPipe Input 1"))

(do
  (reset-clock!)
  (start-clock 100))

(add-info-stepper)
(remove-stepper :synth)
(remove-stepper :synth2)
(add-stepper :synth3 :sixteenth (note-stepper 1 (repeatedly 16 #(rand-nth (take 16 (scale :c :ionian 5)))))))

;; (add-pattern :notes (note-pattern 0 [60 62 _ _ 40 _ _ _ 60 _ _ _ 40 _ _ _ _]))
;; (remove-pattern :notes)
;; (add-pattern :chords (chord-pattern 0 [[60 62 64] _ _ _ [60 64 67] _ _ _]))
;; (remove-pattern :chords)
;; (add-pattern :chords (chord-pattern 0 [[:a :M 5]]))
;; (add-pattern(chord-pattern 0 :a :aeolian 5 [[0 3] [1 4]])
;; (add-pattern(chord :c :M 5)

;; ;; todo velocity
;; (add-pattern :beats
;;   (beat-pattern 0 {
;;      (note :c  4) "x___x___x___x___"
;;      (note :c# 3) "__x___x___x___x_"
;;      ;; (note :g  5) "xxx_x_x_xxx_x_x_"
;;      (note :a  3) (rand-beat-pattern 16 "x_____")
;;      }))

;; (add-pattern :keys
;;   (beat-pattern 2 {
;;     (note :c  6) "x___x___x___x___"
;;     (note :d  6) "__x___x___x___x_"
;;     (note :g  4) "xxx_x_x_xxx_x_x_"
;;     (note :a  4) (rand-beat-pattern 16) 
;;     }))


;; (start-clock 120)
;; (add-pattern :test [{}])
;; (stop-clock)
;; (reset-clock!)

;; (tick)
