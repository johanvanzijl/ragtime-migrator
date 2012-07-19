(ns ragtime.migrator.core
   (:require [ragtime.core :as ragtime]
             [ragtime.strategy :as strategy]))

(defmacro defmigration
          "Defines a new migration to be used.
           Calls ragtime/remember-migration to ensure migration is registered"
          {:arglists '([name doc-string? attr-map? & bodies])}
          [name & args]
          `(do (def ~name ~@args)
               (ragtime/remember-migration ~name)))

(defn migrate-allnew 
      "Applies all new migrations to the database"
      ([db]
      (ragtime/migrate-all db 
                           (reverse (vec (vals @ragtime/defined-migrations)))
                           strategy/apply-new))
      ([db migrations]
       (ragtime/migrate-all db migrations strategy/apply-new)))

(defn migrate-new 
      "Applies only new migrations to the database. Ensures no duplicate application"
      [db migration]
      (when-not
         (some #(= (:id migration) %) (ragtime/applied-migration-ids db))
         (ragtime/migrate db migration)))
