(ns
  ^{:author "pmuchina@mo-de.com"}
  bc-module-kenya.config
  (:require [clojure.tools.logging :as log]
            )
  (:use [propertea.core])

  )

;;Required vectors - contains all the compulsory values of the config
(def config-map (read-properties "C:\\Users\\pmuchina.MO-DE\\Desktop\\projects\\clojure\\bc_module_kenya\\bc_module.config" :parse-int [:connection-timeout :validation-timeout :idle-timeout :max-lifetime :minimum-idle :maximum-pool-size :queue_port :start_time :stop_time :timezone :message_limit :country_code :poll-interval] :required [:db-name :db-ip :db-username :db-password :db-port :poll-interval :channel :timezone :from :queue_host :name]))

;;(validate-config)
(log/info config-map)
(def db-name (:db-name config-map))
(def db-ip (:db-ip config-map))
(def db-username (:db-username config-map))
(def db-password (:db-password config-map))
(def db-port (:db-port config-map))
(def connection-timeout (or (:connection-timeout config-map) 30000))
(def validation-timeout (or (:validation-timeout config-map) 5000))
(def idle-timeout (or (:idle-timeout config-map) 300000))
(def max-lifetime (or (:max-lifetime config-map) 1800000))
(def minimum-idle (or (:minimum-idle config-map) 10))
(def maximum-pool-size (or (:maximum-pool-size config-map) 75))
(def queue_host (:queue_host config-map))
(def queue_port (or (:queue_port config-map) 5672))
(def queue_name (or (:queue_name  config-map) "promos-test"))
(def queue_exchange (or (:queue_exchange  config-map) "promo-queue-sms1"))
(def queue_routing_key (or (:queue_routing_key  config-map) "promo1"))
(def queue_username (or (:queue_username  config-map) "peter"))
(def queue_password (or (:queue_password  config-map) "peter"))
(def start_time (or (:start_time  config-map) 6))
(def stop_time (or (:stop_time  config-map) 18))
(def timezone (or (:timezone  config-map) 3))
(def message_limit (or (:message_limit  config-map) 300))
(def from (:from  config-map))
(def country_code (or (:country_code  config-map) 254))
(def message_1 (:message_1  config-map))
(def message_2 (:message_2  config-map))
(def message_3 (:message_3  config-map))
(def message_4 (:message_4  config-map))
(def message_5 (:message_5  config-map))
(def channel (:channel  config-map))
(def kannel-url (or (:kannel-url config-map) "http://127.0.0.1:13013/sendsms?username=%s&password=%s&to=%s&text=%s&from=%s"))
(def kannel-user (or (:kannel-user  config-map) "mdsa"))
(def kannel-pass (or (:kannel-password  config-map) "mdsa"))
(def poll_interval (:poll-interval  config-map))


