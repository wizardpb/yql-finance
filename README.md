# yql-finance

A Clojure library designed to ... well, that part is up to you.

## Usage

### Leinigen

Add this to your project.clj:

    [com.prajnainc/yql-finance "0.1.0-SNAPSHOT"]

Then

    (:require [yql-finance.core :as yq])

## API

The interface is simple, currently two functions to get historical and current quotes:

### Historical data

    (yq/get-historical-data tickers start-date end-date)

Tickers are seqence of String, Symbol or Keyword tickers, start and end dates can be Java Dates, or Strings in the form "yyyy-MM-dd" e.g. "2016-12-10"

This returns a Map keyed by ticker keyword, whose values are all quotes for that ticker, one for each date. Each quote is a Map with keywords keys

    [:Close :Open :High :Low :Symbol :Date :Volume :Adj_Close].

### Current Quote

    (yq/get-quotes tickers)

Tickers are sequence of String, Symbol or Keyword tickers

This returns a Map keyed by ticker keyword, whose values are current quotes for that ticker, one for each date. Each quote is a Map with keywords keys

    [:DaysHigh :MarketCapitalization :YearLow  :Volume :Change :StockExchange :DaysLow :YearHigh :LastTradePriceOnly :Name :Symbol :AverageDailyVolume :DaysRange]

## License

Copyright © 2016 Prajna Inc. All Rights Reserved

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
