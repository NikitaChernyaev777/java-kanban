package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subtasksId = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String title, String description) {
        super(title, description, Status.NEW);
    }

    public void addSubtask(int id) {
        if (id != this.getId()) {
            subtasksId.add(id);
        }
    }

    public List<Integer> getSubtasksId() {
        return subtasksId;
    }

    public void calculateEpicFields(List<Subtask> subtasks) {
        Duration totalDuration = Duration.ZERO;
        LocalDateTime earliestStartTime = null;
        LocalDateTime latestEndTime = null;

        for (Subtask subtask : subtasks) {
            totalDuration = totalDuration.plus(subtask.getDuration() != null
                    ? subtask.getDuration() : Duration.ZERO);
            if (subtask.getStartTime() != null) {
                if (earliestStartTime == null || subtask.getStartTime().isBefore(earliestStartTime)) {
                    earliestStartTime = subtask.getStartTime();
                }
                LocalDateTime subtasksEnd = subtask.getEndTime();
                if (subtasksEnd != null && (latestEndTime == null || subtasksEnd.isAfter(latestEndTime))) {
                    latestEndTime = subtasksEnd;
                }
            }
        }

        setDuration(totalDuration);
        setStartTime(earliestStartTime);
        endTime = latestEndTime;
    }

    @Override
    public String toString() {
        return String.format("%d,EPIC,%s,%s,%s,%d,%s,%s",
                getId(),
                getTitle(),
                getStatus(),
                getDescription(),
                getDuration() != null ? getDuration().toMinutes() : 0,
                getStartTime() != null ? getStartTime().toString() : "null",
                getEndTime() != null ? getEndTime().toString() : "null");
    }
}