# Clojure Fullstack Template

Just a fullstack app template for myself (or others) to use in the future.


## Software Stack
* Reitit routing for both backend and frontend
* H2 DB for fast and easy prototyping
* Next JDBC as the DB library
* Shadow-cljs for frontend build tool
* Timbre for logging
* Reagent for React
* Dockerfile template

## Installation

Download from https://github.com/musab/clojure-fullstack

## Usage

Run the project directly:

    $ clojure -M -m musab.core

Run the project's tests (they'll fail until you edit them):

    $ clojure -M:test:runner

Build an uberjar:

    $ clojure -M:uberjar

Run that uberjar:

    $ java -jar core.jar

## License

Copyright © 2020 Musab

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
