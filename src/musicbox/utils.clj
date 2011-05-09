(ns musicbox.utils)

(def & comp)
(def p partial)

(defn rand-nums
  ([max] (rand-nums max max))
  ([max len] (repeatedly len #(rand-int max))))

(defn parse-integer [str]
    (try (Integer/parseInt str) 
         (catch NumberFormatException nfe 0)))

