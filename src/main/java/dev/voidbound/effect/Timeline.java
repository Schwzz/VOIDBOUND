package dev.voidbound.effect;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public final class Timeline {
    private final long duration;
    private final Consumer<EndReason> cleanup;
    private final Map<String, Phase> phases = new HashMap<>();
    private final List<Cue> cues = new ArrayList<>();
    private long elapsed = -1L;
    private int nextCue;
    private boolean started;
    private boolean advancing;
    private boolean finished;

    public Timeline(long duration, Consumer<EndReason> cleanup) {
        if (duration <= 0L) {
            throw new IllegalArgumentException("Timeline duration must be positive");
        }
        this.duration = duration;
        this.cleanup = Objects.requireNonNull(cleanup, "cleanup");
    }

    public Timeline phase(String name, long start, long end, Easing easing) {
        requireNotStarted();
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(easing, "easing");
        if (start < 0L || end <= start || end > duration) {
            throw new IllegalArgumentException("Invalid phase bounds");
        }
        if (phases.putIfAbsent(name, new Phase(start, end, easing)) != null) {
            throw new IllegalArgumentException("Duplicate phase: " + name);
        }
        return this;
    }

    public Timeline cue(long tick, Runnable action) {
        requireNotStarted();
        if (tick < 0L || tick > duration) {
            throw new IllegalArgumentException("Cue is outside the timeline");
        }
        cues.add(new Cue(tick, Objects.requireNonNull(action, "action")));
        return this;
    }

    public void advance(long elapsedTicks) {
        if (finished) {
            return;
        }
        if (advancing) {
            throw new IllegalStateException("A timeline cannot advance from its own cue");
        }
        if (elapsedTicks < 0L || elapsedTicks < elapsed) {
            throw new IllegalArgumentException("Elapsed ticks must be nonnegative and monotonic");
        }
        if (!started) {
            cues.sort(Comparator.comparingLong(Cue::tick));
            started = true;
        }
        elapsed = Math.min(elapsedTicks, duration);
        advancing = true;
        try {
            while (!finished && nextCue < cues.size() && cues.get(nextCue).tick() <= elapsed) {
                cues.get(nextCue++).action().run();
            }
            if (!finished && elapsed == duration) {
                finish(EndReason.COMPLETED);
            }
        } catch (RuntimeException | Error error) {
            if (!finished) {
                finish(EndReason.INTERRUPTED);
            }
            throw error;
        } finally {
            advancing = false;
        }
    }

    public void interrupt() {
        if (!finished) {
            finish(EndReason.INTERRUPTED);
        }
    }

    public boolean isFinished() {
        return finished;
    }

    public long duration() {
        return duration;
    }

    public long elapsedTicks() {
        return Math.max(0L, elapsed);
    }

    public double sampleTime(double partialTick) {
        if (!Double.isFinite(partialTick)) {
            throw new IllegalArgumentException("Partial tick must be finite");
        }
        double partial = Math.max(0.0, Math.min(1.0, partialTick));
        return Math.min(duration, elapsedTicks() + (finished ? 0.0 : partial));
    }

    public double phaseProgress(String name, double partialTick) {
        Phase phase = phases.get(Objects.requireNonNull(name, "name"));
        if (phase == null) {
            throw new IllegalArgumentException("Unknown phase: " + name);
        }
        double progress = (sampleTime(partialTick) - phase.start()) / (phase.end() - phase.start());
        return phase.easing().apply(progress);
    }

    private void requireNotStarted() {
        if (started || finished) {
            throw new IllegalStateException("Timeline is already started or finished");
        }
    }

    private void finish(EndReason reason) {
        finished = true;
        cleanup.accept(reason);
    }

    public enum EndReason {
        COMPLETED,
        INTERRUPTED
    }

    public enum Easing {
        LINEAR {
            @Override
            double ease(double t) {
                return t;
            }
        },
        SMOOTH {
            @Override
            double ease(double t) {
                return t * t * (3.0 - 2.0 * t);
            }
        },
        SMOOTHER {
            @Override
            double ease(double t) {
                return t * t * t * (t * (t * 6.0 - 15.0) + 10.0);
            }
        };

        public double apply(double progress) {
            if (!Double.isFinite(progress)) {
                throw new IllegalArgumentException("Progress must be finite");
            }
            return ease(Math.max(0.0, Math.min(1.0, progress)));
        }

        public double interpolate(double from, double to, double progress) {
            return from + (to - from) * apply(progress);
        }

        abstract double ease(double t);
    }

    private record Phase(long start, long end, Easing easing) {
    }

    private record Cue(long tick, Runnable action) {
    }
}