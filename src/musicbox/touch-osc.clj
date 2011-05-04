(ns musicbox.touch-osc
  (:use [osc]))

(def server (osc-server 5000))
(def client (osc-client "192.168.1.100" 5001))
;; (osc-close server)
;; (osc-close client)

(def triggers {})

(osc-listen server (fn [msg]
					 (dispatch msg)))

(defn dispatch [msg]
  (doseq [[pattern action] triggers]
	  (if (re-seq pattern (:path msg))
		  (action msg))))

(def triggers
  {#"/1/" #(println (:args %))})
