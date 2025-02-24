package com.dgnl_backend.project.dgnl_backend.utils;

import java.util.regex.Pattern;

public class PatternMatching {
    public static boolean patternMatches(String emailAddress, String regexPattern) {
        return Pattern.compile(regexPattern)
          .matcher(emailAddress)
          .matches();
    }
}
