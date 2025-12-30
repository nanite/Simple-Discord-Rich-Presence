package com.sunekaer.sdrp.config;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DimensionEntryTest {
    Map<String, String> matcherTests = Map.of(
            "exact@mymod:custom_dimension", "mymod:custom_dimension",
            "contains@custom", "mymod:custom_dimension",
            "startsWith@mymod:", "mymod:custom_dimension",
            "endsWith@:custom_dimension", "mymod:custom_dimension",
            "regex@my.*:custom_dimension", "mymod:custom_dimension",
            "namespace@mymod", "mymod:custom_dimension",
            "path@custom_dimension", "mymod:custom_dimension"
    );

    @Test
    public  void testMatches() {
        for (Map.Entry<String, String> testCase : matcherTests.entrySet()) {
            String matcher = testCase.getKey();
            String compareTo = testCase.getValue();
            SDRPConfig.DimensionEntry dimensionEntry = new SDRPConfig.DimensionEntry(matcher, "Test Dimension", "Testing dimension match", null);
            boolean result = dimensionEntry.matches(compareTo);
            assertTrue(result, "Matcher '" + matcher + "' should match '" + compareTo + "'");
        }
    }
}
