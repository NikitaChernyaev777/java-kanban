package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import task.Epic;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public EpicHandler(TaskManager taskManager) {
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
                List<Epic> epics = taskManager.getListOfAllEpics();
                sendResponse(exchange, gson.toJson(epics), 200);
            } else if (method.equals("GET") && pathParts.length == 3) {
                int id = Integer.parseInt(pathParts[2]);
                Epic epic = taskManager.getEpicById(id);
                if (epic != null) {
                    sendResponse(exchange, gson.toJson(epic), 200);
                } else {
                    sendNotFound(exchange);
                }
            } else if (method.equals("POST") && pathParts.length == 2) {
                InputStream inputStream = exchange.getRequestBody();
                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                Epic epic = gson.fromJson(body, Epic.class);

                if (epic.getId() == 0) {
                    taskManager.addEpic(epic);
                    sendResponse(exchange, gson.toJson(epic), 201);
                } else if (taskManager.updateEpic(epic)) {
                    sendResponse(exchange, gson.toJson(epic), 201);
                } else {
                    sendHasInteractions(exchange);
                }
            } else if (method.equals("DELETE") && pathParts.length == 3) {
                int id = Integer.parseInt(pathParts[2]);
                if (taskManager.removeEpicById(id)) {
                    sendResponse(exchange, "{\"status\":\"Epic deleted\"}", 200);
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