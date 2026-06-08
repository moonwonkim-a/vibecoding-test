package com.example.library.common.util;

import com.example.library.common.exception.BusinessException;
import com.example.library.common.exception.ErrorCode;

import java.util.regex.Pattern;

public final class UserCode7Util {

    private static final Pattern USER_CODE7_PATTERN = Pattern.compile("\\d{7}");

    private UserCode7Util() {
    }

    public static void validate(String userCode7) {
        if (userCode7 == null || !USER_CODE7_PATTERN.matcher(userCode7).matches()) {
            throw BusinessException.of(ErrorCode.EX_014);
        }
    }

    public static String mask(String code) {
        if (code == null || code.length() < 4) {
            return "***";
        }
        return code.substring(0, 4) + "***";
    }

    public static String extractPrefixFromMasked(String masked) {
        if (masked == null || masked.length() < 4) {
            throw BusinessException.of(ErrorCode.EX_005);
        }
        return masked.substring(0, 4);
    }
}
