package history;

import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    @Test
    void shouldAddTask() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Task task1 = new Task("Задача 1", "Описание 1", Status.NEW);
        taskManager.addTask(task1);

        assertNotNull(taskManager.getTaskById(task1.getId()), "Задача должна быть добавлена");
    }

    @Test
    void shouldReturnNullIfTaskNotFound() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        assertNull(taskManager.getTaskById(777), "Метод должен вернуть null, если задача не найдена");
    }

    @Test
    void shouldAddEpicAndSubtasks() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic1 = new Epic("Эпи 1", "Описание 1");
        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание 1", Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание 2", Status.NEW, epic1.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        List<Subtask> subtasks = taskManager.getListOfEpicSubstaks(epic1.getId());

        assertEquals(2, subtasks.size(), "Должно быть добавлено 2 подзадачи");
    }

    @Test
    void shouldRemoveTaskById() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Task task1 = new Task("Задача 1", "Описание 1", Status.NEW);
        taskManager.addTask(task1);

        assertTrue(taskManager.removeTaskById(task1.getId()), "Задача должна быть удалена");
        assertNull(taskManager.getTaskById(task1.getId()), "Задача не должна быть найдена после удаления");
    }

    @Test
    void taskStatusShouldNotChangeAfterAdditionToManager() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task task1 = new Task("Задача 1", "Описание 1", Status.NEW);
        manager.addTask(task1);

        Task retrievedTask = manager.getTaskById(task1.getId());

        assertEquals(Status.NEW, retrievedTask.getStatus(),
                "Статус задачи не должен изменяться после добавления");
        assertEquals(task1.getDescription(), retrievedTask.getDescription(),
                "Описание задачи должно совпадать");
    }

    @Test
    void shouldNotConflictWithGeneratedAndManualId() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task1 = new Task("Задача с id 1", "Описание 1", Status.NEW);
        task1.setId(1);
        manager.addTask(task1);

        Task task2 = new Task("Задача с сгенерированным id", "Описание 2", Status.NEW);
        manager.addTask(task2);

        assertNotNull(manager.getTaskById(task1.getId()), "Задача с id 1 должна быть найдена");
        assertNotNull(manager.getTaskById(task2.getId()), "Задача с сгенерированным id должна быть найдена");
    }
}