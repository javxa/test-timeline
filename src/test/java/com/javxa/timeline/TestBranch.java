package com.javxa.timeline;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class TestBranch {

    @Test
    void runOnce() {
        if (Branch.begin(this::runOnce)) return;
        assertEquals(0, Branch.index());
    }

    @Test
    void testBranch() {
        if (Branch.begin(this::testBranch)) return;

        if (Branch.diverge("First", "Second")) {
            assertEquals(0, Branch.index());
        } else {
            assertEquals(1, Branch.index());
        }
    }

    @Test
    void test_Permutations() {
        if (Branch.begin(this::test_Permutations)) return;
        int index = Branch.index();

        if (Branch.diverge("First choice 1", "First choice 2")) {
            assertTrue(index == 0 || index == 1);
        } else {
            assertTrue(index == 2 || index == 3);
        }

        if (Branch.diverge("Second choice 1", "Second choice 2")) {
            assertTrue(index == 0 || index == 2);
        } else {
            assertTrue(index == 1 || index == 3);
        }
    }

    @Test
    void test_ElseIfChain() {
        if (Branch.begin(this::test_ElseIfChain)) return;

        if (Branch.diverge()) {
            assertEquals(0, Branch.index());
        } else if (Branch.diverge()) {
            assertEquals(1, Branch.index());
        } else if (Branch.diverge()) {
            assertEquals(2, Branch.index());
        } else {
            assertEquals(3, Branch.index());
        }
    }

    @Test
    void test_ForEachList() {
        if (Branch.begin(this::test_ForEachList)) return;

        int i = Branch.forEach(new Integer[] { 0, 1, 2 });
        assertEquals(i, Branch.index());

    }

    @Test
    void testEnum() {
        if (Branch.begin(this::testEnum)) return;

        TestEnum test = Branch.forEachEnum(TestEnum.class);
        assertEquals(test.ordinal(), Branch.index());
    }



    public enum TestEnum {
        One,
        Two,
        Three,
        Four,
        Five
    }
}
