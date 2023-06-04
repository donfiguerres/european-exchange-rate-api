package com.europeanexchangerates.exchangeapi.util.dataparser;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.TreeMap;

import com.europeanexchangerates.exchangeapi.dto.ExchangeRate;

public interface DataParser {
    public TreeMap<LocalDate, ExchangeRate> parseData(InputStream inputStream) throws Exception;
}
