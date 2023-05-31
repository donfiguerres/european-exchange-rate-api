package com.europeanexchangerates.exchangeapi.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.europeanexchangerates.exchangeapi.dto.ExchangeRate;
import com.europeanexchangerates.exchangeapi.util.UrlCsvZipDataDownloader;
import com.europeanexchangerates.exchangeapi.util.DataDownloader;

@Service
public class ExchangeRateService {
    private Map<LocalDate, ExchangeRate> exchangeRates;

    public ExchangeRateService() throws Exception {
        DataDownloader downloader = new UrlCsvZipDataDownloader();
        this.exchangeRates = downloader.downloadData();
    }

    public ExchangeRateService(DataDownloader downloader) throws Exception {
        this.exchangeRates = downloader.downloadData();
    } 

    public ExchangeRate getRatesForDate(LocalDate date) {
        // Return exchange rate data for the given date
        return exchangeRates.get(date);
    }

    public BigDecimal convertCurrency(LocalDate date, String source, String target, BigDecimal amount) {
        // Convert the amount from source to target currency
        BigDecimal sourceRate = exchangeRates.get(date).getRates().get(source);
        BigDecimal targetRate = exchangeRates.get(date).getRates().get(target);
        return amount.multiply(sourceRate).divide(targetRate, 2, RoundingMode.HALF_UP);
    }

    public BigDecimal getHighestRate(LocalDate startDate, LocalDate endDate, String currency) {
        // Get the highest rate for the currency in the date range
        BigDecimal highestRate = BigDecimal.ZERO;
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            BigDecimal rate = exchangeRates.get(date).getRates().get(currency);
            if (rate.compareTo(highestRate) > 0) {
                highestRate = rate;
            }
        }
        return highestRate;
    }

    public BigDecimal getAverageRate(LocalDate startDate, LocalDate endDate, String currency) {
        // Get the average rate for the currency in the date range
        BigDecimal sum = BigDecimal.ZERO;
        int count = 0;
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            sum = sum.add(exchangeRates.get(date).getRates().get(currency));
            count++;
        }
        return sum.divide(new BigDecimal(count), 2, RoundingMode.HALF_UP);
    }
}
