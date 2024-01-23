package com.javxa.timeline;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class TestCases {

    @Test
    void test_UserSelection() {
        if (Timeline.begin(this::test_UserSelection)) return;

        log.info("User creates an account");
        log.info("User logs in");
        log.info("User selects a language");

        if (Timeline.diverge()) {
            log.info("User selects Swedish");
        } else {
            log.info("User selects English");
        }

        log.info("User logs out");
    }

}
