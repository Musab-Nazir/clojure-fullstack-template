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

Download from https://github.com/Musab-Nazir/clojure-fullstack-template

## Usage
Build the frontend to ensure your cljs is compiled to js:

    $ npm run script build

Run the backend server on port 8080 (if no port is provided 8888 is used):

    $ clojure -M -m musab.core 8080
    
Run the frontend in watch mode for dev purposes. The default port is 3000:

    $ npm start

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
