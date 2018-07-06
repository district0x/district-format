# district-format

[![Build Status](https://travis-ci.org/district0x/district-format.svg?branch=master)](https://travis-ci.org/district0x/district-format)


Set of functions helpful for formatting. Formatting functions always return string.  
This library also comes with optional [mount](https://github.com/tolitius/mount) module at [district.format.mount](#districtformatmount), that
helps to set up global configuration in a cleaner way. 


## Installation
Add `[district0x/district-format "1.0.1"]` into your project.clj  
Include `[district.format]` in your CLJS file  
Optionally include `[district.format.mount]` in your CLJS file, where you use `mount/start`

## API Overview
- [district.format](#districtformat)
  - [format-datetime](#format-datetime)
  - [format-local-datetime](#format-local-datetime)
  - [format-date](#format-date)
  - [format-local-date](#format-local-date)
  - [format-number](#format-number)
  - [format-currency](#format-currency)
  - [format-token](#format-token)
  - [format-eth](#format-eth)
  - [format-dnt](#format-dnt)
  - [format-number-metric](#format-number-metric)
  - [etherscan-addr-url](#etherscan-addr-url)
  - [etherscan-tx-url](#etherscan-tx-url)
  - [time-ago](#time-ago)
  - [pluralize](#pluralize)
  - [truncate](#truncate)
  - [format-bool](#format-bool)
  - [format-time-unit](#format-time-unit)
  - [format-time-units](#format-time-units)
  - [format-url](#format-url)
  - [format-namespaced-kw](#format-namespaced-kw)
  - [format-percentage](#format-percentage)
  - [ensure-trailing-slash](#ensure-trailing-slash)
  - [clj->json](#clj->json)
- [district.format.mount](#districtformatmount)
  

## district.format

#### <a name="format-datetime"></a>`format-datetime [date & [formatter]]`
Formats [cljs-time](https://github.com/andrewmcveigh/cljs-time) datetime. Optionally you can pass a formatter. List
of available formatters can be found at [cljs-time.format/formatters](https://github.com/andrewmcveigh/cljs-time/blob/master/src/cljs_time/format.cljs#L142),
or you can pass formatting string as well.

Default formatter can be set at `format/*default-datetime-formatter*` or with mount under the key `:default-datetime-formatter`.

Default: `:rfc822`

```clojure
(format/format-datetime (t/date-time 2018 2 3))
;; => "Sat, 03 Feb 2018 00:00:00 Z"

(format/format-datetime (t/date-time 2018 2 3) :basic-date)
;; => "20180203"
```


#### <a name="format-local-datetime"></a>`format-local-datetime [date & [formatter]]`
Formats local datetime.

Default formatter can be set at `format/*default-local-datetime-formatter*` or with mount under the key `:default-local-datetime-formatter`.

Default: `:rfc822`

```clojure
(format/format-local-datetime (t/local-date-time 2018 2 3 10 11 12))
;; => "Sat, 03 Feb 2018 10:11:12"
```


#### <a name="format-date"></a>`format-date [date & [formatter]]`
Formats date.

Default formatter can be set at `format/*default-date-formatter*` or with mount under the key `:default-date-formatter`.

Default: `"EEE, dd MMM yyyy"`

```clojure
(format/format-date (t/date-time 2018 2 3))
;; => "Sat, 03 Feb 2018"
```


#### <a name="format-local-date"></a>`format-local-date [date & [formatter]]`
Formats local date.

Default formatter can be set at `format/*default-local-date-formatter*` or with mount under the key `:default-local-date-formatter`.

Default: `"EEE, dd MMM yyyy"`

```clojure
(format/format-local-date (t/local-date-time 2018 2 3))
;; => "Sat, 03 Feb 2018"
```


#### <a name="format-number"></a>`format-number [x & [opts]]`
Formats a number.  
Optional opts: 
* `:locale`
* `:max-fraction-digits`
* `:min-fraction-digits`

Default locale can be set at `format/*default-locale*` or with mount under the key `:default-locale`.  
Default max-fraction-digits can be set at `format/*default-max-number-fraction-digits*` 
or with mount under the key `:default-max-number-fraction-digits`.

Default locale: `"en-US"`
Default max-fraction-digits: `2`

```clojure
(format/format-number 1000000.12945678)
;; => "1,000,000.13"

(format/format-number 1000000.12945678 {:locale "de-DE" :max-fraction-digits 3})
;; => "1.000.000,129"
```


#### <a name="format-currency"></a>`format-currency [x & [opts]]`
Formats a number with fiat currency.  
Optional opts: 
* `:locale`
* `:max-fraction-digits`
* `:min-fraction-digits`
* `:currency`

Default locale can be set at `format/*default-locale*` or with mount under the key `:default-locale`.  
Default max-fraction-digits can be set at `format/*default-max-currency-fraction-digits*` 
or with mount under the key `:default-max-currency-fraction-digits`.  
Default min-fraction-digits can be set at `format/*default-min-currency-fraction-digits*` 
or with mount under the key `:default-min-currency-fraction-digits`.  

Default locale: `"en-US"`
Default max-fraction-digits: `2`
Default min-fraction-digits: `nil`

```clojure
(format/format-currency 1000000.12945678 {:currency "USD"})
;; => "$1,000,000.13"
```


#### <a name="format-token"></a>`format-token [x & [opts]]`
Formats a number.  
Optional opts: 
* `:locale`
* `:max-fraction-digits`
* `:min-fraction-digits`
* `:token`

Default locale can be set at `format/*default-locale*` or with mount under the key `:default-locale`.  
Default max-fraction-digits can be set at `format/*default-max-token-fraction-digits*` 
or with mount under the key `:default-max-token-fraction-digits`.  
Default min-fraction-digits can be set at `format/*default-min-token-fraction-digits*` 
or with mount under the key `:default-min-token-fraction-digits`.  

Default locale: `"en-US"`
Default max-fraction-digits: `2`
Default min-fraction-digits: `nil`

```clojure
(format/format-token 1000000.12945678 {:token "DNT"})
;; => "1,000,000.13 DNT"
```

#### <a name="format-eth"></a>`format-eth [x & [opts]]`
Calls `format-token` with token `ETH`.
```clojure
(format/format-eth 1000000.12945678)
;; => "1,000,000.13 ETH"
```

#### <a name="format-dnt"></a>`format-dnt [x & [opts]]`
Calls `format-token` with token `DNT`.
```clojure
(format/format-dnt 1000000.12945678)
;; => "1,000,000.13 DNT"
```

#### <a name="format-number-metric"></a>`format-number-metric [x & [opts]]`
Formats number in shortened form with metric unit symbols. 
```clojure
(format/format-number-metric 10000)
;; => "10K"

(format/format-number-metric 10000000)
;; => "10M"
```


#### <a name="etherscan-addr-url"></a>`etherscan-addr-url [address]`
Returns etherscan url for an address 
```clojure
(format/etherscan-addr-url "0x7d10b16dd1f9e0df45976d402879fb496c114936")
;; => "https://etherscan.io/address/0x7d10b16dd1f9e0df45976d402879fb496c114936"
```


#### <a name="etherscan-tx-url"></a>`etherscan-tx-url [tx-hash]`
Returns etherscan url for a transaction 
```clojure
(format/etherscan-tx-url "0x60a1ef75c4217e2a23eab7ae508ff000b458abe92a2f80d766da1223917faa26")
;; => "https://etherscan.io/tx/0x60a1ef75c4217e2a23eab7ae508ff000b458abe92a2f80d766da1223917faa26"
```


#### <a name="time-ago"></a>`time-ago [from-time & [to-time]]`
Returns time ago string. If `to-time` is not given, current time is used. 
 
```clojure
(format/time-ago (t/minus (t/now) (t/minutes 5)))
;; => "5 minutes ago"

(format/time-ago (t/date-time 2017 10 10) (t/date-time 2017 11 10))
;; => "1 month ago"
```


#### <a name="pluralize"></a>`pluralize [n & [word ending1 ending2]]` 
```clojure
(format/pluralize 2 "car")
;; => "2 cars"

(format/pluralize 2 "lad" "y" "ies")
;; => "2 ladies"
```

#### <a name="truncate"></a>`truncate [string length & [suffix]]` 
```clojure
(format/truncate "abcdefghlij" 6)
;; => "abc..."

(format/truncate "abcdefghlij" 2 "")
;; => "ab"
```

#### <a name="format-bool"></a>`format-bool [true?]` 
```clojure
(format/format-bool true)
;; => "true"
```

#### <a name="format-time-unit"></a>`format-time-unit [unit amount & [opts]]` 
```clojure
(format/format-time-unit :days 1)
;; => "1 day"

(format/format-time-unit :seconds 2)
;; => "2 seconds"

(format/format-time-unit :seconds 2 {:short? true})
;; => "2 sec."
```

#### <a name="format-time-units"></a>`format-time-units [time-units & [opts]]` 
```clojure
(format/format-time-units {:days 1 :hours 10 :minutes 4 :seconds 5})
;; => "1 day 10 hours 4 minutes 5 seconds"

(format/format-time-units {:hours 10 :minutes 4 :seconds 5} {:short? true})
;; => "10 hours 4 min. 5 sec."
```

#### <a name="format-url"></a>`format-url [path query-map]` 
```clojure
(format/format-url "/abc" {:a 1 :b 2})
;; => "/abc?a=1&b=2"
```

#### <a name="format-namespaced-kw"></a>`format-namespaced-kw [kw]` 
```clojure
(format/format-namespaced-kw :a/b)
;; => "a/b"
```

#### <a name="format-percentage"></a>`format-namespaced-kw [p t]`
```clojure
(format/format-percentage 1 3)
;; => "33%"
```

#### <a name="ensure-trailing-slash"></a>`ensure-trailing-slash []`
```clojure
(format/ensure-trailing-slash "http://127.0.0.1:8080")
;; => "http://127.0.0.1:8080/"

(format/ensure-trailing-slash "http://127.0.0.1:8080/")
;; => "http://127.0.0.1:8080/"
```

#### <a name="clj->json"></a>`clj->json []`
```clojure
(format/clj->json {:title "PepeSmile"
                   :image-hash "data"
                   :search-tags "pepe frog dank"})
;; => "{\"title\":\"PepeSmile\",\"image-hash\":\"data\",\"search-tags\":\"pepe frog dank\"}"
```

## district.format.mount
With mount you can setup all global configs at once under the key `:format`. 

```clojure
  (ns my-district.core
    (:require [mount.core :as mount]
              [district.format.mount]))
              
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
```


## Development
```bash
lein deps
# To run tests and rerun on changes
lein doo chrome tests
```
