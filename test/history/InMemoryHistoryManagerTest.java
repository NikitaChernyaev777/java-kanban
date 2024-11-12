package history;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Status;
import task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;
    private Task task1, task2;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
        task1 = new Task("Задача 1", "Описание 1", Status.NEW);
        task1.setId(1);
        task2 = new Task("Задача 2", "Описание 2", Status.NEW);
        task2.setId(2);
    }

    @Test
    void shouldAddTaskToHistory() {
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size(), "История должна содержать одну задачу");
        assertEquals(task1, history.getFirst(), "Задача должна быть в истории");
    }

    @Test
    void shouldUpdateHistoryWhenTaskViewedAgain() {
        historyManager.add(task1);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size(), "История должна содержать только одну запись для "
                + "уникальной задачи");
        assertEquals(task1, history.getFirst(), "Запись в истории должна быть task1");
    }

    @Test
    void shouldRemoveTaskFromHistory() {
        historyManager.add(task1);
        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();

        assertTrue(history.isEmpty(), "История должна быть пустой после удаления задачи");
    }

    @Test
    void shouldHandleMultipleTasksCorrectly() {
        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(),
                "История должна содержать две задачи");
        assertEquals(task1, history.get(0), "Первая запись в истории должна быть task1");
        assertEquals(task2, history.get(1), "Вторая запись в истории должна быть task2");
    }
}