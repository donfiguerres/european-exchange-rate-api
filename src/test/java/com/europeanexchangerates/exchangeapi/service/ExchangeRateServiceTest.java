package com.europeanexchangerates.exchangeapi.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
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
        Map<LocalDate, ExchangeRate> dummyData = new HashMap<>() {{
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
}
