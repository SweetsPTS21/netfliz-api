package com.netfliz.netfliz.util;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

@Component
public class CommonUtils {
    public static String generateUsername(String email) {
        if (Strings.isBlank(email)) {
            return null;
        }
        return email.split("@")[0];
    }
}
