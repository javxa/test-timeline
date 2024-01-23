package com.javxa.timeline;

import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Timeline {

    private static final Map<Thread, TimelineTracker> timelines = new HashMap<>();

    public static boolean begin(Runnable runnable) {

        Thread currentThread = Thread.currentThread();

        TimelineTracker timelineTracker = timelines.get(currentThread);

        if (timelineTracker != null) {
            return false;
        }

        timelineTracker = new TimelineTracker(runnable);
        timelines.put(currentThread, timelineTracker);

        timelineTracker.start();

        timelines.remove(currentThread);
        return true;
    }

    private static TimelineTracker requireTimeline() {
        TimelineTracker timelineTracker = timelines.get(Thread.currentThread());
        if (timelineTracker == null) throw new IllegalStateException("No timeline has been initialized");
        return timelineTracker;
    }

    public static boolean diverge() {
        return requireTimeline().diverge();
    }

    public static boolean diverge(String branchName) {
        return diverge(branchName, null);
    }

    public static boolean diverge(String branchName, String elseName) {
        if (diverge()) {
            if (branchName != null) log.info("Branching into: " + branchName);
            return true;
        }

        if (elseName != null) log.info("Branching into: " + elseName);
        return false;
    }

    static int index() {
        return requireTimeline().getCurrentTimelineIndex();
    }

    public static <T extends Enum<T>> T forEachEnum(Class<T> clazz) {
        return forEach(clazz.getEnumConstants());
    }

    public static <T> T forEach(T[] values) {
        TimelineTracker timelineTracker = requireTimeline();

        for (int i = 0; i < values.length - 1; i++) {
            if (timelineTracker.diverge())
                return values[i];
        }

        return values[values.length - 1];
    }

}
