package task;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subtasksId;

    public Epic(String title, String description) {
        super(title, description, Status.NEW);
        this.subtasksId = new ArrayList<>();
    }

    public void addSubtask(int id) {
        if (id != this.getId()) {
            subtasksId.add(id);
        }
    }

    public List<Integer> getSubtasksId() {
        return subtasksId;
    }

    @Override
    public String toString() {
        return "task.Epic{" + (super.toString()).substring(5, super.toString().length() - 1)
                + ", idSubTasks=" + subtasksId + "}";
    }
}