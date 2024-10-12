import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksId;

    public Epic(String title, String description, Status status, ArrayList<Integer> subtasksId) {
        super(title, description, status);
        this.subtasksId = new ArrayList<>();
        this.subtasksId.addAll(subtasksId);
    }

    public void addSubtask(int id) {
        subtasksId.add(id);
    }

    public ArrayList<Integer> getSubtasksId() {
        return subtasksId;
    }

    @Override
    public String toString() {
        return "Epic{" + (super.toString()).substring(5, super.toString().length() - 1)
                + ", idSubTasks=" + subtasksId + "}";
    }
}