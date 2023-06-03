package com.europeanexchangerates.exchangeapi.util;

import java.math.BigDecimal;

import org.junit.jupiter.params.converter.SimpleArgumentConverter;

public class NullableConverter extends SimpleArgumentConverter {
    @Override
    protected Object convert(Object source, Class<?> targetType) {
        String sourceString = (String) source;
        if ("null".equals(sourceString)) {
            return null;
        }
        return new BigDecimal(sourceString);
    }
}
