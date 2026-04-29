package com.blockforge.common.buildstation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class BuilderStationQueue {
    private final List<BuilderStationJob> jobs = new ArrayList<>();

    public synchronized BuilderStationJob enqueue(BuilderStationJob job, int maxQueuedJobs) {
        if (jobs.size() >= Math.max(1, maxQueuedJobs)) {
            throw new IllegalStateException("Builder Station queue is full.");
        }
        jobs.add(job);
        return job;
    }

    public synchronized List<BuilderStationJob> list() {
        return jobs.stream()
                .sorted(Comparator.comparingLong(BuilderStationJob::queuedAtGameTime))
                .toList();
    }

    public synchronized Optional<BuilderStationJob> currentFor(UUID playerId) {
        return jobs.stream()
                .filter(job -> job.playerId().equals(playerId))
                .filter(job -> job.status() == BuilderStationJobStatus.RUNNING || job.status() == BuilderStationJobStatus.QUEUED || job.status() == BuilderStationJobStatus.PAUSED)
                .findFirst();
    }

    public synchronized Optional<BuilderStationJob> update(String jobId, java.util.function.UnaryOperator<BuilderStationJob> updater) {
        for (int i = 0; i < jobs.size(); i++) {
            BuilderStationJob job = jobs.get(i);
            if (job.jobId().equals(jobId)) {
                BuilderStationJob next = updater.apply(job);
                jobs.set(i, next);
                return Optional.of(next);
            }
        }
        return Optional.empty();
    }
}
