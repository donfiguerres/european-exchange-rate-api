package com.europeanexchangerates.exchangeapi.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

import com.europeanexchangerates.exchangeapi.dto.ExchangeRate;
import com.europeanexchangerates.exchangeapi.util.DataDownloader;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ExchangeRateServiceTest {
    private ExchangeRateService exchangeRateService; 

    @Mock
    DataDownloader dataDownloader;
    
    @BeforeEach
    void setUp() throws Exception {
        TreeMap<LocalDate, ExchangeRate> dummyData = new TreeMap<>() {{
            put(LocalDate.of(2023, 5, 30), new ExchangeRate(new HashMap<>() {{
                put("USD", BigDecimal.valueOf(1.0744));
                put("JPY", BigDecimal.valueOf(150.01));
                put("BGN", BigDecimal.valueOf(1.9558));
                put("GBP", BigDecimal.valueOf(0.86365));
            }}));
            put(LocalDate.of(2023, 5, 29), new ExchangeRate(new HashMap<>() {{
                put("USD", BigDecimal.valueOf(1.0715));
                put("JPY", BigDecimal.valueOf(150.29));
                put("BGN", BigDecimal.valueOf(1.9558));
                put("GBP", BigDecimal.valueOf(0.86805));
            }}));
            put(LocalDate.of(2023, 5, 26), new ExchangeRate(new HashMap<>() {{
                put("USD", BigDecimal.valueOf(1.0751));
                put("JPY", BigDecimal.valueOf(150.24));
                put("BGN", BigDecimal.valueOf(1.9558));
                put("GBP", BigDecimal.valueOf(0.86813));
            }}));
            put(LocalDate.of(2023, 5, 25), new ExchangeRate(new HashMap<>() {{
                put("USD", BigDecimal.valueOf(1.0735));
                put("JPY", BigDecimal.valueOf(149.63));
                put("BGN", BigDecimal.valueOf(1.9558));
            }}));
            put(LocalDate.of(2023, 5, 24), new ExchangeRate(new HashMap<>() {{
                put("USD", BigDecimal.valueOf(1.0785));
                put("JPY", BigDecimal.valueOf(149.3));
                put("BGN", BigDecimal.valueOf(1.9558));
                put("GBP", BigDecimal.valueOf(0.86993));
            }}));
        }};
        when(dataDownloader.downloadData()).thenReturn(dummyData);

        exchangeRateService = new ExchangeRateService(dataDownloader);
    }

    @Test
    void testGetRatesForDate() {
        LocalDate inputDate = LocalDate.parse(
            "2023-05-30", DateTimeFormatter.ISO_DATE);
        Map<String, BigDecimal> rates = exchangeRateService
                                    .getRatesForDate(inputDate).getRates();
        assertEquals(BigDecimal.valueOf(1.0744), rates.get("USD"));
        assertEquals(BigDecimal.valueOf(150.01), rates.get("JPY"));
        assertEquals(BigDecimal.valueOf(1.9558), rates.get("BGN"));
        assertEquals(BigDecimal.valueOf(0.86365), rates.get("GBP"));

        // Missing
        assertEquals(null, rates.get("EEK"));
    }


    @Test
    void testConvertCurrency() {
        LocalDate inputDate = LocalDate.parse(
            "2023-05-30", DateTimeFormatter.ISO_DATE);
        // rounded down
        assertEquals(BigDecimal.valueOf(13962.21),
            exchangeRateService.convertCurrency(inputDate, "USD",
                                            "JPY", BigDecimal.valueOf(100))
        );
        // rounded up
        assertEquals(BigDecimal.valueOf(44.16),
            exchangeRateService.convertCurrency(inputDate, "BGN",
                                            "GBP", BigDecimal.valueOf(100))
        );
        // target is missing
        assertEquals(null,
            exchangeRateService.convertCurrency(inputDate, "BGN",
                                            "EEK", BigDecimal.valueOf(100))
        );
        // source is missing
        assertEquals(null,
            exchangeRateService.convertCurrency(inputDate, "EEK",
                                            "BGN", BigDecimal.valueOf(100))
        );
        // both source and target are missing
        assertEquals(null,
            exchangeRateService.convertCurrency(inputDate, "EEK",
                                            "TRL", BigDecimal.valueOf(100))
        );
    }

    @Test
    void testGetHighestRate() {
        // Includes a weekend. Should be processed properly.
        LocalDate startDate = LocalDate.parse(
            "2023-05-26", DateTimeFormatter.ISO_DATE);
        LocalDate endDate = LocalDate.parse(
            "2023-05-30", DateTimeFormatter.ISO_DATE);
        assertEquals(BigDecimal.valueOf(1.0751), 
            exchangeRateService.getHighestRate(startDate, endDate, "USD"));
    }


    @Test
    void testGetHighestRate_DaysWithoutData() {
        // Includes days without data for the currency.
        LocalDate startDate = LocalDate.parse(
            "2023-05-24", DateTimeFormatter.ISO_DATE);
        LocalDate endDate = LocalDate.parse(
            "2023-05-30", DateTimeFormatter.ISO_DATE);
        assertEquals(BigDecimal.valueOf(0.86993), 
            exchangeRateService.getHighestRate(startDate, endDate, "GBP"));
    }

    @Test
    void testGetHighestRate_NoDataForCurrency() {
        LocalDate startDate = LocalDate.parse(
            "2023-05-24", DateTimeFormatter.ISO_DATE);
        LocalDate endDate = LocalDate.parse(
            "2023-05-30", DateTimeFormatter.ISO_DATE);
        assertEquals(null, 
            exchangeRateService.getHighestRate(startDate, endDate, "EEK"));
    }

    @Test
    void testGetAverageRate() {
        // Includes a weekend. Should be processed properly.
        LocalDate startDate = LocalDate.parse(
            "2023-05-26", DateTimeFormatter.ISO_DATE);
        LocalDate endDate = LocalDate.parse(
            "2023-05-30", DateTimeFormatter.ISO_DATE);
        assertEquals(BigDecimal.valueOf(1.07), 
            exchangeRateService.getAverageRate(startDate, endDate, "USD"));
    }


    @Test
    void testGetAverageRate_DaysWithoutData() {
        // Includes days without data for the currency.
        LocalDate startDate = LocalDate.parse(
            "2023-05-24", DateTimeFormatter.ISO_DATE);
        LocalDate endDate = LocalDate.parse(
            "2023-05-30", DateTimeFormatter.ISO_DATE);
        assertEquals(BigDecimal.valueOf(0.87), 
            exchangeRateService.getAverageRate(startDate, endDate, "GBP"));
    }

    @Test
    void testGetAverageRate_NoDataForCurrency() {
        LocalDate startDate = LocalDate.parse(
            "2023-05-24", DateTimeFormatter.ISO_DATE);
        LocalDate endDate = LocalDate.parse(
            "2023-05-30", DateTimeFormatter.ISO_DATE);
        assertEquals(null, 
            exchangeRateService.getAverageRate(startDate, endDate, "EEK"));
    }
}
