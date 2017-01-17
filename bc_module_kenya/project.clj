(defproject bc_module_kenya "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.postgresql/postgresql "9.4-1201-jdbc41"]
                 [org.clojure/java.jdbc "0.6.1"]
                 [hikari-cp "1.7.5"]
                 [org.slf4j/slf4j-log4j12 "1.7.3"]
                 [org.clojure/tools.logging "0.3.1"]
                 [com.novemberain/langohr "3.6.1"]
                 [cheshire "5.6.3"]
                 [clj-time "0.12.2"]
                 [org.clojure/test.check "0.9.0"]
                 [clj-http "2.3.0"]
                 [yesql "0.5.3"]
                 [propertea "1.2.3"]
                 ]
  :jvm-opts ["-Dlog4j.configuration=file:C:\\Users\\pmuchina.MO-DE\\Desktop\\projects\\clojure\\bc_module_kenya\\log4j.properties"
             "-Dbc_module_config=C:/Users/pmuchina.MO-DE/Desktop/projects/clojure/bc_module_kenya/bc_module.config"]
  :profiles {:uberjar {:aot :all}}
  :main ^:skip-aot bc-module-kenya.service
  :target-path "target/%s")