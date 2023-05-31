package com.europeanexchangerates.exchangeapi.util;

import java.time.LocalDate;
import java.util.TreeMap;

import com.europeanexchangerates.exchangeapi.dto.ExchangeRate;

public interface DataDownloader {
    public TreeMap<LocalDate,ExchangeRate> downloadData() throws Exception;
}
