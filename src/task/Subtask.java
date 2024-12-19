package task;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String title, String description, Status status, int epicId) {
        super(title, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return String.format("%d,SUBTASK,%s,%s,%s,%d,%d,%s,%s",
                getId(),
                getTitle(),
                getStatus(),
                getDescription(),
                epicId,
                getDuration() != null ? getDuration().toMinutes() : 0,
                getStartTime() != null ? getStartTime().toString() : "null",
                getEndTime() != null ? getEndTime().toString() : "null");
    }
}