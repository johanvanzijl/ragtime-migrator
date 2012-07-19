(ns ragtime.migrator.postgres-test
  (:use [clojure.test]
        [ragtime.migrator.postgres]
        [ragtime.migrator.core]
        [ragtime.core]))

(def test-db (ragtime.migrator.postgres.PgSqlDatabase.
              "org.postgresql.Driver" "postgresql" "//localhost:5432/myapp_test"  "test"  "test"))

(defn pg-fixture [f]
  (sql/with-connection test-db
    (f)))

(use-fixtures :each pg-fixture)
(defmigration test-dog
   {:id "test-dog"
    :up (fn [db] (sql/with-connection db
                       (sql/do-commands ("CREATE TABLE test_dog(name varchar(100));"))))
    :down (fn [db] (sql/with-connection db
                       (sql/do-commands ("DROP TABLE test_dog;"))))})

(defmigration test-cat
   {:id "test-dog"
    :up (fn [db] (sql/with-connection db
                       (sql/do-commands ("CREATE TABLE test_cat(name varchar(100));"))))
    :down (fn [db] (sql/with-connection db
                       (sql/do-commands ("DROP TABLE test_cat;"))))})
;"CREATE TABLE test_dog( id uuid NOT NULL,name varchar(100), user_id uuid, createdat timestamp with time zone, CONSTRAINT test_dog_pk_id PRIMARY KEY (id ))WITH ( OIDS=FALSE );"

(deftest test-add-migrations
  ;test if all migrations are applied
  (migrate-allnew test-db)
  (is (= ["test-dog" "test-cat"] (applied-migration-ids test-db)))
  (rollback test-cat)
  (is (= ["test-dog"] (applied-migration-ids test-db)))
  ;make sure dog is not applied twice
  (migrate-new test-db test-dog) 
  (is (= ["test-dog"] (applied-migration-ids test-db)))
  (rollback-last)
  (is (= [ ] (applied-migration-ids test-db))))
