package com.europeanexchangerates.exchangeapi.provider;

import java.time.LocalDate;
import java.util.TreeMap;

import com.europeanexchangerates.exchangeapi.dto.ExchangeRate;
import com.europeanexchangerates.exchangeapi.util.DataDownloader;
import com.europeanexchangerates.exchangeapi.util.UrlCsvZipDataDownloader;

public class UrlCsvZipExchangeRateProvider implements ExchangeRateProvider {
    private DataDownloader downloader;

    public UrlCsvZipExchangeRateProvider() {
        this.downloader = new UrlCsvZipDataDownloader();
    }

    public UrlCsvZipExchangeRateProvider(DataDownloader downloader) {
        this.downloader = downloader;
    }

    public TreeMap<LocalDate, ExchangeRate> getExchangeRates() throws Exception {
        return downloader.downloadData();
    }
}
