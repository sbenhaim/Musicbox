(ns musicbox.touch-osc
  (:use [osc]))

;; (osc-close tosc-server)
;; (osc-close tosc-client)
(def tosc-server (osc-server 5000))
(def tosc-client (osc-client "192.168.1.101" 5001))

(def triggers (atom {}))

(defn dispatch [{[val] :args :as msg}]
  (doseq [[pattern action] @triggers]
    (if-let [matches (re-seq pattern (:path msg))]
		  (action {:val val :msg msg :matches matches}))))

(osc-listen tosc-server (fn [msg]
					 (dispatch msg)))

