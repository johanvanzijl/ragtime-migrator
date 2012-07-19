(ns ragtime.migrator.postgres-test
  (:use [clojure.test]
        [ragtime.migrator.postgres]
        [ragtime.core :only [add-migration-id remove-migration-id
                             applied-migration-ids]])
  (:require [clojure.java.jdbc :as sql]))
; this test has been copied from the ragtime.sql library
(def test-db (ragtime.migrator.postgres.PgSqlDatabase.
              "org.postgresql.Driver" "postgresql" "//localhost:5432/myapp_test"  "test"  "test"))

(defn pg-fixture [f]
  (sql/with-connection test-db
    (f)))

(use-fixtures :each pg-fixture)

(deftest test-add-migrations
  (add-migration-id test-db "12")
  (add-migration-id test-db "13")
  (add-migration-id test-db "20")
  (is (= ["12" "13" "20"] (applied-migration-ids test-db)))
  (remove-migration-id test-db "13")
  (is (= ["12" "20"] (applied-migration-ids test-db)))
  (remove-migration-id test-db "12")
  (remove-migration-id test-db "20"))