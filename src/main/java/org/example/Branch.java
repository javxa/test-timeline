package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Branch {

    private static final Map<Thread, Branch> timelines = new HashMap<>();

    public static void begin(Runnable runnable) {
        Branch branch = timelines.get(Thread.currentThread());
        if (branch.runnable == runnable) {
            branch._begin();
            return;
        }
        branch = new Branch(runnable);
        timelines.put(Thread.currentThread(), branch);
        branch._begin();
    }

    public static boolean diverge() {
        Branch branch = timelines.get(Thread.currentThread());
        if (branch == null) throw new IllegalStateException("No timeline has been initialized");
        return branch._diverge();
    }


    // Instance variables

    private final Runnable runnable;

    private Branch(Runnable runnable) {
        this.runnable = Objects.requireNonNull(runnable);
    }


    private boolean _diverge() {

    }

    private void _begin() {

    }


}
