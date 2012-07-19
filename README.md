ragtime-migrator
================

ragtime-migrator adds a few functions to [ragtime](https://github.com/weavejester/ragtime) which make working with SQL Databases easier. 
It also provides a specific implementation for PostgreSQL databases.

This is pretty much for my own use, but maybe someone will find it useful.

## Migrations

You should first familiarise yourself with [ragtime](https://github.com/weavejester/ragtime). All functions provided with ragtime can still be used with this library.

ragtime-migrator provides a `defmigration` macro for defining new migrations. All that the macro does is to ensure the migration is added to ragtime's `defined-migrations` atom. 
This enables easily applying migrations in bulk and ensuring migrations are not applied twice. New migrations are defined in the same way as for ragtime except you use the `defmigration` macro as shown below:

    (defmigration test-dog
       {:id "test-dog"
        :up (fn [db] (sql/with-connection db
                         (sql/do-commands ("CREATE TABLE test_dog(name varchar(100));"))))
        :down (fn [db] (sql/with-connection db
                         (sql/do-commands ("DROP TABLE test_dog;"))))})

In order to apply all defined migrations(which have not been applied to the database yet), you can use the `migrate-allnew` function.

    (migrate-allnew test-db) 

If you want to apply single migrations, without the risk of applying them twice you can use `migrate-new`.

    (migrate-new test-db test-dog)
    
For all other migration functions, such as rollbacks you can still use the standard ragtime functions.

## PostgreSQL

The ragtime.migrator.postgres library provides a  `PgSqlDatabase` record that can be used
to wrap a Postgres database connection map. `PgSqlDatabase` has a specific implementation of the `Migrateable` protocol which enables the use with Postgres Databases. 
This main difference between `PgSqlDatabase` and `ragtime.sql.database.SqlDatabase` is that `PgSqlDatabase` uses a timestamp instead of date type in the migrations table.

    #ragtime.migrator.postgres.PgSqlDatabase{
          :classname "org.postgresql.Driver"
          :subprotocol "postgresql"
          :subname "//localhost:5432/test_db"
          :user "test"
          :password "test"}


## License

Copyright © 2012 Johan van Zijl

Distributed under the Eclipse Public License, the same as Clojure.