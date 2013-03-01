(ns ragtime.migrator.postgres
   (:require [clojure.java.jdbc :as sql]
             [ragtime.core :as ragtime])
   (:import (java.util TimeZone)
            (java.util Calendar)
            (java.sql Timestamp)))


(def ^:private migrations-table "ragtime_migrations")

(defn ^:internal ensure-migrations-table-exists [db]
  ;; TODO: is there a portable way to detect table existence?
  ;; Timestamp works better for PgSql than date
  (sql/with-connection db
    (try
      (sql/create-table migrations-table
                        [:id "varchar(255)"]
                        [:created_at "timestamp with time zone"])
      (catch Exception _))))

(defn get-timestamp-now []
   "Get the timestamp into something PgSQL understands. Forces UTC"
   (Timestamp. (.getTimeInMillis (Calendar/getInstance (TimeZone/getTimeZone "UTC")))))


(defrecord PgSqlDatabase [classname subprotocol subname user password]
  ragtime/Migratable
  (add-migration-id [db id]
    (sql/with-connection db
      (ensure-migrations-table-exists db)
      (sql/insert-values migrations-table
                         [:id :created_at] [(str id) (get-timestamp-now)])))
  
  (remove-migration-id [db id]
    (sql/with-connection db
      (ensure-migrations-table-exists db)
      (sql/delete-rows migrations-table ["id = ?" id])))

  (applied-migration-ids [db]
    (sql/with-connection db
      (ensure-migrations-table-exists db)
      (sql/with-query-results results
        ["SELECT id FROM ragtime_migrations ORDER BY created_at"]
        (vec (map :id results)))))
 )

(defrecord JndiPgSqlDatabase [name]
  ragtime/Migratable
  (add-migration-id [db id]
    (sql/with-connection db
      (ensure-migrations-table-exists db)
      (sql/insert-values migrations-table
                         [:id :created_at] [(str id) (get-timestamp-now)])))
  
  (remove-migration-id [db id]
    (sql/with-connection db
      (ensure-migrations-table-exists db)
      (sql/delete-rows migrations-table ["id = ?" id])))

  (applied-migration-ids [db]
    (sql/with-connection db
      (ensure-migrations-table-exists db)
      (sql/with-query-results results
        ["SELECT id FROM ragtime_migrations ORDER BY created_at"]
        (vec (map :id results)))))
 )
