package history;

import task.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history = new LinkedList<>();
    private static final int HISTORY_MAX_SIZE = 10;

    @Override
    public void add(Task task) {
        if (history.size() >= HISTORY_MAX_SIZE) {
            history.removeFirst();
        }
        history.addLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return new LinkedList<>(history);
    }
}
