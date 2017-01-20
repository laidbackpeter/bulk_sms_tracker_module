(ns bc-module-kenya.db-conn
  ^{:author "pmuchina@mo-de.com"}
  (:require [bc-module-kenya.config :as config]
            [hikari-cp.core :refer :all]
            [clojure.java.jdbc :as jdbc]
            [clojure.tools.logging :as log]
            [cheshire.core :refer :all]
            [bc-module-kenya.helper-fns :as hfns]
            [yesql.core :refer [defqueries]]
            )
  )

;;get DB resource
(def datasource-options {:auto-commit        true
                         :read-only          false
                         :connection-timeout config/connection-timeout
                         :validation-timeout config/validation-timeout
                         :idle-timeout       config/idle-timeout
                         :max-lifetime       config/max-lifetime
                         :minimum-idle       config/minimum-idle
                         :maximum-pool-size  config/maximum-pool-size
                         :pool-name          "db-pool"
                         ;;:adapter          "postgresql" --Mutually exclusive with jdbc-url
                         :username           config/db-username
                         :password           config/db-password
                         :database-name      config/db-ip
                         :server-name        config/db-name
                         :port-number        (read-string config/db-port)
                         :jdbc-url           (str "jdbc:postgresql://" config/db-ip ":" config/db-port "/" config/db-name)
                         :register-mbeans    false})

(def datasource
  (make-datasource datasource-options))

(def connection {:datasource datasource})

(defqueries "sql/queries.sql"
            {:connection connection})

;;Function to check if schema is there
(defn db-schema-migrated?
  "Check if the schema has been created in database"
  []
  (log/info "Checking if required schema exists")
  (try
    (pos? (:count (first (check-if-schema-exists))))
  (catch Exception e (str "caught exception: " (.getMessage e))))
  )

;;(db-schema-migrated?)

(defn apply-schema-migration
  "Apply the schema to the database"
  []
  (try
  (when (not (db-schema-migrated?))
    (app-schema))
  (catch Exception e (str "caught exception: " (.getMessage e))))
  )

(jdbc/with-db-transaction [tx connection]
                            (delete-sub! {:subscriber_fk (:subscriber_fk map)} {:connection tx}))

;;(apply-schema-migration)
(defn make_message
  [map]
  (try
    ;;(delete-sub! {:subscriber_fk (:subscriber_fk map)})
    ;; How to incorporate DB transactions when using YESQL
    (jdbc/with-db-transaction [tx connection]
                              (delete-sub! {:subscriber_fk (:subscriber_fk map)} {:connection tx}))
    (log/info (str "Make message - " (str map)))
    (generate-string {:session-id (:subscriber_fk map)
                      :request-id (:subscriber_fk map)
                      :msisdn (hfns/delocalize_msisdn (:subscriber_fk map))
                      :message (hfns/get-message (eval (symbol (str "config/message_" (str (:message_type map))))) {:amount (:amount map) :message-type (:message_type map) :subscriber-fk (:subscriber_fk map)})
                      :flash? false
                      :from config/from})
    (catch Exception e (str "caught exception: " (.getMessage e)))
    )
  )

(defn get_subs
  []
  (log/info "Getting subs")
  (try
    (get-subs {:limit config/message_limit} {:as-arrays? false :row-fn make_message})
    (catch Exception e (str "caught exception: " (.getMessage e))))
  )

;;(get_subs)


;;(def message (generate-string {:session-id 1235 :request-id 123 :msisdn 123 :message "Hello my old friend" :flash? false :from "Airtel"}))