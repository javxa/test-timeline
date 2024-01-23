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

    /**
     * The following test method will run once for every combination of divergences
     */
    @Test
    void test_DinnerPreparations() {
        if (Timeline.begin(this::test_DinnerPreparations)) return;

        if (Timeline.diverge()) {
            log.info("Decide to order food");

            if (Timeline.diverge()) {
                log.info("Order with Foodora");
            } else {
                log.info("Order with Uber eats");
            }

        } else {
            log.info("Buy Groceries");
            log.info("Prepare food");
        }

        if (Timeline.diverge()) {
            log.info("Eat slowly..");
        } else if (Timeline.diverge()) {
            log.info("Eat in normal speed");
        } else if (Timeline.diverge()) {
            log.info("Eat fast");
        } else {
            log.info("Gulp that shit");
        }

        if (Timeline.diverge()) {
            log.info("Do dishes");
        } else {
            log.info("Skip dishes");
        }

        if (Timeline.diverge()) {
            log.info("Go to bed early");
        } else {
            log.info("Stay up late");
        }
    }

}
