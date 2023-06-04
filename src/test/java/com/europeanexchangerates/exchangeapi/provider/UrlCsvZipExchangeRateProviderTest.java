package com.europeanexchangerates.exchangeapi.provider;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.europeanexchangerates.exchangeapi.dto.ExchangeRate;
import com.europeanexchangerates.exchangeapi.exception.NoDataFromSource;
import com.europeanexchangerates.exchangeapi.util.datadownloader.DataDownloader;
import com.europeanexchangerates.exchangeapi.util.dataparser.DataParser;

@ExtendWith(MockitoExtension.class)
public class UrlCsvZipExchangeRateProviderTest {
    private ListAppender<ILoggingEvent> listAppender;
    private Logger logger;

    @Mock
    DataDownloader dataDownloader;

    @Mock
    DataParser dataParser;

    @InjectMocks
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
            }
        };

        // when(dataParser.parseData(any())).thenReturn(dummyData);

        // Get the Logger for the class under test.
        logger = (Logger) LoggerFactory.getLogger(UrlCsvZipExchangeRateProvider.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @Test
    void getExchangeRates_withValidData_returnsNonEmptyMap() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        zos.putNextEntry(new ZipEntry("file1.csv"));
        zos.closeEntry();
        zos.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ZipInputStream zis = new ZipInputStream(bais);

        TreeMap<LocalDate, ExchangeRate> expectedRates = new TreeMap<>();
        expectedRates.put(LocalDate.now(), new ExchangeRate(null));

        when(dataDownloader.downloadData(anyString())).thenReturn(zis);
        when(dataParser.parseData(any())).thenReturn(expectedRates);

        TreeMap<LocalDate, ExchangeRate> resultRates = exchangeRateProvider.getExchangeRates();

        assertEquals(expectedRates, resultRates);
    }

    @Test
    void getExchangeRates_withEmptyData_throwsException() throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(new byte[0]);
        BufferedInputStream bis = new BufferedInputStream(bais);
        ZipInputStream zis = new ZipInputStream(bis);

        when(dataDownloader.downloadData(anyString())).thenReturn(zis);

        assertThrows(NoDataFromSource.class, () -> exchangeRateProvider.getExchangeRates());
    }

    @Test
    void getExchangeRates_multipleFilesInZip_logWarning() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        zos.putNextEntry(new ZipEntry("file1.csv"));
        zos.closeEntry();
        zos.putNextEntry(new ZipEntry("file2.csv"));
        zos.closeEntry();
        zos.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ZipInputStream zis = new ZipInputStream(bais);

        TreeMap<LocalDate, ExchangeRate> expectedRates = new TreeMap<>();
        expectedRates.put(LocalDate.now(), new ExchangeRate(null));

        when(dataDownloader.downloadData(anyString())).thenReturn(zis);
        when(dataParser.parseData(any())).thenReturn(expectedRates);

        // Data should still be returned.
        TreeMap<LocalDate, ExchangeRate> resultRates = exchangeRateProvider.getExchangeRates();
        assertEquals(expectedRates, resultRates);

        // Ensure that the warning is logged as this is part of the specified
        // behavior for the class.
        List<ILoggingEvent> logsList = listAppender.list;
        assertFalse(logsList.isEmpty());
        assertEquals("The contents of the zip archive has changed. Please check the data source.",
                logsList.get(0).getFormattedMessage());
    }
}
