package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import task.Status;
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

    @BeforeEach
    public void setUp() {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        gson = new GsonBuilder()
                .registerTypeAdapter(java.time.LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(java.time.Duration.class, new DurationAdapter())
                .create();

        task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());
        task2 = new Task("Задача 2", "Лписание задачи 2", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.now().plusMinutes(10));

        manager.removeListOfAllTasks();
        manager.removeListOfAllSubtasks();
        manager.removeListOfAllEpics();

        try {
            taskServer.start();
        } catch (IOException e) {
            e.printStackTrace();
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
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

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
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Не удалось получить список задач");

        List<Task> tasksFromResponse = gson.fromJson(response.body(), List.class);
        assertEquals(2, tasksFromResponse.size(), "Количество задач не совпадает с ожидаемым");
    }
}