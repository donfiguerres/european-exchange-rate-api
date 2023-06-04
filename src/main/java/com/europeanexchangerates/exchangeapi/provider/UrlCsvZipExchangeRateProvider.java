package com.europeanexchangerates.exchangeapi.provider;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.TreeMap;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.europeanexchangerates.exchangeapi.dto.ExchangeRate;
import com.europeanexchangerates.exchangeapi.exception.NoDataFromSource;
import com.europeanexchangerates.exchangeapi.util.datadownloader.DataDownloader;
import com.europeanexchangerates.exchangeapi.util.datadownloader.UrlCsvZipDataDownloader;
import com.europeanexchangerates.exchangeapi.util.dataparser.CsvDataParser;
import com.europeanexchangerates.exchangeapi.util.dataparser.DataParser;

public class UrlCsvZipExchangeRateProvider implements ExchangeRateProvider {
    private DataDownloader downloader;
    private DataParser parser;
    private static final Logger LOGGER = LoggerFactory.getLogger(UrlCsvZipExchangeRateProvider.class);

    public UrlCsvZipExchangeRateProvider() {
        this.downloader = new UrlCsvZipDataDownloader();
        this.parser = new CsvDataParser();
    }

    public UrlCsvZipExchangeRateProvider(DataDownloader downloader, DataParser parser) {
        this.downloader = downloader;
        this.parser = parser;
    }

    public TreeMap<LocalDate, ExchangeRate> getExchangeRates() throws Exception {
        InputStream data = downloader.downloadData("https://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist.zip");

        // Only one CSV file is expected from the ZIP file.
        if (((ZipInputStream) data).getNextEntry() == null) {
            throw new NoDataFromSource("No files found from the zip file");
        }
        TreeMap<LocalDate, ExchangeRate> exchangeRates = parser.parseData(data);

        // If the zip file has changed, skip the next files but log a warning.
        // Ideally, alerts should be sent if running in a production environment.
        if (((ZipInputStream) data).getNextEntry() != null) {
            LOGGER.warn("The contents of the zip archive has changed. Please check the data source.");
        }

        return exchangeRates;
    }
}
