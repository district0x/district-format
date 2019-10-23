(ns district.format
  (:require
    [cemerick.url :as url]
    [cljs-time.core :as t]
    [cljs-time.format :as time-format]
    [clojure.string :as string]))


(def ^:dynamic *default-datetime-formatter* :rfc822)

(def ^:dynamic *default-local-datetime-formatter* *default-datetime-formatter*)

(def ^:dynamic *default-date-formatter* "EEE, dd MMM yyyy")

(def ^:dynamic *default-local-date-formatter* *default-date-formatter*)

(def ^:dynamic *default-locale* "en-US")

(def ^:dynamic *default-max-number-fraction-digits* 2)

(def ^:dynamic *default-max-currency-fraction-digits* 2)

(def ^:dynamic *default-min-currency-fraction-digits* nil)

(def ^:dynamic *default-max-token-fraction-digits* 2)

(def ^:dynamic *default-min-token-fraction-digits* nil)

(declare ensure-trailing-slash)

(defn- get-formatter [fmt]
  (if (keyword? fmt)
    (time-format/formatters fmt)
    (time-format/formatter fmt)))

(defn format-datetime
  ([date]
   (format-datetime date *default-datetime-formatter*))
  ([date formatter]
   (when date
     (time-format/unparse (get-formatter formatter) date))))


(defn format-local-datetime
  ([date]
   (format-local-datetime date *default-local-datetime-formatter*))
  ([date formatter]
   (when date
     (time-format/unparse-local (get-formatter formatter) date))))


(defn format-date
  ([date]
   (format-date date *default-date-formatter*))
  ([date formatter]
   (when date
     (time-format/unparse (get-formatter formatter) date))))


(defn format-local-date
  ([date]
   (format-local-date date *default-local-date-formatter*))
  ([date formatter]
   (when date
     (time-format/unparse-local (get-formatter formatter) date))))

(defn format-number [x & [{:keys [:locale :max-fraction-digits :min-fraction-digits]
                           :or {locale *default-locale*
                                max-fraction-digits *default-max-number-fraction-digits*}}]]
  (when x
    (.toLocaleString x locale #js {:maximumFractionDigits max-fraction-digits
                                   :minimumFractionDigits min-fraction-digits})))


(defn format-currency [x & [{:keys [:currency :locale :max-fraction-digits :min-fraction-digits]
                             :or {locale *default-locale*
                                  max-fraction-digits *default-max-currency-fraction-digits*
                                  min-fraction-digits *default-min-currency-fraction-digits*}}]]
  (when x
    (.toLocaleString x locale #js {:maximumFractionDigits max-fraction-digits
                                   :minimumFractionDigits min-fraction-digits
                                   :style "currency"
                                   :currency currency})))


(defn format-token
  [x & [{:keys [:token :locale :max-fraction-digits :min-fraction-digits]
         :or {locale *default-locale*
              max-fraction-digits *default-max-token-fraction-digits*
              min-fraction-digits *default-min-token-fraction-digits*}}]]
  (when x
    (str (.toLocaleString x locale #js {:maximumFractionDigits max-fraction-digits
                                        :minimumFractionDigits min-fraction-digits})
         (when token
           (str " " token)))))


(defn format-eth [x & [opts]]
  (format-token x (merge {:token "ETH"} opts)))


(defn format-dnt [x & [opts]]
  (format-token x (merge {:token "DNT"} opts)))


(defn format-number-metric [x & [opts]]
  (when x
    (cond
      (< x 1000) (format-number x opts)
      (< 999 x 1000000) (str (format-number (/ x 1000) opts) "K")
      (< 999999 x) (str (format-number (/ x 1000000) opts) "M"))))

(defn etherscan-addr-url
  ([address]
   (str "https://etherscan.io/address/" address))
  ([root-url address]
   (str (ensure-trailing-slash root-url) "address/" address)))

(defn etherscan-tx-url
  ([tx-hash]
   (str "https://etherscan.io/tx/" tx-hash))
  ([root-url tx-hash]
   (str (ensure-trailing-slash root-url) "tx/" tx-hash)))

(defn time-ago
  ([from-time]
   (time-ago from-time (t/now)))
  ([from-time to-time]
   (when to-time
     (let [units [{:name "second" :limit 60 :in-second 1}
                  {:name "minute" :limit 3600 :in-second 60}
                  {:name "hour" :limit 86400 :in-second 3600}
                  {:name "day" :limit 604800 :in-second 86400}
                  {:name "week" :limit 2629743 :in-second 604800}
                  {:name "month" :limit 31556926 :in-second 2629743}
                  {:name "year" :limit nil :in-second 31556926}]
           diff (t/in-seconds (t/interval from-time to-time))]
       (if (< diff 5)
         "just now"
         (let [unit (first (drop-while #(or (>= diff (:limit %))
                                            (not (:limit %)))
                                       units))]
           (-> (/ diff (:in-second unit))
             js/Math.floor
             int
             (#(str % " " (:name unit) (when (> % 1) "s") " ago")))))))))


(defn pluralize
  "Taken from reagent-utils
   pluralizes the word based on the number of items
   (util/pluralize [\"John\"] \"lad\")
   (util/pluralize [\"John\" \"James\"] \"lad\")
   (util/pluralize [\"Alice\"] \"lad\" \"y\" \"ies\")"
  [n & [word ending1 ending2 :as opts]]
  (let [plural (case (count opts)
                 1 "s"
                 2 ending1
                 3 ending2)
        singular (case (count opts)
                   (list 1 2) ""
                   3 ending1)]
    (str n " " word (if (== 1 n) singular plural))))


(defn truncate
  "Truncate a string with suffix (ellipsis by default) if it is
   longer than specified length."
  ([string length]
   (truncate string length "..."))
  ([string length suffix]
   (let [string-len (count string)
         suffix-len (count suffix)]
     (if (<= string-len length)
       string
       (str (subs string 0 (- length suffix-len)) suffix)))))


(defn format-bool [x]
  (if x "true" "false"))


(def time-unit->text {:days "day" :hours "hour" :minutes "minute" :seconds "second"})

(def time-unit->text-short {:days "day" :hours "hour" :minutes "min." :seconds "sec."})


(defn format-time-unit [unit amount & [{:keys [:short?]}]]
  (let [f (if short? time-unit->text-short time-unit->text)
        unit-text (f unit)]
    (if (and short? (string/ends-with? unit-text "."))
      (pluralize amount unit-text "")
      (pluralize amount unit-text))))


(defn format-time-units [{:keys [:seconds :minutes :hours :days]} & [opts]]
  ;; To ensure proper order
  (.slice
    (reduce (fn [acc [unit amount]]
              (if (pos? amount)
                (str acc (format-time-unit unit amount opts) " ")
                acc))
            ""
            [[:days days] [:hours hours] [:minutes minutes] [:seconds seconds]])
    0 -1))


(defn zero-time-units? [time-units]
  (every? zero? (vals time-units)))


(defn format-url [path query-map]
  (str path "?" (url/map->query query-map)))


(defn format-namespaced-kw [kw]
  (when kw
    (str (when-let [n (namespace kw)] (str n "/")) (name kw))))

(defn format-percentage [portion total & [format-opts]]
  (str (if (pos? total)
         (format-number (* (/ portion total) 100.0)
                        (merge
                          {:max-fraction-digits 1
                           :min-fraction-digits 0}
                          format-opts))
         0)
       "%"))

(defn ensure-trailing-slash [s]
  (str s
       (when-not (string/ends-with? s "/")
         "/")))

(defn clj->json
  [coll]
  (.stringify js/JSON (clj->js coll)))
