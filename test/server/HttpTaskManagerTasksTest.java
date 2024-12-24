package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import server.adapter.DurationAdapter;
import server.adapter.LocalDateTimeAdapter;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTasksTest {

    private InMemoryTaskManager manager;
    private HttpTaskServer taskServer;
    private Gson gson;

    private Task task1;
    private Task task2;
    private Task task3;
    private Epic epic1;
    private Subtask subtask1;

    @BeforeEach
    public void setUp() {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();

        task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());
        task2 = new Task("Задача 2", "Описание задачи 2", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.now().plusMinutes(10));
        task3 = new Task("Task 3", "Описание задачи 3", Status.DONE,
                Duration.ofMinutes(45), LocalDateTime.of(2023, 12, 23, 14, 0));
        epic1 = new Epic("Эпик 1", "Описание эпика 1");
        subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", Status.NEW, 0);

        manager.removeListOfAllTasks();
        manager.removeListOfAllSubtasks();
        manager.removeListOfAllEpics();

        try {
            taskServer.start();
        } catch (IOException e) {
            fail("Не удалось запустить HTTP сервер");
        }
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        String taskJson = gson.toJson(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Ошибка при создании задачи");

        List<Task> tasksFromManager = manager.getListOfAllTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Неверное количество задач");
        assertEquals("Задача 1", tasksFromManager.get(0).getTitle(), "Имя задачи не совпадает");
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        manager.addTask(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task1.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Задача не найдена");

        Task retrievedTask = gson.fromJson(response.body(), Task.class);
        assertEquals(task1.getTitle(), retrievedTask.getTitle(),
                "Полученная задача не совпадает с добавленной");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        manager.addTask(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task1.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Задача не была удалена");

        List<Task> tasksFromManager = manager.getListOfAllTasks();
        assertEquals(0, tasksFromManager.size(), "Задача не была удалена из менеджера");
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        manager.addTask(task1);
        manager.addTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Не удалось получить список задач");

        List<Task> tasksFromResponse = gson.fromJson(response.body(), List.class);
        assertEquals(2, tasksFromResponse.size(), "Количество задач не совпадает с ожидаемым");
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        String epicJson = gson.toJson(epic1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Ошибка при создании эпика");

        List<Epic> epicsFromManager = manager.getListOfAllEpics();
        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Неверное количество эпиков");
        assertEquals("Эпик 1", epicsFromManager.get(0).getTitle(), "Имя эпика не совпадает");
        System.out.println("Тело ответа сервера: " + response.body());
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        manager.addEpic(epic1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic1.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Эпик не найден");

        Epic retrievedEpic = gson.fromJson(response.body(), Epic.class);
        assertEquals(epic1.getTitle(), retrievedEpic.getTitle(),
                "Полученный эпик не совпадает с добавленным");
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        manager.addEpic(epic1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic1.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Эпик не был удален");

        List<Epic> epicsFromManager = manager.getListOfAllEpics();
        assertEquals(0, epicsFromManager.size(), "Эпик не был удален из менеджера");
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        manager.addEpic(epic1);
        subtask1.setEpicId(epic1.getId());
        String subtaskJson = gson.toJson(subtask1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Ошибка при создании подзадачи");

        List<Subtask> subtasksFromManager = manager.getListOfAllSubtasks();
        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Неверное количество подзадач");
        assertEquals("Подзадача 1", subtasksFromManager.get(0).getTitle(),
                "Имя подзадачи не совпадает");
    }

    @Test
    public void testGetSubtaskById() throws IOException, InterruptedException {
        manager.addEpic(epic1);
        subtask1.setEpicId(epic1.getId());
        manager.addSubtask(subtask1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask1.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Подзадача не найдена");

        Subtask retrievedSubtask = gson.fromJson(response.body(), Subtask.class);
        assertEquals(subtask1.getTitle(), retrievedSubtask.getTitle(),
                "Полученная подзадача не совпадает с добавленной");
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        manager.addEpic(epic1);
        subtask1.setEpicId(epic1.getId());
        manager.addSubtask(subtask1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask1.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Подзадача не была удалена");

        List<Subtask> subtasksFromManager = manager.getListOfAllSubtasks();
        assertEquals(0, subtasksFromManager.size(), "Подзадача не была удалена из менеджера");
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        manager.addTask(task1);
        manager.addTask(task2);
        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Не удалось получить историю");

        List<Task> historyFromResponse = gson.fromJson(response.body(), List.class);
        assertEquals(2, historyFromResponse.size(), "История задач не совпадает с ожидаемой");
    }

    @Test
    public void testGetPrioritizedTasks() throws IOException, InterruptedException {
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Не удалось получить приоритизированные задачи");

        List<Task> prioritizedFromResponse = gson.fromJson(response.body(), List.class);
        assertEquals(3, prioritizedFromResponse.size(),
                "Количество приоритизированных задач не совпадает с ожидаемым");
    }
}