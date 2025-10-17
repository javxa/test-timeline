package com.javxa.timeline

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.TestFactory

class TimelineTest {

    /**
     *               ,-- 0
     *            ,<
     *         ,<   `-- 1
     *    ---<   `-- 2
     *        `-- 3
     *
     */
    @TestFactory
    fun `Nested nested nested nested branch should work as intended`() = timeline {
        if (branch("branch1")) {
            if (branch("branch2")) {
                if (branch("branch3")) {
                    Assertions.assertEquals(0, timelineIndex)
                    return@timeline
                }
                Assertions.assertEquals(1, timelineIndex)
                return@timeline
            }
            Assertions.assertEquals(2, timelineIndex)
            return@timeline
        }
        Assertions.assertEquals(3, timelineIndex)
        return@timeline
    }

    /**
     *         ,-- 0
     *    ---<   ,-- 1
     *        `<   ,-- 2
     *          `<
     *            `-- 3
     */
    @TestFactory
    fun `Branch after branch after branch should work as intended`() = timeline {
        if (branch("branch1")) {
            Assertions.assertEquals(0, timelineIndex)
            return@timeline
        }
        if (branch("branch2")) {
            Assertions.assertEquals(1, timelineIndex)
            return@timeline
        }
        if (branch("branch3")) {
            Assertions.assertEquals(2, timelineIndex)
            return@timeline
        }
        Assertions.assertEquals(3, timelineIndex)
    }

    /**
     *           ,-- 0
     *         ,<
     *        /  `-- 1
     *    ---<
     *        \  ,-- 2
     *         `<
     *           `-- 3
     */
    @TestFactory
    fun `Nested branching should work as intended`() = timeline {
        Assertions.assertTrue(timelineIndex in 0..< 4)

        if (branch("outer")) {
            if (branch("inner1")) {
                Assertions.assertEquals(0, timelineIndex)
                Assertions.assertEquals("[...]", predictedPathName)
            } else {
                Assertions.assertEquals(1, timelineIndex)
                Assertions.assertEquals("outer: true, inner1: false [...]", predictedPathName)
            }
        } else {
            if (branch("inner2")) {
                Assertions.assertEquals(2, timelineIndex)
                Assertions.assertEquals("outer: false [...]", predictedPathName)
            } else {
                Assertions.assertEquals(3, timelineIndex)
                Assertions.assertEquals("outer: false, inner2: false [...]", predictedPathName)
            }
        }
    }

    /**
     *               ,-- h0
     *       w0   ,-<--- h1
     *          /    `-- h2
     *        /  w1  ,-- h0
     *    ---<------<--- h1
     *        \      `-- h2
     *          \    ,-- h0
     *       w2   `-<--- h1
     *               `-- h2
     */
    @TestFactory
    fun `Branch should work without if-statements`() = timeline {

        val width = branch("width", 0..< 3)
        val height = branch("height", 0..< 3)

        Assertions.assertEquals(timelineIndex, width * 3 + height)

        if (width == 0 && height == 0) {
            Assertions.assertEquals("[...]", predictedPathName)
        } else if (height == 0) {
            Assertions.assertEquals("width: $width [...]", predictedPathName)
        } else {
            Assertions.assertEquals("width: $width, height: $height [...]", predictedPathName)
        }
    }

    /**
     *        ,-- 0
     *    ---<--- 1
     *        `-- 2
     */
    @TestFactory
    fun `Branch should work with when()-statements`() = timeline {

        Assertions.assertTrue(timelineIndex in 0..2)

        when (branch("number", 0..2)) {
            0 -> {
                Assertions.assertEquals(0, timelineIndex)
                Assertions.assertEquals("[...]", predictedPathName)
            }
            1 -> {
                Assertions.assertEquals(1, timelineIndex)
                Assertions.assertEquals("number: 1 [...]", predictedPathName)
            }
            2 -> {
                Assertions.assertEquals(2, timelineIndex)
                Assertions.assertEquals("number: 2 [...]", predictedPathName)
            }
        }
    }

}