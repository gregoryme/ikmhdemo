package dev.kofe.ikmhdemo.service;

import org.springframework.stereotype.Service;

@Service
public class UrlCoder {

    private final char REPLACE_SYMBOL = '|';

    // URL coder
    public String urlCoder(String originalUrl) {
        String result = "";
        for (int i = 0; i < originalUrl.length(); i++) {
            result += (originalUrl.charAt(i) == '/') ? REPLACE_SYMBOL : originalUrl.charAt(i);
        }

        return result;
    }

    // URL decoder
    public String urlDeCoder(String codedUrl) {
        String result = "";
        for (int i = 0; i < codedUrl.length(); i++) {
            result += (codedUrl.charAt(i) == REPLACE_SYMBOL) ? '/' : codedUrl.charAt(i);
        }

        return result;
    }

}
