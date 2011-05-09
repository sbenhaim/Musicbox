(ns musicbox.interactive
  (:use musicbox.stepper
        musicbox.clock
        musicbox.theory
        musicbox.utils
        musicbox.music
        midi))

(def master-chord (atom nil))
(def master-key (atom nil))
(def master-mode (atom nil))
(def master-root (atom nil))

(defn i-arp-action [n {octave :octave :as args}]
     (fn [_]
       (let [note (nth (apply get-chord* (conj @master-chord octave)) n)]
         (play-note note args))))

(defn i-arp-stepper [args pattern]
  (stepper (fn [args s] {:foo (i-arp-action s args)}) args pattern))

(defn i-mode-arp-action [n {octave :octave :as args}]
  (fn [_]
    (let [note (nth (get-mode-chord @master-key @master-mode @master-root octave) n)]
      (play-note note args))))

(defn i-mode-arp-stepper [args pattern]
  (stepper (fn [args s] {:foo (i-mode-arp-action s args)})
           args pattern))

(defn i-chord-action [n {octave :octave :as args}]
  (fn [_]
    (let [c (apply get-chord (conj @master-chord octave))]
      (play-chord c args))))

(defn i-chord-stepper [args pattern]
  (stepper (fn [args s] {:foo (i-chord-action s args)}) args pattern))

(defn i-mode-chord-action [n {n :notes o :octave :as args}]
  (fn [_]
    (let [c (get-mode-chord @master-key @master-mode @master-root o n)]
      (play-chord c args))))

(defn i-mode-chord-stepper [args pattern]
  (stepper (fn [args s] {:foo (i-mode-chord-action s args)}) args pattern))
