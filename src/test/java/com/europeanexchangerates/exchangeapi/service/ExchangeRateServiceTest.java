package com.europeanexchangerates.exchangeapi.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

import com.europeanexchangerates.exchangeapi.dto.CurrencyAverageRate;
import com.europeanexchangerates.exchangeapi.dto.CurrencyConversion;
import com.europeanexchangerates.exchangeapi.dto.CurrencyHighestRate;
import com.europeanexchangerates.exchangeapi.dto.ExchangeRate;
import com.europeanexchangerates.exchangeapi.exception.InvalidDateRangeException;
import com.europeanexchangerates.exchangeapi.provider.UrlCsvZipExchangeRateProvider;
import com.europeanexchangerates.exchangeapi.util.NullableConverter;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ExchangeRateServiceTest {
    private ExchangeRateService exchangeRateService;

    @Mock
    UrlCsvZipExchangeRateProvider exchangeRateProvider;

    @BeforeEach
    void setUp() throws Exception {
        TreeMap<LocalDate, ExchangeRate> dummyData = new TreeMap<>() {
            {
                put(LocalDate.of(2023, 5, 30), new ExchangeRate(new HashMap<>() {
                    {
                        put("USD", BigDecimal.valueOf(1.0744));
                        put("JPY", BigDecimal.valueOf(150.01));
                        put("BGN", BigDecimal.valueOf(1.9558));
                        put("GBP", BigDecimal.valueOf(0.86365));
                    }
                }));
                put(LocalDate.of(2023, 5, 29), new ExchangeRate(new HashMap<>() {
                    {
                        put("USD", BigDecimal.valueOf(1.0715));
                        put("JPY", BigDecimal.valueOf(150.29));
                        put("BGN", BigDecimal.valueOf(1.9558));
                        put("GBP", BigDecimal.valueOf(0.86805));
                    }
                }));
                put(LocalDate.of(2023, 5, 26), new ExchangeRate(new HashMap<>() {
                    {
                        put("USD", BigDecimal.valueOf(1.0751));
                        put("JPY", BigDecimal.valueOf(150.24));
                        put("BGN", BigDecimal.valueOf(1.9558));
                        put("GBP", BigDecimal.valueOf(0.86813));
                    }
                }));
                put(LocalDate.of(2023, 5, 25), new ExchangeRate(new HashMap<>() {
                    {
                        put("USD", BigDecimal.valueOf(1.0735));
                        put("JPY", BigDecimal.valueOf(149.63));
                        put("BGN", BigDecimal.valueOf(1.9558));
                    }
                }));
                put(LocalDate.of(2023, 5, 24), new ExchangeRate(new HashMap<>() {
                    {
                        put("USD", BigDecimal.valueOf(1.0785));
                        put("JPY", BigDecimal.valueOf(149.3));
                        put("BGN", BigDecimal.valueOf(1.9558));
                        put("GBP", BigDecimal.valueOf(0.86993));
                    }
                }));
            }
        };
        when(exchangeRateProvider.getExchangeRates()).thenReturn(dummyData);

        exchangeRateService = new ExchangeRateService(exchangeRateProvider);
    }

    @ParameterizedTest
    @CsvSource({
            "2023-05-30, USD, 1.0744",
            "2023-05-30, JPY, 150.01",
            "2023-05-30, BGN, 1.9558",
            "2023-05-30, GBP, 0.86365",
            "1995-05-30, GBP, null",
    })
    void testGetRatesForDate(String dateString, String currency,
            @ConvertWith(NullableConverter.class) BigDecimal expected) {
        LocalDate inputDate = LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE);
        Optional<ExchangeRate> result = exchangeRateService.getRatesForDate(inputDate);
        if (expected == null) {
            assertEquals(Optional.empty(), result);
        } else {
            assertEquals(expected, result.get().getRates().get(currency));
        }
    }

    @ParameterizedTest
    @CsvSource({
            // rounded down
            "2023-05-30, USD, JPY, 100, 13962.21",
            // rounded up
            "2023-05-30, BGN, GBP, 100, 44.16",
            // target is missing
            "2023-05-30, BGN, EEK, 100, null",
            // source is missing
            "2023-05-30, EEK, BGN, 100, null",
            // both source and target are missing
            "2023-05-30, EEK, TRL, 100, null"
    })
    void testConvertCurrency(String dateString, String sourceCurrency,
            String targetCurrency, BigDecimal amount,
            @ConvertWith(NullableConverter.class) BigDecimal expected) {
        LocalDate inputDate = LocalDate
                .parse(dateString, DateTimeFormatter.ISO_DATE);
        Optional<CurrencyConversion> result = exchangeRateService
                .convertCurrency(inputDate, sourceCurrency, targetCurrency, amount);
        if (expected == null) {
            assertEquals(Optional.empty(), result);
        } else {
            assertEquals(expected, result.get().getConvertedAmount());
        }
    }

    @ParameterizedTest
    @CsvSource({
            // includes a weekend - should be processed properly
            "2023-05-26, 2023-05-30, USD, 1.0751",
            // includes days without data for the currency
            "2023-05-24, 2023-05-30, GBP, 0.86993",
            // no data for the currency
            "2023-05-24, 2023-05-30, EEK, null"
    })
    void testGetHighestRate(String startDateString, String endDateString,
            String currency,
            @ConvertWith(NullableConverter.class) BigDecimal expected) {
        LocalDate startDate = LocalDate
                .parse(startDateString, DateTimeFormatter.ISO_DATE);
        LocalDate endDate = LocalDate
                .parse(endDateString, DateTimeFormatter.ISO_DATE);
        CurrencyHighestRate result = exchangeRateService
                .getHighestRate(startDate, endDate, currency);
        if (expected == null) {
            assertNull(result);
        } else {
            assertEquals(expected, result.getHighestRate());
        }
    }

    @Test
    public void getHighestRate_endDateBeforeStartDate_throwsInvalidDateRangeException() {
        LocalDate startDate = LocalDate.of(2023, 5, 30);
        LocalDate endDate = LocalDate.of(2023, 5, 29);

        assertThrows(InvalidDateRangeException.class, () -> {
            exchangeRateService.getHighestRate(startDate, endDate, "USD");
        });
    }

    @ParameterizedTest
    @CsvSource({
            // includes a weekend - should be processed properly
            "2023-05-26, 2023-05-30, USD, 1.07",
            // includes days without data for the currency
            "2023-05-24, 2023-05-30, GBP, 0.87",
            // no data for the currency
            "2023-05-24, 2023-05-30, EEK, null"
    })
    void testGetAverageRate(String startDateString, String endDateString,
            String currency,
            @ConvertWith(NullableConverter.class) BigDecimal expected) {
        LocalDate startDate = LocalDate
                .parse(startDateString, DateTimeFormatter.ISO_DATE);
        LocalDate endDate = LocalDate
                .parse(endDateString, DateTimeFormatter.ISO_DATE);
        CurrencyAverageRate result = exchangeRateService
                .getAverageRate(startDate, endDate, currency);
        if (expected == null) {
            assertNull(result);
        } else {
            assertEquals(expected, result.getAverageRate());
        }
    }

    @Test
    public void getAverageRate_endDateBeforeStartDate_throwsInvalidDateRangeException() {
        LocalDate startDate = LocalDate.of(2023, 5, 30);
        LocalDate endDate = LocalDate.of(2023, 5, 29);

        assertThrows(InvalidDateRangeException.class, () -> {
            exchangeRateService.getAverageRate(startDate, endDate, "USD");
        });
    }
}
