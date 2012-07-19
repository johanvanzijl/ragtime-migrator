(defproject ragtime-migrator "0.1.0-SNAPSHOT"
  :description "Add-on for ragtime to provide some useful SQL specific migration functions"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.2.1"]
                 [org.clojure/java.jdbc "0.1.1"]
                 [ragtime/ragtime.core "0.2.1"]]
  :profiles  {:dev {:dependencies [[postgresql "9.0-801.jdbc4"]]}}
  )
                 
