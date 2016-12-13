(ns yql-finance.historical-data
  (:import (java.text SimpleDateFormat)))

(defn- parse-ticker [quote-map]
  (into {}
    (map (fn [[k v]]
          [k (condp = k
               :Symbol (keyword v)
               :Date (.parse (SimpleDateFormat. "yyyy-MM-dd") v)
               (BigDecimal. v))
           ])
     quote-map)))

(defn parse-response
  "Take a map built from returned JSON data and parse into a map indexing closing prices by ticker"
  [json-quotes]
  (->> json-quotes
    (map parse-ticker)
    (group-by :Symbol)))
