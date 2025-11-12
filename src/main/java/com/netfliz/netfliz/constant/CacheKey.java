package com.netfliz.netfliz.constant;

public class CacheKey {
    public static final String CACHE_USER_INFO = "CACHE_USER_INFO";

    public static String buildKey(String... keys) {
        return String.join("_", keys);
    }
}
