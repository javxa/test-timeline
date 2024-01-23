package com.javxa.timeline;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class Timeline {

    final Runnable runnable;
    final LinkedList<Boolean> foundDivergences = new LinkedList<>();
    ListIterator<Boolean> traveler;
    @Getter
    private int currentTimelineIndex = 0;

    boolean diverge() {
        if (traveler.hasNext()) {
            return traveler.next();
        }
        traveler.add(true);
        return true;
    }

    void start() {
        currentTimelineIndex = -1;
        do {
            currentTimelineIndex++;

            log.info("");
            log.info("Starting timeline " + (currentTimelineIndex + 1));

            traveler = foundDivergences.listIterator();
            runnable.run();
            prepare();
        } while(!foundDivergences.isEmpty());

        log.info("");
        log.info("All timelines explored" );
    }

    private void prepare() {
        while( !foundDivergences.isEmpty() && Objects.equals(false, foundDivergences.getLast())) {
            foundDivergences.removeLast();
        }

        if (foundDivergences.isEmpty()) return;

        foundDivergences.removeLast();
        foundDivergences.add(false);
    }

}
