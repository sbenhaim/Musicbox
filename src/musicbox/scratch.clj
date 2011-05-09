(ns musicbox.scratch
  (:use musicbox.stepper
        musicbox.music
        musicbox.clock
        musicbox.theory
        musicbox.utils
        musicbox.song
        musicbox.interactive
        musicbox.touch-osc
        midi))


;; TOCKS
;; 
;; A tock is a function that takes clock info
;; (defn tock [info] (println info))
;; (add-tock :info tock)

;; AND CLOCKS
;; 
;; A clock is a 24/beat callbacker the provides beat info
;; (start-clock 10)
;; (reset-clock!)


;; STEPPERS - HIGHER-ORDER STEP SEQUENCERS
;;
;; A stepper is an array of dictionaries (maps, hash-maps, whatever)
;; Each item in the array index (each dictionary value) is executed simultaneously (or thereabouts)
;; Dictionary keys are for easy removal/replacement of actions
;;
;; Steppers are added with names and resolutions, and create new tocks
;; Stepper names are for easy removal/replacement of steppers
;; (add-stepper :info :quarter [{:info #(println %)}])
;; (start-clock 120)
;; (reset-clock!)


;; MUSIC!
;; (reset! *midi-out* (midi-out "MidiPipe Input 1"))
;; (play-note (get-note :f 5) {:chan 1})
;; (play-chord (get-chord :d :m 4) {:chan 1})


;; NOTE STEPPERS
;; (add-stepper :notes :quarter (note-stepper {:chan 1} [60 62 63 _]))
;; (add-stepper :notes :eighth (note-stepper {:chan 1} '[c4 d4 eb4 f4]))
;; (add-stepper :notes :eighth (note-stepper {:chan 1} (take 8 (get-scale :a :blues 4))))
;; (add-stepper :notes :sixteenth (note-stepper {:chan 1} (repeatedly 20 #(rand-nth (take 16 (get-scale :a :hungarian-minor 4))))))

;; (do
;;   (start-clock 120)
;;   (reset-clock!))

;; CHORD STEPPERS

;; (add-stepper :chord :whole (chord-stepper {:chan 1} [[60 64 67] [62 65 69]]))
;; (add-stepper :chord :whole (chord-stepper {:chan 1} [(get-chord :c :M 5) (get-chord :d :m 5)]))

;; (start-clock 120)
;; (reset-clock!)


;; ARP STEPPERS

;; (add-stepper :scale :eighth (arp-stepper {:chan 1} (get-scale :c :ionian 4 8) (range 8)))
;; (add-stepper :scale :sixteenth (arp-stepper {:chan 1} (get-scale :c :whole-tone 4 20) (rand-nums 32)))

;; (let [root 0]
;;   (add-stepper :scale :quarter (arp-stepper {:chan 1} (get-mode-chord :c :hungarian-minor root 3 4) (rand-nums 16)))
;;   (add-stepper :scale1 :eighth (arp-stepper {:chan 1} (get-mode-chord :c :hungarian-minor root 4 4) (rand-nums 16)))
;;   (add-stepper :scale2 :sixteenth (arp-stepper {:chan 1} (get-mode-chord :c :hungarian-minor root 4 4) (rand-nums 16)))
;;   ;; (remove-stepper :scale)
;;   ;; (remove-stepper :scale1)
;;   ;; (remove-stepper :scale2)
;;   )

;; (reset-clock!)
;; (start-clock 120)


;; BEAT STEPPERS

;; (add-stepper :beats :sixteenth
;;   (beat-stepper 0 {
;;      (get-note :c  3) "x___x___x___x___"
;;      (get-note :c# 3) "__x___x___x___x_"
;;      (get-note :g  5) "xxx_x_x_xxx_x_x_"
;;      (get-note :a  3) (rand-beat-pattern 16 "x_")
;;      }))

;; (add-stepper :keys :sixteenth
;;   (beat-stepper {:chan 1} {
;;     (get-note :c  6) "x___x___x___x___"
;;     (get-note :d  6) "__x___x___x___x_"
;;     (get-note :g  4) "xxx_x_x_xxx_x_x_"
;;     (get-note :a  4) (rand-beat-pattern 16) 
;;     }))

;; (start-clock 120)
;; (reset-clock!)


;; INTERACTIVE STEPPERS
;;

;; (reset! master-chord [:f :m7])

;; (add-stepper :arp :sixteenth
;;              (i-arp-stepper {:chan 1 :octave 4}
;;                             (rand-nums 20 32)))

;; (do
;;   (reset-clock!)
;;   (start-clock 120))


;; FINALLY, A SONG

(def chrds [:chords :whole
             (i-chord-stepper {:chan 1 :octave 4 :vel 30} (rand-nums 5))])

(def arp1 [:arp1 :eighth
             (i-arp-stepper {:chan 1 :octave 3} (rand-nums 16 32))])

(def arp2 [:arp2 :sixteenth
             (i-arp-stepper {:chan 1 :octave 4} (rand-nums 20 32))])

(def beats [:beats :sixteenth
            (beat-stepper {:chan 0}
                          {
                           (get-note :c  3) "x___x___x___x___"
                           (get-note :e  3) "__x___x___x___xx"
                           (get-note :a  4) (rand-beat-pattern 16)
                           })])

(def song
  {
   0 {:chord [:c :m]
      :tempo 100
      :steppers [chrds beats arp1 arp2]}
   4  {:chord [:e :m]}
   8  {:chord [:c :m]}
   12 {:chord [:e :m]}
   16 {:chord [:d :dim7]}
   20 {:chord [:d :dim7]}
   24 {:chord [:f :M]}
   28 {:chord [:a :M]}
   31 {:stop true}
   })


(play-song song)
;; (stop-song)


;; LESS CODE, MOAR BUTTONS

(def my-chords
  [
   [:c :m]
   [:e :m]
   [:d :dim7]
   [:f :M]
   [:a :M]
   ])

(def song
  {
   0 {:chord (my-chords 0)
      :tempo 100
      :steppers [chrds beats arp1 arp2]}})

(defn pick-chord [{on-off :val [[_ num]] :matches}]
  (when (= on-off 1.0)
    (let [num (parse-integer num)]
      (when-let [chrd (get my-chords (dec num))]
        (reset! master-chord chrd))
      (when (= num 15)
        (play-song song))
      (when (= num 16)
        (stop-song)))))

(defn change-tempo [{val :val}]
  (when (pos? val)
    (start-clock (* 200 val))))

(defn do-stuff [{val :val [[_ num]] :matches}]
  (when (= val 1.0)
    (let [num (parse-integer num)]
      (play-note (+ 60 (dec num)) {:chan 1})
      (println num val))))

(reset! triggers
        {
         #"/2/push(.+)" #'do-stuff
          })
