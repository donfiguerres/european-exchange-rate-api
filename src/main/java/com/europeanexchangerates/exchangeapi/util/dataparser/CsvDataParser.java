package com.europeanexchangerates.exchangeapi.util.dataparser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.europeanexchangerates.exchangeapi.dto.ExchangeRate;

public class CsvDataParser implements DataParser {
    public TreeMap<LocalDate, ExchangeRate> parseData(InputStream inputStream) throws Exception {
        TreeMap<LocalDate, ExchangeRate> exchangeRates = new TreeMap<>();
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream));

        // Iterate over each entry in the zip file
        // Skip header line
        String[] headers = bufferedReader.readLine().split(",");
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String[] data = line.split(",");
            LocalDate date = LocalDate.parse(data[0]);

            Map<String, BigDecimal> rates = new HashMap<>();
            for (int i = 1; i < headers.length; i++) {
                if (!data[i].equals("N/A")) {
                    rates.put(headers[i], new BigDecimal(data[i]));
                }
            }
            exchangeRates.put(date, new ExchangeRate(rates));
        }

        return exchangeRates;
    }
}
