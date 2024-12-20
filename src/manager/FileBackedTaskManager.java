package manager;

import task.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final Path filePath;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public FileBackedTaskManager(Path filePath) {
        this.filePath = filePath;
    }

    protected void save() {
        try {
            StringBuilder data = new StringBuilder("id,type,name,status,description,epic,duration,startTime\n");
            for (Task task : getListOfAllTasks()) {
                data.append(task).append("\n");
            }
            for (Epic epic : getListOfAllEpics()) {
                data.append(epic).append("\n");
            }
            for (Subtask subtask : getListOfAllSubtasks()) {
                data.append(subtask).append("\n");
            }
            Files.writeString(filePath, data.toString());
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка сохранения в файл", exception);
        }
    }

    public void requestSave() {
        save();
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public boolean addSubtask(Subtask subtask) {
        boolean result = super.addSubtask(subtask);
        save();
        return result;
    }

    @Override
    public boolean updateTask(Task task) {
        boolean result = super.updateTask(task);
        save();
        return result;
    }

    @Override
    public boolean updateEpic(Epic epic) {
        boolean result = super.updateEpic(epic);
        save();
        return result;
    }

    @Override
    public boolean updateSubtask(Subtask subtask) {
        boolean result = super.updateSubtask(subtask);
        save();
        return result;
    }

    @Override
    public boolean removeTaskById(int id) {
        boolean result = super.removeTaskById(id);
        save();
        return result;
    }

    @Override
    public boolean removeEpicById(int id) {
        boolean result = super.removeEpicById(id);
        save();
        return result;
    }

    @Override
    public boolean removeSubtaskById(int id) {
        boolean result = super.removeSubtaskById(id);
        save();
        return result;
    }

    public static FileBackedTaskManager loadFromFile(Path file) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        try {
            List<String> lines = Files.readAllLines(file);

            for (int i = 1; i < lines.size(); i++) {
                fromString(lines.get(i), taskManager);
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка загрузки из файла", exception);
        }
        return taskManager;
    }

    private static void fromString(String value, FileBackedTaskManager taskManager) {
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        Type type = Type.valueOf(parts[1]);

        String title = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];

        Duration duration = (parts.length > 6 && !"null".equals(parts[6]))
                ? Duration.ofMinutes(Long.parseLong(parts[6]))
                : null;
        LocalDateTime startTime = (parts.length > 7 && !"null".equals(parts[7]))
                ? LocalDateTime.parse(parts[7], FORMATTER)
                : null;

        switch (type) {
            case TASK -> {
                Task task = new Task(title, description, status);
                task.setId(id);
                task.setDuration(duration);
                task.setStartTime(startTime);
                taskManager.addTask(task);
            }
            case EPIC -> {
                Epic epic = new Epic(title, description);
                epic.setId(id);
                taskManager.addEpic(epic);
            }
            case SUBTASK -> {
                int epicId = Integer.parseInt(parts[5]);
                Subtask subtask = new Subtask(title, description, status, epicId);
                subtask.setId(id);
                subtask.setDuration(duration);
                subtask.setStartTime(startTime);
                taskManager.addSubtask(subtask);
            }
            default -> throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }
}