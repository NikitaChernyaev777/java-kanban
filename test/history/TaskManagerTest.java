package history;

import org.junit.jupiter.api.*;
import task.*;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;
    protected Task task1, task2;
    protected Epic epic1;
    protected Subtask subtask1, subtask2;

    protected abstract T createTaskManager();

    @BeforeEach
    void setUp() {
        taskManager = createTaskManager();
        task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        task2 = new Task("Задача 2", "Описание задачи 2", Status.NEW);
        epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addEpic(epic1);
        subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", Status.NEW, epic1.getId());
        subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", Status.NEW, epic1.getId());
    }

    @Test
    void shouldAddTask() {
        taskManager.addTask(task1);

        assertNotNull(taskManager.getTaskById(task1.getId()));
        assertEquals(task1, taskManager.getTaskById(task1.getId()));
    }

    @Test
    void shouldAddEpic() {
        taskManager.addEpic(epic1);

        assertNotNull(taskManager.getEpicById(epic1.getId()));
        assertEquals(epic1, taskManager.getEpicById(epic1.getId()));
    }

    @Test
    void shouldAddSubtask() {
        taskManager.addEpic(epic1);
        assertTrue(taskManager.addSubtask(subtask1));

        assertNotNull(taskManager.getSubtaskById(subtask1.getId()));
        assertEquals(subtask1, taskManager.getSubtaskById(subtask1.getId()));
    }

    @Test
    void shouldUpdateTask() {
        taskManager.addTask(task1);
        task1.setStatus(Status.IN_PROGRESS);
        assertTrue(taskManager.updateTask(task1));

        assertEquals(Status.IN_PROGRESS, taskManager.getTaskById(task1.getId()).getStatus());
    }

    @Test
    void shouldRemoveTaskById() {
        taskManager.addTask(task1);
        assertTrue(taskManager.removeTaskById(task1.getId()));

        assertNull(taskManager.getTaskById(task1.getId()));
    }

    @Test
    void shouldCalculateEpicStatus() {
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        assertEquals(Status.NEW, epic1.getStatus());

        subtask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);

        assertEquals(Status.IN_PROGRESS, epic1.getStatus());

        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);

        assertEquals(Status.DONE, epic1.getStatus());
    }

    @Test
    void shouldDetectTimeIntersection() {
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(Duration.ofHours(1));

        taskManager.addTask(task1);

        task2.setStartTime(task1.getStartTime().plusMinutes(30));
        task2.setDuration(Duration.ofHours(1));

        assertThrows(IllegalArgumentException.class, () -> taskManager.addTask(task2));
    }

    @Test
    void shouldGetHistory() {
        taskManager.addTask(task1);
        taskManager.getTaskById(task1.getId());

        assertEquals(1, taskManager.getHistory().size());
        assertEquals(task1, taskManager.getHistory().get(0));
    }
}