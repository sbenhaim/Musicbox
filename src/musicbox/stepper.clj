(ns musicbox.stepper
  (:use musicbox.clock))

(defn prob [percent]
  (> percent (rand-int 100)))

(defn maybe [chance fn & args]
  (when (prob chance) (apply fn args)))

(defn modify-stepper [stepper step fun & args]
  (assoc stepper step (apply fun (stepper step) args)))

(defn modify-actions [stepper fun step repeat-interval type action]
  (loop [index step stepper stepper]
	(if (< index (count stepper))
	  (recur (+ index repeat-interval)
			 (fun stepper index type action))
	  stepper)))

(defn add-action [stepper step type action]
  (modify-stepper stepper step assoc type action))

(defn add-actions [stepper step repeat-interval type action]
  (modify-actions stepper add-action step repeat-interval type action))

(defn remove-action [stepper step type]
  (modify-stepper stepper step dissoc type))

(defn remove-actions [stepper step repeat-interval type action]
  (modify-actions stepper remove-action step repeat-interval type action))

(defn call-actions [stepper step info]
  (doseq [[k a] (stepper step)]
	(a (conj info [:step step]))))

(defn clock-tick-fn [res stepper]
  (fn [tick-info]
    (when-let [num (tick-info res)]
      (let [step (mod num (count stepper))]
        (call-actions stepper step tick-info)))))

(defn mk-stepper [len mul] (vec (repeat (* len mul) {})))

(defn add-stepper [name res stepper]
  (add-tock name (clock-tick-fn res stepper)))

(defmacro add-stepper+ [stepper]
  `(add-stepper (keyword (quote ~stepper)) ~stepper))

(def remove-stepper remove-tock)

(defmacro remove-stepper+ [stepper]
  `(remove-stepper (keyword (quote ~stepper))))
