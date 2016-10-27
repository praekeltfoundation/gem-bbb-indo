package com.rr.rgem.gem.service;

/**
 * Created by Wimpie Victor on 2016/10/26.
 */
public interface UrlStore {
    String loadUrl();
    String loadUrl(String defaultUrl);
    void saveUrl(String url);
}
