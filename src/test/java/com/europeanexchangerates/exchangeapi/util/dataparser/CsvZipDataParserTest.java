package com.europeanexchangerates.exchangeapi.util.dataparser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.europeanexchangerates.exchangeapi.dto.ExchangeRate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

class CsvZipDataParserTest {

    @Mock
    InputStream inputStream;

    private CsvDataParser parser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        parser = new CsvDataParser();
    }

    @Test
    void parseData() throws Exception {
        String testInput = "Date,USD,EUR\n" +
                "2023-06-04,1.2100,0.8400\n" +
                "2023-06-03,1.2200,0.8500\n";
        InputStream inputStream = new ByteArrayInputStream(testInput.getBytes());

        TreeMap<LocalDate, ExchangeRate> result = parser.parseData(inputStream);

        assertEquals(new BigDecimal("1.2100"), result.get(LocalDate.of(2023, 6, 4)).getRates().get("USD"));
        assertEquals(new BigDecimal("0.8400"), result.get(LocalDate.of(2023, 6, 4)).getRates().get("EUR"));
        assertEquals(new BigDecimal("1.2200"), result.get(LocalDate.of(2023, 6, 3)).getRates().get("USD"));
        assertEquals(new BigDecimal("0.8500"), result.get(LocalDate.of(2023, 6, 3)).getRates().get("EUR"));
    }

    @Test
    void parseData_throwsExceptionForMalformedData() {
        String testInput = "Date,USD,EUR\n" +
                "2023-06-04,1.2100,\n" +
                "2023-06-03,1.2200,0.8500\n";
        InputStream inputStream = new ByteArrayInputStream(testInput.getBytes());

        assertThrows(Exception.class, () -> parser.parseData(inputStream));
    }
}
