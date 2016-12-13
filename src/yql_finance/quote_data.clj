(ns yql-finance.quote-data
  (:require [clojure.string :as str])
  (:import (java.text SimpleDateFormat)))

(defn- convert-suffixed [v]
  (if-let [matches (re-matches #"(\d+\.\d+)([MB])" v)]
    (let [[all n m] matches
          v (BigDecimal. n)]
      (condp = m
        "M" (* 1000000 v)
        "B" (* 1000000000 v)
        v))))

(defn- parse-ticker [quote-map]
  (into {}
    (map (fn [[k v]]
           [k (condp contains? k
                #{:Symbol} (keyword v)
                #{:Name} v
                #{:StockExchange} v
                #{:Change :Low :High :YearLow :YearHigh :LastTradePriceOnly :AverageDailyVolume :Volume
                  :DaysHigh :DaysLow} (BigDecimal. v)
                #{:MarketCapitalization} (convert-suffixed v)
                #{:DaysRange} (map #(BigDecimal. %1) (str/split v #" - "))
                )
            ])
      (dissoc quote-map :symbol))))

(defn parse-response
  "Take a map built from returned JSON data and parse into a map indexing closing prices by ticker"
  [json-quotes]
  (->> json-quotes
    (map parse-ticker)
    (group-by :Symbol)))
