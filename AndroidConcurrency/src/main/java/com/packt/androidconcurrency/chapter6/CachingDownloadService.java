package com.packt.androidconcurrency.chapter6;

public class CachingDownloadService extends DownloadService {

    @Override
    protected Cache initCache() {
        return new CacheDirCache(getApplicationContext());
    }
}
