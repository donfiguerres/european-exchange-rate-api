package com.europeanexchangerates.exchangeapi.service;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.europeanexchangerates.exchangeapi.dto.ExchangeRate;
import com.europeanexchangerates.exchangeapi.util.UrlCsvZipDataDownloader;

@Service
public class ExchangeRateService {
    private Map<LocalDate, ExchangeRate> exchangeRates;

    public ExchangeRateService() {
        // this.data = loadCSVData();
    }

    @PostConstruct
    public void init() throws Exception {
        this.exchangeRates = loadCSVData();
    }

    private Map<LocalDate, ExchangeRate> loadCSVData() throws Exception {
        // Use WebClient to download the CSV file and CSVReader to parse it.
        // Return a map of dates to exchange rate objects.
        UrlCsvZipDataDownloader downloader = new UrlCsvZipDataDownloader();
        return downloader.downloadData();
    }

    public ExchangeRate getRatesForDate(LocalDate date) {
        // Return exchange rate data for the given date
        return exchangeRates.get(date);
    }

    public BigDecimal convertCurrency(LocalDate date, String source, String target, BigDecimal amount) {
        // Convert the amount from source to target currency
        return null;
    }

    public BigDecimal getHighestRate(LocalDate startDate, LocalDate endDate, String currency) {
        // Get the highest rate for the currency in the date range
        return null;
    }

    public BigDecimal getAverageRate(LocalDate startDate, LocalDate endDate, String currency) {
        // Get the average rate for the currency in the date range
        return null;
    }
}
