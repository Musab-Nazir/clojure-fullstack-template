(ns musab.core-test
  (:require [clojure.test :refer [deftest testing is]]
            [musab.handler :refer [app]]))

(deftest ping-test
  (testing "Testing if the main api can be pinged"
    (is (= 200
           (:status
            (app {:request-method :get, :uri "/api/ping"}))))))

(deftest admin-test
  (testing "Testing if the admin api can be pinged"
    (is (= 200
           (:status
            (app {:request-method :get, :uri "/api/admin/users"}))))))
