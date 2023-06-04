package com.europeanexchangerates.exchangeapi.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.TreeMap;

import org.springframework.stereotype.Service;

import com.europeanexchangerates.exchangeapi.dto.CurrencyAverageRate;
import com.europeanexchangerates.exchangeapi.dto.CurrencyConversion;
import com.europeanexchangerates.exchangeapi.dto.CurrencyHighestRate;
import com.europeanexchangerates.exchangeapi.dto.ExchangeRate;
import com.europeanexchangerates.exchangeapi.exception.InvalidDateRangeException;
import com.europeanexchangerates.exchangeapi.provider.ExchangeRateProvider;
import com.europeanexchangerates.exchangeapi.provider.UrlCsvZipExchangeRateProvider;

@Service
public class ExchangeRateService {

    // A TreeMap is used to store the exchange rates in chronological order.
    // The data is currently only loaded into memory but can be easily
    // replaced with a database or any other data store.
    private TreeMap<LocalDate, ExchangeRate> exchangeRates;

    public ExchangeRateService() throws Exception {
        ExchangeRateProvider provider = new UrlCsvZipExchangeRateProvider();
        this.exchangeRates = provider.getExchangeRates();
    }

    public ExchangeRateService(ExchangeRateProvider provider) throws Exception {
        this.exchangeRates = provider.getExchangeRates();
    }

    /**
     * Get all the exchange rates for a given date.
     * 
     * @param date date to get exchange rates for
     * 
     * @return exchange rates
     */
    public Optional<ExchangeRate> getRatesForDate(LocalDate date) {
        return Optional.ofNullable(exchangeRates.get(date));
    }

    /**
     * Convert the amount from source to target currency.
     * 
     * The results are rounded using the RoundingMode.HALF_UP policy.
     * 
     * @param date   date to get exchange rates for
     * @param source source currency code
     * @param target target currency code
     * @param amount amount to convert
     * 
     * @return converted amount
     */
    public Optional<CurrencyConversion> convertCurrency(LocalDate date, String source,
            String target, BigDecimal amount) {
        Optional<ExchangeRate> exchangeRateForDate = Optional.ofNullable(exchangeRates.get(date));
        return exchangeRateForDate.flatMap(exchangeRate -> {
            Optional<BigDecimal> optionalSourceRate = Optional.ofNullable(exchangeRate.getRates().get(source));
            Optional<BigDecimal> optionalTargetRate = Optional.ofNullable(exchangeRate.getRates().get(target));

            if (optionalSourceRate.isPresent() && optionalTargetRate.isPresent()) {
                BigDecimal sourceRate = optionalSourceRate.get();
                BigDecimal targetRate = optionalTargetRate.get();
                BigDecimal convertedValue = amount.multiply(targetRate)
                        .divide(sourceRate, 2, RoundingMode.HALF_UP);
                return Optional.of(
                        new CurrencyConversion(source, target, amount, date, convertedValue));
            } else {
                return Optional.empty();
            }
        });
    }

    /**
     * Get the highest rate for the currency in the date range.
     * 
     * @param startDate start date of the date range
     * @param endDate   end date of the date range
     * @param currency  currency code to get the highest rate for
     * @return highest rate
     */
    public CurrencyHighestRate getHighestRate(LocalDate startDate,
            LocalDate endDate, String currency) {
        if (endDate.isBefore(startDate)) {
            throw new InvalidDateRangeException("End date cannot be before start date.");
        }
        Optional<BigDecimal> maxRate = exchangeRates
                .subMap(startDate, true, endDate, true).values().stream()
                .map(exchangeRate -> exchangeRate.getRates().get(currency))
                .filter(Objects::nonNull)
                .max(BigDecimal::compareTo);
        BigDecimal highestRate = maxRate.orElse(null);
        return highestRate == null
                ? null
                : new CurrencyHighestRate(currency, startDate, endDate, highestRate);
    }

    /**
     * Get the average rate for the currency in the date range
     * 
     * @param startDate start date of the date range
     * @param endDate   end date of the date range
     * @param currency  currency code to get the highest rate for
     * @return average rate
     */
    public CurrencyAverageRate getAverageRate(LocalDate startDate,
            LocalDate endDate, String currency) {
        if (endDate.isBefore(startDate)) {
            throw new InvalidDateRangeException("End date cannot be before start date.");
        }

        OptionalDouble average = exchangeRates
                .subMap(startDate, true, endDate, true).values().stream()
                .map(exchangeRate -> exchangeRate.getRates().get(currency))
                .filter(Objects::nonNull)
                .mapToDouble(BigDecimal::doubleValue)
                .average();

        if (!average.isPresent())
            return null;

        BigDecimal averageRate = BigDecimal.valueOf(average.getAsDouble())
                .setScale(2, RoundingMode.HALF_UP);
        return new CurrencyAverageRate(currency, startDate, endDate, averageRate);
    }
}
