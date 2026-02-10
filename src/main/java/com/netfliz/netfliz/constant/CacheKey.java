package com.netfliz.netfliz.constant;

import java.util.Collection;

public class CacheKey {
    // Cache key
    public static final String CACHE_USER_INFO = "CACHE_USER_INFO";
    public static final String CACHE_CONFIG_ACTIVE = "CACHE_CONFIG_ACTIVE";
    public static final String CACHE_MOVIE_GENRES = "CACHE_MOVIE_GENRES";
    public static final String CACHE_MOVIE_BY_GENRES = "CACHE_MOVIE_BY_GENRES";
    public static final String CACHE_MOVIE_METADATA = "CACHE_MOVIE_METADATA";

    public static final String CACHE_PRESIGN_URL = "CACHE_PRESIGN_URL";

    // Movie
    public static final String CACHE_MOVIE = "movie";

    // Lock key
    public static final String LOCK_MOVIE = "lock:movie";

    // Cache time
    public static final Integer LOCK_MOVIE_TTL = 10;

    public static final Integer CACHE_FIVE_MINUTE = 60 * 5;
    public static final Integer CACHE_ONE_MINUTE = 60;
    public static final Integer CACHE_HAFT_HOUR = 60 * 30;
    public static final Integer CACHE_ONE_HOUR = 60 * 60;
    public static final Integer CACHE_ONE_DAY = 24 * 60 * 60;

    public static String buildKey(String... keys) {
        return String.join(":", keys);
    }

    public static String buildKey(Collection<String> keys) {
        return String.join(":", keys);
    }
}
