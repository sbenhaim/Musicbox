(defproject musicbox "1.0.0-SNAPSHOT"
  :description "An odd approach to sorta algorithmic music."
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
				 [overtone "0.1.5"]
				 [org.clojars.automata/rosado.processing "1.1.0"]
                 ;; [overtone/osc-clj "0.3.0-SNAPSHOT"]
                 ;; [overtone/byte-spec "0.2.0-SNAPSHOT"]
                 ;; [overtone/midi-clj "0.2.0-SNAPSHOT"]
				 ]
  :dev-dependencies [[marginalia "0.2.0"]
					 [swank-clojure "1.4.0-SNAPSHOT"]
					 [clojure-source "1.2.0"]]
  ;; :jvm-opts ["-agentlib:jdwp=transport=dt_socket,server=y,suspend=n"]
  ;; :jvm-opts ["-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8888"]
  :resources-path "src/main/controlP5.jar")
