(ns musicbox.controlp5
  (:require [rosado.processing :as p5]
			[clojure.contrib.pprint :as pp])
  (:import (controlP5 ControlP5 ControlListener ControlEvent)
		   (javax.swing JFrame)
		   (processing.core PApplet)))

(declare *ctrl*)
(def bg (atom 10))
(def triggers (atom {}))
(def layout (atom {}))

;; (def layout {"Group" [[:slider "sliderA" 0 100 :lb]
;; 					  [:numberbox "numberboxB"]
;; 					  [:numberbox "numberboxC" :lb]
;; 					  [:button "buttonB"]
;; 					  [:button "buttonC"]]})

(defn draw [this]
  (p5/background-float @bg))

(defn auto-layout [layout]
  (doseq [[group-name controls] layout]
	(let [group (.addGroup *ctrl* group-name 30 30)]
	  (.begin *ctrl* group 10 10)
	  (let [mmap {:slider (memfn addSlider name min max)
				  :numberbox (memfn addNumberbox name)
				  :button (memfn addButton name)}]
		(doseq [control controls]
		  (let [method (mmap (first control))
				lb (= (last control) :lb)
				args (if lb (butlast (rest control))
						 (rest control))]
			(let [thing (apply method *ctrl* args)]
			  (if lb (.linebreak thing))))))
	  (.end *ctrl*))))

(defn setup [this]
  "Runs once."
  (def *ctrl* (ControlP5. this))
  (auto-layout @layout)
  (p5/smooth)
  (p5/framerate 10))

(defn dispatch-event [^ControlEvent evt]
  (when-let [trigger (@triggers (.name evt))]
	(trigger (.value evt))))

(def p5-applet
  (proxy [PApplet ControlListener] []
	(setup []
	  (binding [p5/*applet* this]
		(setup this)))
	(controlEvent [^ControlEvent evt]
	  (dispatch-event evt))
	(draw []
	  (binding [p5/*applet* this]
		(draw this)))))

(defn init-controlp5 []
  (.init p5-applet))

(def swing-frame (JFrame. "Processing with Clojure"))

(doto swing-frame
  (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE)
  (.setSize 800 800)
  (.add p5-applet)
  (.pack)
  (.show))

(.setDefaultCloseOperation swing-frame JFrame/DISPOSE_ON_CLOSE)

(defn controllers [name]
  (.controller *ctrl* name))
