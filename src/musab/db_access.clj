(ns musab.db-access
  (:require [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))

(def db-spec {:dbtype "sqlite" :dbname "app-db"})

(def db-source (jdbc/get-datasource db-spec))

(defn get-all-users
  []
  (jdbc/execute!
   db-source
   ["select * from users"]
   {:builder-fn rs/as-unqualified-lower-maps}))

(comment
  (jdbc/execute!
   db-source
   ["create table Users (id int auto_increment primary key,
   name varchar (255), email varchar (255))"])

  (jdbc/execute!
   db-source
   ["insert into Users (name, email) values 
     ('The Doctor', 'timelord3000@tardis.com')"])

  (jdbc/execute!
   db-source
   ["select * from users"]))