(ns bc-module-kenya.helper-fns
  ^{:author "pmuchina@mo-de.com"}
  (:require [bc-module-kenya.config :as config]
            [clojure.tools.logging :as log]
            [cheshire.core :refer :all]
            [clj-http.client :as req]
            [clj-http.util :as en]
            [bc-module-kenya.rmq-conn :as rmq]
            )
  )

(defn delocalize_msisdn
  [msisdn]
  (try
    (str config/country_code msisdn)
    (catch Exception e (str "caught exception: " (.getMessage e))))
  )

;;kannel
;;(def messgae (generate-string {:session-id 1235 :request-id 123 :msisdn 123 :message "Hello my old friend" :flash? false :from "Airtel"}))

(defn send_to_kannel
  [obj]
  ;;get url from config and populate msisdn value to get url
  (def details (parse-string obj))
  (try
  (let [url (format config/kannel-url config/kannel-user config/kannel-pass (details :msisdn) (en/url-encode (details :message)) config/from)]
    ;;send request
    (log/info (str "Sending to kannel - " (str url)))
    (req/get url)
    )
  (catch Exception e (str "caught exception: " (.getMessage e))))
  )

(defn publish_data
  [obj]
  (try
    (condp = (clojure.string/lower-case config/channel)
      "kannel" (send_to_kannel obj)
      "rmq" (rmq/publish_data obj))
    (catch Exception e (str "caught exception: " (.getMessage e))))
  )

(defn parse-values
  "Function parses values in code"
  [msg values]
  (clojure.string/replace msg #"\+\{(\S+)\}"  #(str (values (keyword (read-string (last %1)))))))

(defn parse-params
  "Function parses values in configuration"
  [msg]
  (clojure.string/replace msg #"\$\{(\S+)\}"  #(str (eval (read-string (last %1))))))

(defn get-message
  "Function returns parsed message"
  [msg values]
  (parse-values (parse-params msg) values))

;;(println (get-message "Dear Customer, N+{deducted} has been deducted from your account to repay your N+{loaned} loan. Your balance is now N+{balance}. Please recharge your account."  {:deducted 30 :loaned 50 :balance 0}))
;;(send_to_kannel message)

