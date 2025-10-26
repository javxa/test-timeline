package com.javxa.timeline

import org.junit.jupiter.api.DynamicTest
import java.util.*
import java.util.logging.Logger
import java.util.stream.Stream
import kotlin.reflect.KFunction1
import kotlin.streams.asStream


fun timeline(block: Timeline.() -> Unit): Stream<DynamicTest> {
    return TimelineTrackerImpl(block).asSequence().asStream()
}

interface Timeline {
    val timelineIndex: Int
    val branch: Boolean
    val predictedPathName: String

    fun branch(name: String? = null, notifySkipped: Boolean = true): Boolean
    fun <T: Any> branch(paths: Iterable<T>, pathName: KFunction1<T, Any?> = Any::toString ): T
    fun <T: Any> branch(name: String, paths: Iterable<T>, pathName: KFunction1<T, Any?> = Any::toString): T
}

class TimelineTrackerImpl(
    private val block: Timeline.() -> Unit
) : Timeline, Iterator<DynamicTest> {

    private val log: Logger = Logger.getLogger(Timeline::class.simpleName)

    override val branch: Boolean
        get() = branch(notifySkipped = true)

    override fun branch(name: String?, notifySkipped: Boolean): Boolean {
        val next = getNextBranch()
        if (next) {
            currentPathNames.add(BranchInfo(name = name, path = true, prediction = if (notifySkipped) false else null ))
        } else if (notifySkipped) {
            currentPathNames.add(BranchInfo(name = name, path = false, prediction = null))
        }
        return next
    }

    override fun <T: Any> branch(paths: Iterable<T>, pathName: KFunction1<T, Any?>): T {
        return branch("branch", paths, pathName)
    }

    override fun <T: Any> branch(name: String, paths: Iterable<T>, pathName: KFunction1<T, Any?>): T {
        val it = paths.iterator()

        if (!it.hasNext())
            throw IllegalStateException("Iterable did not contain any items")

        while (true) {
            val nextElement = it.next()

            if (!it.hasNext()) {
                currentPathNames.add(BranchInfo(name = name, path = pathName(nextElement), prediction = null))
                return nextElement
            }

            if (getNextBranch()) {
                val prediction = if (it.hasNext()) it.next() else null
                currentPathNames.add(BranchInfo(name = name, path = pathName(nextElement), prediction = prediction))
                return nextElement
            }
        }
    }

    override var timelineIndex = 0
        private set
    override var predictedPathName = "[...]"
        private set(value) { field = "$value [...]" }

    // For each traveler, the path is prepared as the one that should be followed.
    // When a traveler has reached the end, it is prepared for the next one.
    private val paths: MutableList<Boolean> = LinkedList()
    private val currentPathNames: MutableList<BranchInfo> = LinkedList()
    private var travelerIndex = 0

    private fun printCurrentPath() {
        log.info(StringBuilder("\n").apply {
            appendLine("======[ Timeline ]======")
            appendLine("Timeline Index: $timelineIndex")
            currentPathNames.forEach {
                appendLine(it)
            }
            appendLine("========================")
        }.toString())
    }

    private fun predictNextPath() = LinkedList<BranchInfo>().apply {
        var foundFirstPrediction = false
        currentPathNames.reversed().forEach {

            if (foundFirstPrediction) {
                addFirst(BranchInfo(name = it.name, path = it.path, prediction = null))
            } else if (it.prediction != null) {
                foundFirstPrediction = true
                addFirst(BranchInfo(name = it.name, path = it.prediction, prediction = null))
            }
        }
    }

    private fun prepareForNext() {
        // paths contain the previous travelled path
        // if the last branch is true, it should switch to false
        // if the last branch is false, we should remove all trailing false, then switch the last true to false

        printCurrentPath()

        while (paths.isNotEmpty() && !paths.last()) {
            paths.removeLast()
        }

        if (paths.isNotEmpty()) {
            paths.removeLast()
            paths.add(false)
        }

        val predictedPath = predictNextPath()
        predictedPathName = predictedPath.joinToString(", ")

        currentPathNames.clear()

    }

    private fun runNext() {
        travelerIndex = 0
        try {
            block()
        } finally {
            abortTraveler()
            prepareForNext()
            timelineIndex++
        }
    }

    private fun abortTraveler() {
        val size = paths.size
        val branchesTraveled = travelerIndex
        repeat(size - branchesTraveled) {
            paths.removeLast()
        }
    }

    private fun getNextBranch(): Boolean {

        val size = paths.size

        if (travelerIndex == size) {
            paths.add(true)
            travelerIndex++
            return true
        }

        if (travelerIndex > size) {
            throw IllegalStateException("Traveled beyond the event horizon")
        }

        val path = paths[travelerIndex]
        travelerIndex++
        return path
    }

    override fun hasNext(): Boolean {
        val hasRunOnce = timelineIndex != 0
        val hasPathsLeft = paths.size > 0
        return !hasRunOnce || hasPathsLeft
    }

    override fun next(): DynamicTest {
        return DynamicTest.dynamicTest(predictedPathName) {
            runNext()
        }
    }

    private data class BranchInfo(
        val name: String?,
        val path: Any?,
        val prediction: Any?,
    ) {
        override fun toString(): String {
            val branchName = name ?: "branch"
            return "$branchName: $path"
        }
    }
}
