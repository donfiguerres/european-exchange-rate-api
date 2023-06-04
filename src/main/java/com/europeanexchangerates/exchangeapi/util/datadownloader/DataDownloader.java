package com.europeanexchangerates.exchangeapi.util.datadownloader;

import java.io.InputStream;

public interface DataDownloader {
    public InputStream downloadData(String url) throws Exception;
}
