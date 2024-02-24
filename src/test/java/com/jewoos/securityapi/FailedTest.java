package com.jewoos.securityapi;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FailedTest {

    @Test
    @DisplayName("실패하는 테스트")
    void test() {
        assertEquals(1, 2);
    }
}
