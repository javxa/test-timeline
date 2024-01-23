package com.javxa.timeline;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class TestTimeline {

    @Test
    void runOnce() {
        if (Timeline.begin(this::runOnce)) return;
        assertEquals(0, Timeline.index());
    }

    @Test
    void testBranch() {
        if (Timeline.begin(this::testBranch)) return;

        if (Timeline.diverge("First", "Second")) {
            assertEquals(0, Timeline.index());
        } else {
            assertEquals(1, Timeline.index());
        }
    }

    @Test
    void test_Permutations() {
        if (Timeline.begin(this::test_Permutations)) return;
        int index = Timeline.index();

        if (Timeline.diverge("First choice 1", "First choice 2")) {
            assertTrue(index == 0 || index == 1);
        } else {
            assertTrue(index == 2 || index == 3);
        }

        if (Timeline.diverge("Second choice 1", "Second choice 2")) {
            assertTrue(index == 0 || index == 2);
        } else {
            assertTrue(index == 1 || index == 3);
        }
    }

    @Test
    void test_ElseIfChain() {
        if (Timeline.begin(this::test_ElseIfChain)) return;

        if (Timeline.diverge()) {
            assertEquals(0, Timeline.index());
        } else if (Timeline.diverge()) {
            assertEquals(1, Timeline.index());
        } else if (Timeline.diverge()) {
            assertEquals(2, Timeline.index());
        } else {
            assertEquals(3, Timeline.index());
        }
    }

    @Test
    void test_ForEachList() {
        if (Timeline.begin(this::test_ForEachList)) return;

        int i = Timeline.forEach(new Integer[] { 0, 1, 2 });
        assertEquals(i, Timeline.index());

    }

    @Test
    void testEnum() {
        if (Timeline.begin(this::testEnum)) return;

        TestEnum test = Timeline.forEachEnum(TestEnum.class);
        assertEquals(test.ordinal(), Timeline.index());
    }



    public enum TestEnum {
        One,
        Two,
        Three,
        Four,
        Five
    }
}
