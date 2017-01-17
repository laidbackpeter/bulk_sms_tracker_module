(ns
  ^{:author "pmuchina@mo-de.com"}
  bc-module-kenya.rmq-conn
  (:gen-class)
  (:require [langohr.core      :as rmq]
            [langohr.channel   :as lch]
            [langohr.queue     :as lq]
            [langohr.exchange  :as le]
            [langohr.basic     :as lb]
            [bc-module-kenya.config :as config]
            [clojure.tools.logging :as log]
            [cheshire.core :refer :all]
            ))


(defn publish_data
  [message]
  (log/info (str "Publishing - " (str message)))
  ;;Connect to rabbitmq
    (let
      [
       queue_host config/queue_host
       queue_port config/queue_port
       queue_name config/queue_name
       queue_exchange config/queue_exchange
       queue_routing_key config/queue_routing_key
       queue_username config/queue_username
       queue_password config/queue_password
       conn (rmq/connect {:host queue_host :port queue_port :vhost "/" :username queue_username :password queue_password})
       ch (lch/open conn)
       mess message
       ]
      ;;Declare exchange
      (le/declare ch queue_exchange "direct")
      ;;;Declare queue
      (lq/declare ch queue_name {:durable true :exclusive false :auto-delete false})
      ;;;bind queue to exchange
      (lq/bind ch queue_name queue_exchange{:routing-key queue_routing_key})
      ;;Publish
      (lb/publish ch queue_exchange queue_routing_key mess{:content-encoding "UTF-8", :content-type "application/json"})
      ;;unbind/close connection
      (lq/unbind ch queue_name queue_exchange)
      (rmq/close ch)
      (rmq/close conn)
      )
  )

;;(println message)
;;(publish_data message)
