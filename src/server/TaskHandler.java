package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import task.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TaskHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(java.time.LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(java.time.Duration.class, new DurationAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");

        try {
            if (method.equals("GET") && pathParts.length == 2) {
                List<Task> tasks = taskManager.getListOfAllTasks();
                sendResponse(exchange, gson.toJson(tasks), 200);
            } else if (method.equals("GET") && pathParts.length == 3) {
                int id = Integer.parseInt(pathParts[2]);
                Task task = taskManager.getTaskById(id);
                if (task != null) {
                    sendResponse(exchange, gson.toJson(task), 200);
                } else {
                    sendNotFound(exchange);
                }
            } else if (method.equals("POST") && pathParts.length == 2) {
                InputStream inputStream = exchange.getRequestBody();
                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                Task task = gson.fromJson(body, Task.class);

                if (task.getId() == 0) {
                    taskManager.addTask(task);
                    sendResponse(exchange, gson.toJson(task), 201);
                } else if (taskManager.updateTask(task)) {
                    sendResponse(exchange, gson.toJson(task), 201);
                } else {
                    sendHasInteractions(exchange);
                }
            } else if (method.equals("DELETE") && pathParts.length == 3) {
                int id = Integer.parseInt(pathParts[2]);
                if (taskManager.removeTaskById(id)) {
                    sendResponse(exchange, "{\"status\":\"Task deleted\"}", 200);
                } else {
                    sendNotFound(exchange);
                }
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendServerError(exchange);
        }
    }
}