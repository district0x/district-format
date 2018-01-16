(ns district.format.mount
  (:require
    [district.format]
    [mount.core :as mount :refer [defstate]]))

(declare start)

(defstate format :start (start (:format (mount/args))))

(defn start [{:keys [:default-datetime-formatter
                     :default-local-datetime-formatter
                     :default-local-date-formatter
                     :default-date-formatter
                     :default-locale
                     :default-max-number-fraction-digits
                     :default-max-currency-fraction-digits
                     :default-min-currency-fraction-digits
                     :default-max-token-fraction-digits
                     :default-min-token-fraction-digits]}]
  (when default-datetime-formatter
    (set! district.format/*default-datetime-formatter* default-datetime-formatter))
  (when default-local-datetime-formatter
    (set! district.format/*default-local-datetime-formatter* default-local-datetime-formatter))
  (when default-local-date-formatter
    (set! district.format/*default-local-date-formatter* default-local-date-formatter))
  (when default-date-formatter
    (set! district.format/*default-date-formatter* default-date-formatter))
  (when default-locale
    (set! district.format/*default-locale* default-locale))
  (when default-max-number-fraction-digits
    (set! district.format/*default-max-number-fraction-digits* default-max-number-fraction-digits))
  (when default-max-currency-fraction-digits
    (set! district.format/*default-max-currency-fraction-digits* default-max-currency-fraction-digits))
  (when default-min-currency-fraction-digits
    (set! district.format/*default-min-currency-fraction-digits* default-min-currency-fraction-digits))
  (when default-max-token-fraction-digits
    (set! district.format/*default-max-token-fraction-digits* default-max-token-fraction-digits))
  (when default-min-token-fraction-digits
    (set! district.format/*default-min-token-fraction-digits* default-min-token-fraction-digits)))
