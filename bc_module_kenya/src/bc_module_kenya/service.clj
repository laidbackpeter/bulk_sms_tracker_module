(ns bc-module-kenya.service
  ^{:author "pmuchina@mo-de.com"}
  (:gen-class)
  (:require [bc-module-kenya.config :as config]
            [clojure.tools.logging :as log]
            [bc-module-kenya.db-conn :as db]
            [clj-time.core :as t]
            [bc-module-kenya.helper-fns :as hfns]
            )
  )

(defn infinite
  "Parent process"
  [f seconds]
  (try
    (future (loop [] (f) (Thread/sleep (* seconds 1000)) (recur)))
    (catch Exception e (str "caught exception: " (.getMessage e))))
  )

(defn process_subs
  "Parent function"
  []
  (try
  (println "Extracting subs")
  (def hour (t/hour (t/to-time-zone (t/now) (t/time-zone-for-offset config/timezone))))
  (if (and (>= hour config/start_time) (< hour config/stop_time))
    (do  (let [message  (db/get_subs)] (doseq [x message] (hfns/publish_data x))))
    (do (println "Not time to process") (log/info "Logging-don't"))
    )
  (catch Exception e (str "caught exception: " (.getMessage e))))
  )

;;(process_subs)
;;(db/get_subs)

(defn -main []
  (try
  (db/apply-schema-migration)
  (infinite process_subs config/poll_interval)
  (catch Exception e (str "caught exception: " (.getMessage e))))
 )

;;(-main)

