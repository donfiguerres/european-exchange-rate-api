package com.europeanexchangerates.exchangeapi.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.TreeMap;

import org.springframework.stereotype.Service;

import com.europeanexchangerates.exchangeapi.dto.CurrencyConversion;
import com.europeanexchangerates.exchangeapi.dto.ExchangeRate;
import com.europeanexchangerates.exchangeapi.util.UrlCsvZipDataDownloader;
import com.europeanexchangerates.exchangeapi.util.DataDownloader;

@Service
public class ExchangeRateService {

    // A TreeMap is used to store the exchange rates in chronological order.
    private TreeMap<LocalDate, ExchangeRate> exchangeRates;

    public ExchangeRateService() throws Exception {
        DataDownloader downloader = new UrlCsvZipDataDownloader();
        this.exchangeRates = downloader.downloadData();
    }

    public ExchangeRateService(DataDownloader downloader) throws Exception {
        this.exchangeRates = downloader.downloadData();
    } 

    /**
     * Get all the exchange rates for a given date.
     * @param date date to get exchange rates for
     * 
     * @return exchange rates
     */
    public ExchangeRate getRatesForDate(LocalDate date) {
        // Return exchange rate data for the given date
        return exchangeRates.get(date);
    }

    /**
     * Convert the amount from source to target currency.
     * 
     * The results are rounded using the RoundingMode.HALF_UP policy.
     * 
     * @param date date to get exchange rates for
     * @param source source currency code
     * @param target target currency code
     * @param amount amount to convert
     * 
     * @return converted amount
     */
    public CurrencyConversion convertCurrency(LocalDate date, String source,
                                        String target, BigDecimal amount) {
        BigDecimal sourceRate = exchangeRates.get(date).getRates().get(source);
        BigDecimal targetRate = exchangeRates.get(date).getRates().get(target);
        if (sourceRate == null || targetRate == null)
            return null;
        BigDecimal convertedValue =  amount.multiply(targetRate)
            .divide(sourceRate, 2, RoundingMode.HALF_UP);

        return new CurrencyConversion(source, target, amount, date, convertedValue);
    }

    /**
     * Get the highest rate for the currency in the date range.
     * @param startDate start date of the date range
     * @param endDate end date of the date range
     * @param currency currency code to get the highest rate for
     * @return highest rate
     */
    public BigDecimal getHighestRate(LocalDate startDate,
                                        LocalDate endDate, String currency) {
        Optional<BigDecimal> maxRate = exchangeRates
                .subMap(startDate, true, endDate, true).values().stream()
                .map(exchangeRate -> exchangeRate.getRates().get(currency))
                .filter(Objects::nonNull)
                .max(BigDecimal::compareTo);
        return maxRate.orElse(null);
    }


    /**
     * Get the average rate for the currency in the date range
     * @param startDate start date of the date range
     * @param endDate end date of the date range
     * @param currency currency code to get the highest rate for
     * @return average rate
    */
    public BigDecimal getAverageRate(LocalDate startDate, LocalDate endDate, String currency) {
        OptionalDouble average = exchangeRates
                .subMap(startDate, true, endDate, true).values().stream()
                .map(exchangeRate -> exchangeRate.getRates().get(currency))
                .filter(Objects::nonNull)
                .mapToDouble(BigDecimal::doubleValue)
                .average();

        return average.isPresent()
                ? BigDecimal.valueOf(average.getAsDouble())
                        .setScale(2, RoundingMode.HALF_UP)
                : null;
    }
}
