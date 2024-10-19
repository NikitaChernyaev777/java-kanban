package history;

import org.junit.jupiter.api.Test;
import task.Status;
import task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @Test
    void shouldAddTaskToHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task("Задача 1", "Описание 1", Status.NEW);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size(), "История должна содержать одну задачу");
        assertEquals(task1, history.getFirst(), "Задача должна быть в истории");
    }

    @Test
    void shouldNotExceedMaxSize() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        for (int i = 0; i < 12; i++) {
            Task task = new Task("Тестовая задача " + i, "Тестовое описание", Status.NEW);
            historyManager.add(task);
        }

        List<Task> history = historyManager.getHistory();

        assertEquals(10, history.size(), "История должна содержать не более 10 задач");
    }

    @Test
    void shouldReturnEmptyHistoryIfNoTasks() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        List<Task> history = historyManager.getHistory();

        assertTrue(history.isEmpty(), "История должна быть пустой, если задачи не добавлены");
    }

    @Test
    void shouldRetainPreviousTaskVersionInHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task("Задача 1", "Описание 1", Status.NEW);

        historyManager.add(task1);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();

        // Ожидаем, что история содержит две записи для одной и той же задачи
        assertEquals(2, history.size(),
                "История должна содержать две записи для одной и той же задачи");
        assertEquals(task1, history.get(0), "Первая запись в истории должна быть task1");
        assertEquals(task1, history.get(1), "Вторая запись в истории также должна быть task1");
    }
}