package com.europeanexchangerates.exchangeapi.util.datadownloader;

import java.io.InputStream;
import java.net.URL;
import java.util.zip.ZipInputStream;

public class UrlCsvZipDataDownloader implements DataDownloader {
    public InputStream downloadData(String url) throws Exception {
        ZipInputStream zipInputStream = new ZipInputStream(
                (new URL(url)).openStream());
        return zipInputStream;
    }
}
