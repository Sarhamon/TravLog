package com.travlog.travlog.common;

import java.util.Locale;

public final class Money {

    private Money() {
    }

    public static String format(Long value) {
        return value == null ? null : String.format(Locale.ROOT, "%,d", value);
    }

    public static String format(long value) {
        return String.format(Locale.ROOT, "%,d", value);
    }
}
