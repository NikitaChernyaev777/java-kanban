package history;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    private Path tempFile;
    private FileBackedTaskManager taskManager;

    private Task task;
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = Files.createTempFile("tasks", ".csv");
        taskManager = new FileBackedTaskManager(tempFile);

        task = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        task.setId(1);
        epic = new Epic("Эпик 1", "Описание эпика 1");
        epic.setId(2);
        subtask = new Subtask("Подзадача 1", "Описание подзадачи 1",
                Status.IN_PROGRESS, epic.getId());
        subtask.setId(3);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(tempFile);
    }

    @Test
    void shouldSaveAndLoadEmptyFile() {
        taskManager.save();
        try {
            List<String> lines = Files.readAllLines(tempFile);
            assertEquals(1, lines.size(), "Файл должен содержать только заголовок");
            assertEquals("id,type,name,status,description,epic", lines.get(0),
                    "Некорректный заголовок файла");
        } catch (IOException exception) {
            fail("Не удалось прочитать файл: " + exception.getMessage());
        }

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(loadedManager.getListOfAllTasks().isEmpty(), "Список задач должен быть пуст");
        assertTrue(loadedManager.getListOfAllEpics().isEmpty(), "Список эпиков должен быть пуст");
        assertTrue(loadedManager.getListOfAllSubtasks().isEmpty(), "Список подзадач должен быть пуст");
    }

    @Test
    void shouldSaveAndLoadMultipleTasks() {
        taskManager.addTask(task);
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);

        try {
            List<String> lines = Files.readAllLines(tempFile);
            assertEquals(4, lines.size(),
                    "Файл должен содержать одну задачу, один эпик и одну подзадачу");
        } catch (IOException e) {
            fail("Не удалось прочитать файл: " + e.getMessage());
        }

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loadedManager.getListOfAllTasks().size(),
                "Должна быть загружена одна задача");
        assertEquals(task, loadedManager.getTaskById(task.getId()),
                "Задача загружена некорректно");

        assertEquals(1, loadedManager.getListOfAllEpics().size(),
                "Должен быть загружен один эпик");
        assertEquals(epic, loadedManager.getEpicById(epic.getId()),
                "Эпик загружен некорректно");

        assertEquals(1, loadedManager.getListOfAllSubtasks().size(),
                "Должна быть загружена одна подзадача");
        assertEquals(subtask, loadedManager.getSubtaskById(subtask.getId()),
                "Подзадача загружена некорректно");
    }

    @Test
    void shouldSaveAndLoadTasksWithUpdates() {
        taskManager.addTask(task);
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);

        task.setStatus(Status.DONE);
        taskManager.updateTask(task);

        epic.setDescription("Обновление эпика 1");
        taskManager.updateEpic(epic);

        subtask.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loadedManager.getListOfAllTasks().size(),
                "Должна быть загружена одна задача");
        assertEquals(task, loadedManager.getTaskById(task.getId()),
                "Обновленная задача загружена некорректно");

        assertEquals(1, loadedManager.getListOfAllEpics().size(),
                "Должен быть загружен один эпик");
        assertEquals(epic, loadedManager.getEpicById(epic.getId()),
                "Обновленный эпик загружен некорректно");

        assertEquals(1, loadedManager.getListOfAllSubtasks().size(),
                "Должна быть загружена одна подзадача");
        assertEquals(subtask, loadedManager.getSubtaskById(subtask.getId()),
                "Обновленная подзадача загружена некорректно");

        Epic loadedEpic = loadedManager.getEpicById(epic.getId());
        assertEquals(1, loadedEpic.getSubtasksId().size(),
                "Эпик должен содержать одну подзадачу");
        assertTrue(loadedEpic.getSubtasksId().contains(subtask.getId()),
                "Подзадача должна быть связана с эпиком");
    }
}