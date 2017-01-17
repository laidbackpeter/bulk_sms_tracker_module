(ns bc-module-kenya.gen-tests
  ^{:author "pmuchina@mo-de.com"}
  (:require [clojure.test.check.generators :as gen]
            [clojure.test.check :as tc]
            [clojure.test.check.properties :as prop]
            [bc-module-kenya.config :as config]
            )
  )


(defn delocalize_msisdn
  [msisdn]
  (try
    (str config/country_code msisdn)
    (catch Exception e (str "caught exception: " (.getMessage e))))
  )

(def property
  (prop/for-all [v (gen/choose 450000 900000)]
                (= (subs (str (delocalize_msisdn v)) 0 3) (str "254"))
                ))

(tc/quick-check 100 property)

(gen/sample (gen/choose 450000 900000))

(subs (str 123454) 1 3)

(subs (str (delocalize_msisdn 624324)) 0 3)