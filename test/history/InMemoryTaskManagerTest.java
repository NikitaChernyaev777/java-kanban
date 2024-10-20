package history;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private TaskManager taskManager;
    private Task task1;
    private Task task2;
    private Epic epic1;
    private Subtask subtask1;
    private Subtask subtask2;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();

        task1 = new Task("Задача 1", "Описание 1", Status.NEW);
        task2 = new Task("Задача с сгенерированным id", "Описание 2", Status.NEW);
        epic1 = new Epic("Эпик 1", "Описание 1");
        subtask1 = new Subtask("Подзадача 1", "Описание 1", Status.NEW, epic1.getId());
        subtask2 = new Subtask("Подзадача 2", "Описание 2", Status.NEW, epic1.getId());
    }

    @Test
    void shouldAddTask() {
        taskManager.addTask(task1);

        assertNotNull(taskManager.getTaskById(task1.getId()), "Задача должна быть добавлена");
    }

    @Test
    void shouldReturnNullIfTaskNotFound() {
        assertNull(taskManager.getTaskById(777), "Метод должен вернуть null, если задача не найдена");
    }

    @Test
    void shouldAddEpicAndSubtasks() {
        taskManager.addEpic(epic1);

        subtask1.setEpicId(epic1.getId());
        subtask2.setEpicId(epic1.getId());

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        List<Subtask> subtasks = taskManager.getListOfEpicSubstaks(epic1.getId());

        assertEquals(2, subtasks.size(), "Должно быть добавлено 2 подзадачи");
    }

    @Test
    void shouldRemoveTaskById() {
        taskManager.addTask(task1);

        assertTrue(taskManager.removeTaskById(task1.getId()), "Задача должна быть удалена");
        assertNull(taskManager.getTaskById(task1.getId()), "Задача не должна быть найдена после удаления");
    }

    @Test
    void taskStatusShouldNotChangeAfterAdditionToManager() {
        taskManager.addTask(task1);

        Task retrievedTask = taskManager.getTaskById(task1.getId());

        assertEquals(Status.NEW, retrievedTask.getStatus(),
                "Статус задачи не должен изменяться после добавления");
        assertEquals(task1.getDescription(), retrievedTask.getDescription(),
                "Описание задачи должно совпадать");
    }

    @Test
    void shouldNotConflictWithGeneratedAndManualId() {
        task1.setId(1);
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        assertNotNull(taskManager.getTaskById(task1.getId()), "Задача с id 1 должна быть найдена");
        assertNotNull(taskManager.getTaskById(task2.getId()),
                "Задача с сгенерированным id должна быть найдена");
    }
}