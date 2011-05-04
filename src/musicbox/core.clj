(ns musicbox.core
  (:require [musicbox.controlp5 :as ctrl])
  (:use musicbox.pattern
		musicbox.theory
		midi
		musicbox.clock
		clojure.contrib.str-utils
		clojure.contrib.pprint))

(reset! ctrl/layout {"Group" [[:slider "sliderA" 0 100 :lb]
							  [:numberbox "numberboxB"]
							  [:numberbox "numberboxC" :lb]
							  [:button "buttonB"]
							  [:button "buttonC"]]})

(reset! ctrl/triggers
		{"sliderA" (fn [val] (reset! pitch (Math/floor val)))})

(ctrl/init-controlp5)
(def pitch (atom 60))

;; Todo: multiple clocks (maybe)
(pprint (midi-devices))
(def in (midi-in "MidiPipe Output 1"))
(def out (midi-out "MidiPipe Input 1"))

;; (def kyb (midi-in "Keystation Pro 88 Port 1"))
;; (midi-handle-events kyb (fn [msg ts] (println msg)))

(defn add-note [pos note & args]
  (let [{:keys [vel dur prob] :or {vel 60 dur 500 prob 100}} args]
	 (add-action
	  pos
	  (keyword (str "note-" note))
	  (fn [_ _] (maybe prob midi-note out note vel dur)))))

(defn remove-note [pos note & args]
  (remove-action pos (keyword (str "note-" note))))

(defn add-notes [pos repeat-interval note & args]
  (loop [index pos]
	(when (< index (count @pattern))
	  (apply add-note index note args)
	  (recur (+ index repeat-interval)))))

(defn remove-notes [pos repeat-interval note & args]
  (loop [index pos]
	(when (< index (count @pattern))
	  (remove-note index note)
	  (recur (+ index repeat-interval)))))

(defn chord-action [& _]
  (let [chord (make-chord :e :m 5)]
	(doseq [n chord]
	  (midi-note out n 50 200))))

(defn arp-action [_ pos]
  (let [chord (make-chord :e :m 5)
		note (nth chord (mod pos (count chord)))]
	  (midi-note out note 50 50)))

;; (stop-pattern)
(set-pattern 4 4)
(play-pattern 120)
(stop-pattern)
@pattern
(add-note 6 60)
(remove-all-actions)
(add-action 0 :chord #'chord-action)
;; (add-notes 1 4 65 :vel 50 :dur 50)
;; (remove-notes 0 2 70 :dur 1000 :vel 80)
;; (add-actions 0 1 :arp #'arp-action)
(add-actions 0 4 :slider (fn [& _] (println @pitch)))
;; (add-actions 0 1 :note73 (fn [& args] (maybe 50 midi-note-on out 73 50)))
;; (add-actions 0 1 :note75 (fn [& args] (maybe 50 midi-note-on out 75 50)))
