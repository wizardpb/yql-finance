(ns yql-finance.core
  (:require [yql-finance.common :refer :all]
            [yql-finance.historical-data :as hd]
            [yql-finance.quote-data :as qd]))


(defn get-historical-data [tickers start-date end-date]
  (-> (execute-query
        "select * from yahoo.finance.historicaldata where symbol in %1 and startDate = %2 and endDate = %3"
        tickers start-date end-date)
    (hd/parse-response)
    ))

(defn get-quotes [tickers]
  (->> (execute-query
        "select * from yahoo.finance.quote where symbol in %1" tickers)
    (qd/parse-response)
    (map (fn [[t ql]] [t (first ql)]))
    flatten
    (apply hash-map)
    ))
