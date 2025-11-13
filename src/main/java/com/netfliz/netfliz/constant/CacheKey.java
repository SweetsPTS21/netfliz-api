package com.netfliz.netfliz.constant;

public class CacheKey {
    // Cache key
    public static final String CACHE_USER_INFO = "CACHE_USER_INFO";
    public static final String CACHE_CONFIG_ACTIVE = "CACHE_CONFIG_ACTIVE";

    // Cache time
    public static final Integer CACHE_ONE_MINUTE = 60;
    public static final Integer CACHE_HAFT_HOUR = 60 * 30;
    public static final Integer CACHE_ONE_HOUR = 60 * 60;
    public static final Integer CACHE_ONE_DAY = 24 * 60 * 60;

    public static String buildKey(String... keys) {
        return String.join("_", keys);
    }
}
