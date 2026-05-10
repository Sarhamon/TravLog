package com.travlog.travlog.common;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MoneyTest {

    @Test
    void null_returns_null() {
        assertThat(Money.format((Long) null)).isNull();
    }

    @Test
    void zero_renders_without_separator() {
        assertThat(Money.format(0L)).isEqualTo("0");
    }

    @Test
    void thousands_get_comma_separators() {
        assertThat(Money.format(1_000L)).isEqualTo("1,000");
        assertThat(Money.format(1_500_000L)).isEqualTo("1,500,000");
    }

    @Test
    void negative_values_keep_sign_before_separators() {
        assertThat(Money.format(-50_000L)).isEqualTo("-50,000");
    }

    @Test
    void primitive_overload_delegates_to_same_format() {
        assertThat(Money.format(1_234_567L)).isEqualTo(Money.format(1_234_567));
    }
}
