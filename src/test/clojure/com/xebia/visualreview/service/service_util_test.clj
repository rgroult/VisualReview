;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Copyright 2015 Xebia B.V.
;
; Licensed under the Apache License, Version 2.0 (the "License")
; you may not use this file except in compliance with the License.
; You may obtain a copy of the License at
;
;  http://www.apache.org/licenses/LICENSE-2.0
;
; Unless required by applicable law or agreed to in writing, software
; distributed under the License is distributed on an "AS IS" BASIS,
; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
; See the License for the specific language governing permissions and
; limitations under the License.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(ns com.xebia.visualreview.service.service-util-test
  (:require [clojure.test :refer :all]
            [slingshot.slingshot :as ex]
            [slingshot.test]
            [com.xebia.visualreview.service.service-util :as util]
            [com.xebia.visualreview.test-util :refer :all]))

(defn throw-java-exception-with-message [message]
  (throw (Exception. message)))

(defn throw-slingshot-exception-with-message [message]
  (ex/throw+ {:type :my-exception :message message}))

(deftest attempt-macro
  (testing "When body throws a Java exception"
    (is (thrown+-with-msg? service-exception? #"something went wrong: my exception message"
                          (util/attempt
                            (throw-java-exception-with-message "my exception message")
                            "something went wrong: %s" :1234))))
  (testing "When body throws a Slingshot exception"
    (is (thrown+-with-msg? service-exception? #"something went wrong: my exception message"
                          (util/attempt
                            (throw-slingshot-exception-with-message "something went wrong: my exception message")
                            "something went wrong: %s" :1234))))

  (testing "When no exception is thrown"
    (is (= "my return value" (util/attempt (str "my return " "value") "errormesage" :errorcode)) "Evaluates the body and returns its value")))

(deftest assume-macro
  (testing "When the form returns false"
    (is (thrown+-with-msg? service-exception? #"two"
                          (util/assume (= (+ 2 2) 5) "two plus two is not five" :225))))

  (testing "When the form returns true"
    (is (nil? (util/assume (= (+ 2 2) 4) "two plus two should be five" :225)) "Does not throw an exception")))
