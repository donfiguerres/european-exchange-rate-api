package com.europeanexchangerates.exchangeapi.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipInputStream;

import com.europeanexchangerates.exchangeapi.dto.ExchangeRate;

public class UrlCsvZipDataDownloader {
    public Map<LocalDate, ExchangeRate> downloadData() throws Exception {
        String url = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist.zip";
        Map<LocalDate, ExchangeRate> exchangeRates = new HashMap<>();
        ZipInputStream zipInputStream = new ZipInputStream(
                (new URL(url)).openStream());
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(zipInputStream));

        // Iterate over each entry in the zip file
        while (zipInputStream.getNextEntry() != null) {
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
                exchangeRates.put(date, new ExchangeRate(date, rates));
            }
        }

        return exchangeRates;
    }
}
