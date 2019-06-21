(ns tests.all
  (:require
    [cljs-time.core :as t]
    [cljs.test :refer [deftest is testing run-tests async use-fixtures]]
    [district.format :as format]
    [district.format.mount]
    [mount.core :as mount]
    [re-frame.core :refer [reg-event-fx dispatch-sync subscribe reg-cofx reg-sub dispatch trim-v]]))

(use-fixtures
  :each
  {:after
   (fn []
     (mount/stop))})


(deftest tests
  (-> (mount/with-args
        {:format {:default-datetime-formatter :basic-ordinal-date-time-no-ms
                  :default-local-datetime-formatter :basic-week-date-time
                  :default-local-date-formatter :week-date
                  :default-date-formatter :date
                  :default-locale "sk-SK"
                  :default-max-number-fraction-digits 4
                  :default-max-currency-fraction-digits 5
                  :default-min-currency-fraction-digits 3
                  :default-max-token-fraction-digits 6
                  :default-min-token-fraction-digits 3}})
    (mount/start))

  (is (= "2018003T000000Z" (format/format-datetime (t/date-time 2018 2 3))))
  (is (= "2018W056T101112.000" (format/format-local-datetime (t/local-date-time 2018 2 3 10 11 12))))
  (is (= "2018-02-03" (format/format-date (t/date-time 2018 2 3))))
  (is (= "2018-W05-6" (format/format-local-date (t/local-date-time 2018 2 3))))

  (is (= "1 000 000,1235" (format/format-number 1000000.12345678)))
  (is (= "10,000" (format/format-number 10 {:min-fraction-digits 3})))

  (is (= "$1,000,000.12346" (format/format-currency 1000000.12345678 {:currency "USD" :locale "en-US"})))
  (is (= "10,000 €" (format/format-currency 10 {:currency "EUR"})))

  (is (= "1 000 000,123457 BTC" (format/format-token 1000000.12345678 {:token "BTC"})))
  (is (= "10,000 BTC" (format/format-token 10 {:token "BTC"})))

  (is (= "10,000 ETH" (format/format-eth 10)))
  (is (= "10,000 DNT" (format/format-dnt 10)))

  (is (= "10K" (format/format-number-metric 10000)))
  (is (= "https://etherscan.io/address/0x7d10b16dd1f9e0df45976d402879fb496c114936"
         (format/etherscan-addr-url "0x7d10b16dd1f9e0df45976d402879fb496c114936")))

  (is (= "https://etherscan.io/tx/0x60a1ef75c4217e2a23eab7ae508ff000b458abe92a2f80d766da1223917faa26"
         (format/etherscan-tx-url "0x60a1ef75c4217e2a23eab7ae508ff000b458abe92a2f80d766da1223917faa26")))

  (is (= "5 minutes ago" (format/time-ago (t/minus (t/now) (t/minutes 5)))))
  (is (= "1 month ago" (format/time-ago (t/date-time 2017 10 10) (t/date-time 2017 11 10))))

  (is (= "1 car" (format/pluralize 1 "car")))
  (is (= "2 cars" (format/pluralize 2 "car")))
  (is (= "1 lady" (format/pluralize 1 "lad" "y" "ies")))
  (is (= "2 ladies" (format/pluralize 2 "lad" "y" "ies")))

  (is (= "abc..." (format/truncate "abcdefghlij" 6)))
  (is (= "ab" (format/truncate "abcdefghlij" 2 "")))

  (is (= "true" (format/format-bool true)))
  (is (= "false" (format/format-bool false)))

  (is (= "1 day" (format/format-time-unit :days 1)))
  (is (= "2 seconds" (format/format-time-unit :seconds 2)))
  (is (= "2 sec." (format/format-time-unit :seconds 2 {:short? true})))

  (is (= "1 day 10 hours 4 minutes 5 seconds" (format/format-time-units {:days 1 :hours 10 :minutes 4 :seconds 5})))

  (is (= "10 hours 4 min. 5 sec." (format/format-time-units {:hours 10 :minutes 4 :seconds 5}
                                                            {:short? true})))

  (is (= "/abc?a=1&b=2" (format/format-url "/abc" {:a 1 :b 2})))

  (is (= "a/b" (format/format-namespaced-kw :a/b)))

  (is (contains? #{"33,3%" "33.3%"} (format/format-percentage 1 3)))

  (is (contains? #{"14,2857%" "14.2857%"} (format/format-percentage 1 7 {:max-fraction-digits 4})))

  (is (= "http://127.0.0.1:8080/" (format/ensure-trailing-slash "http://127.0.0.1:8080")))

  (is (= "http://127.0.0.1:8080/" (format/ensure-trailing-slash "http://127.0.0.1:8080/")))

  (is (= "{\"title\":\"PepeSmile\",\"image-hash\":\"data\",\"search-tags\":\"pepe frog dank\"}"
         (format/clj->json {:title "PepeSmile"
                            :image-hash "data"
                            :search-tags "pepe frog dank"}))))

