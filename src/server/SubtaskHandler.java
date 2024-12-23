package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import task.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtaskHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public SubtaskHandler(TaskManager taskManager) {
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
                List<Subtask> subtasks = taskManager.getListOfAllSubtasks();
                sendResponse(exchange, gson.toJson(subtasks), 200);
            } else if (method.equals("GET") && pathParts.length == 3) {
                int id = Integer.parseInt(pathParts[2]);
                Subtask subtask = taskManager.getSubtaskById(id);
                if (subtask != null) {
                    sendResponse(exchange, gson.toJson(subtask), 200);
                } else {
                    sendNotFound(exchange);
                }
            } else if (method.equals("POST") && pathParts.length == 2) {
                InputStream inputStream = exchange.getRequestBody();
                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                Subtask subtask = gson.fromJson(body, Subtask.class);

                if (subtask.getId() == 0) {
                    if (taskManager.addSubtask(subtask)) {
                        sendResponse(exchange, gson.toJson(subtask), 201);
                    } else {
                        sendHasInteractions(exchange);
                    }
                } else if (taskManager.updateSubtask(subtask)) {
                    sendResponse(exchange, gson.toJson(subtask), 201);
                } else {
                    sendHasInteractions(exchange);
                }
            } else if (method.equals("DELETE") && pathParts.length == 3) {
                int id = Integer.parseInt(pathParts[2]);
                if (taskManager.removeSubtaskById(id)) {
                    sendResponse(exchange, "{\"status\":\"Subtask deleted\"}", 200);
                } else {
                    sendNotFound(exchange);
                }
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendServerError(exchange);
        }
    }
}