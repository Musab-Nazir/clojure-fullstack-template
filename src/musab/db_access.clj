(ns musab.db-access
  (:require [next.jdbc :as jdbc]))

(def db-spec {:dbtype "h2" :dbname "app-db"})

(def db-source (jdbc/get-datasource db-spec))

(comment
  (jdbc/execute!
   db-source
   ["create table Users (id int auto_increment primary key,
   name varchar (255), email varchar (255))"])
  
  (jdbc/execute!
   db-source
   ["insert into Users (name, email) values 
     ('Musab Nazir', 'musabnazir@gmail.com')"])
  
  (jdbc/execute!
   db-source
   ["select * from users"]))