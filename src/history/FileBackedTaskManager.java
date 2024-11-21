package history;

import task.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final Path filePath;

    public FileBackedTaskManager(Path filePath) {
        this.filePath = filePath;
    }

    public static void main(String[] args) {
        Path tempFile = Path.of("tasks.csv");

        FileBackedTaskManager taskManager = new FileBackedTaskManager(tempFile);

        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        Task task2 = new Task("Задача 2", "Описание задачи 2", Status.IN_PROGRESS);
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1",
                Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2",
                Status.IN_PROGRESS, epic2.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        taskManager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        System.out.println("Загруженные задачи:");
        for (Task task : loadedManager.getListOfAllTasks()) {
            System.out.println(task);
        }

        System.out.println("\nЗагруженные эпики:");
        for (Epic epic : loadedManager.getListOfAllEpics()) {
            System.out.println(epic);
        }

        System.out.println("\nЗагруженные подзадачи:");
        for (Subtask subtask : loadedManager.getListOfAllSubtasks()) {
            System.out.println(subtask);
        }

        assert loadedManager.getListOfAllTasks().size() == 2 : "Ошибка загрузки задач";
        assert loadedManager.getListOfAllEpics().size() == 2 : "Ошибка загрузки эпиков";
        assert loadedManager.getListOfAllSubtasks().size() == 2 : "Ошибка загрузки подзадач";
        System.out.println("\nЗагрузка завершена успешно!");
    }

    void save() {
        try {
            StringBuilder data = new StringBuilder("id,type,name,status,description,epic\n");
            for (Task task : getListOfAllTasks()) {
                data.append(toString(task)).append("\n");
            }
            for (Epic epic : getListOfAllEpics()) {
                data.append(toString(epic)).append("\n");
            }
            for (Subtask subtask : getListOfAllSubtasks()) {
                data.append(toString(subtask)).append("\n");
            }
            Files.writeString(filePath, data.toString());
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка сохранения в файл", exception);
        }
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
                Task task = fromString(lines.get(i));
                if (task instanceof Subtask) {
                    taskManager.addSubtask((Subtask) task);
                } else if (task instanceof Epic) {
                    taskManager.addEpic((Epic) task);
                } else {
                    taskManager.addTask(task);
                }
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка загрузки из файла", exception);
        }
        return taskManager;
    }

    private static String toString(Task task) {
        if (task instanceof Subtask subtask) {
            return String.format("%d,SUBTASK,%s,%s,%s,%d",
                    subtask.getId(),
                    subtask.getTitle(),
                    subtask.getStatus(),
                    subtask.getDescription(),
                    subtask.getEpicId());
        } else if (task instanceof Epic) {
            return String.format("%d,EPIC,%s,%s,%s",
                    task.getId(),
                    task.getTitle(),
                    task.getStatus(),
                    task.getDescription());
        } else {
            return String.format("%d,TASK,%s,%s,%s",
                    task.getId(),
                    task.getTitle(),
                    task.getStatus(),
                    task.getDescription());
        }
    }

    private static Task fromString(String value) {
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        Type type = Type.valueOf(parts[1]);
        String title = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];
        switch (type) {
            case TASK: {
                Task task = new Task(title, description, status);
                task.setId(id);
                return task;
            }
            case EPIC: {
                Epic epic = new Epic(title, description);
                epic.setId(id);
                return epic;
            }
            case SUBTASK:
                int epicId = Integer.parseInt(parts[5]);
                Subtask subtask = new Subtask(title, description, status, epicId);
                subtask.setId(id);
                return subtask;
            default:
                throw new IllegalArgumentException("Неизвестны тип: " + type);
        }
    }
}
