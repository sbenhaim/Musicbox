(ns musicbox.song
  (:use musicbox.clock))

(mk-pattern
 :sixteenth (midi-note ...)
 :quarter   (midi-note ...))


(mk-pattern 12 14 12 10)

(def song
  {"1:1"  {:key :a
		   :tempo 120
		   :patterns #{:one :two}}
   "5:1"  {:patterns #{:three :four}}
   "9:1"  (song "1:1")
   "17:1" {:patterns #{:five :six}}})

(def pattern :sixteenth
  [{...}
   {...}
   {...}
   {...}])
