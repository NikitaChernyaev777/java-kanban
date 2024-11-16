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
    private Epic epic1;
    private Subtask subtask1;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();

        task1 = new Task("Задача 1", "Описание 1", Status.NEW);
        task1.setId(1);
        epic1 = new Epic("Эпик 1", "Описание 1");
        epic1.setId(2);
        subtask1 = new Subtask("Подзадача 1", "Описание 1", Status.NEW, epic1.getId());
        subtask1.setId(3);

        taskManager.addTask(task1);
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask1);
    }

    @Test
    void shouldRemoveSubtaskAndClearEpicSubtasksList() {
        taskManager.removeSubtaskById(subtask1.getId());

        assertTrue(epic1.getSubtasksId().isEmpty(),
                "Список подзадач в эпике должен быть пустым после удаления подзадачи");
    }

    @Test
    void shouldNotAllowEpicToHaveItsOwnIdAsSubtask() {
        epic1.addSubtask(epic1.getId());
        assertFalse(epic1.getSubtasksId().contains(epic1.getId()),
                "Эпик не должен иметь свой id в списке подзадач");
    }

    @Test
    void shouldUpdateTaskStatusCorrectly() {
        taskManager.addTask(task1);
        task1.setStatus(Status.IN_PROGRESS);
        Task updateTask = taskManager.getTaskById(task1.getId());

        assertEquals(Status.IN_PROGRESS, updateTask.getStatus(),
                "Статус задачи должен быть обновлен в менеджере");
    }

    @Test
    void shouldClearRemovedTasksFromHistory() {
        taskManager.addTask(task1);
        taskManager.getTaskById(task1.getId());
        taskManager.removeTaskById(task1.getId());

        List<Task> history = taskManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой после удаления задачи из менеджера");
    }

    @Test
    void shouldLinkSubtaskToEpicCorrectly() {
        taskManager.addSubtask(subtask1);
        assertEquals(epic1.getId(), subtask1.getEpicId(), "Подзадача должна быть связана с эпиком");
    }
}