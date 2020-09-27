(ns pluggable-web.core-test
  (:require [pluggable-injectable.core :as sut]
            [clojure.spec.alpha :as s]
            #?(:clj [clojure.test :as t :refer [deftest testing is]]
               :cljs [cljs.test :as t :include-macros true :refer [deftest testing is]]))
  (:import [clojure.lang ExceptionInfo]))

(deftest reuse-container-keys-tests
  (testing "Keys correctly selected"
    (let [m  {:a 1 :b 2 :c 3}
          ks [:a :c]
          r  (sut/reuse-container-keys m ks)
          ca (:constructor (:a r))
          cb (:constructor (:b r))
          cc (:constructor (:c r))]
      (is (nil? cb))
      (is (= 1 ((first ca))))
      (is (= 3 ((first cc)))))))

(deftest load-plugins-tests
  (testing "Plugins can be loaded"
    (let [p1   {:id :p1 :beans {:a 1}}
          p2   {:id :p2 :beans {:b 2} :reusable-beans {:c 1}}
          p3   {:id :p2 :beans {:b 5} :reusable-beans {:c 5}}
          sys  (sut/load-plugins [p1 p2])
          sys2 (sut/load-plugins [p1 p3] sys)]
      (is (= 1 (:a sys)))
      (is (= 2 (:b sys)))
      (is (= 1 (:c sys)))
      (is (= 5 (:b sys2)))
      (is (= 1 (:c sys2))))))

(deftest push-plugins!-tests 
  (testing "Plugins can be loaded"
    (let [p1   {:id :p1 :beans {:a 1}}
          p2   {:id :p2 :beans {:b 2} :reusable-beans {:c 1}}
          cont (sut/push-plugins! [p1 p2] true)
          p3   {:id :p2 :beans {:b 5} :reusable-beans {:c 5}}
          con2 (sut/push-plugins! [p1 p3] false)]
      (is (= 1 (:a cont)))
      (is (= 2 (:b cont)))
      (is (= 1 (:c cont)))
      (is (= 5 (:b con2)))
      (is (= 1 (:c con2))))))
