(ns yql-finance.common
  (:require [clojure.string :as str]
            [clojure.pprint :refer :all]
            [clj-http.client :as client]
            [cemerick.url :refer (url url-encode)]
            )
  (:import (java.util Date Collection)
           (java.text SimpleDateFormat)
           (clojure.lang Keyword Symbol)))

(def yql-uri "http://query.yahooapis.com/v1/public/yql")

(defmulti format-param (fn [val] (class val)))

(defmethod format-param String [val] (str "\"" val "\""))

(defmethod format-param Keyword [val] (format-param (name val)))

(defmethod format-param Symbol [val] (format-param (name val)))

(defmethod format-param Date [val]
  (str "\"" (.format (SimpleDateFormat. "yyyy-MM-dd") val) "\""))

(defmethod format-param Collection [val]
  (str "("
    (->> val
     (map #(format-param %1))
     (interpose ",")
     (apply str))
    ")"))

(defn arg-replacement [params marker]
  (let [index (Integer/parseInt (subs marker 1))]
    (if (<= index (count params))
      (format-param (nth params (dec index)))
      (throw (IllegalArgumentException. (str "Missing parameter: %" index))))))

(defn build-query [query-string params]
  (str/replace query-string #"%\d" (partial arg-replacement params)))

; Use our own query string buider because url url-encodes queries which we don't want...
(defn- map->query
  [m]
  (some->> (seq m)
    (map (fn [[k v]]
           [(name k)
            "="
            (str v)]))
    (interpose "&")
    flatten
    (apply str)))

(defn build-url
  "Build and format a URL to obtain pricing data for tickers on date"
  [query params]
  (let [
        query-map {
                   :q      (build-query query params)
                   :env    "store://datatables.org/alltableswithkeys"
                   :format "json"
                   }]
    (-> (url yql-uri)
      (assoc :query (map->query query-map))
      str
      )))

(defn extract-quotes [response]
  (let [get-count (comp :count :query :body)
        get-quotes (comp :quote :results :query :body)]
    (condp = (get-count response)
     0 []
     1 [(get-quotes response)]
     (get-quotes response))))

(defn execute-query [query & params]
  (extract-quotes (client/get (build-url query params) {:as :json})))

