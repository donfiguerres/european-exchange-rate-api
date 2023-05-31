package com.europeanexchangerates.exchangeapi.util;

import java.time.LocalDate;
import java.util.Map;

import com.europeanexchangerates.exchangeapi.dto.ExchangeRate;

public interface DataDownloader {
    public Map<LocalDate,ExchangeRate> downloadData() throws Exception;
}
