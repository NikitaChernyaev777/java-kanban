package history;

import task.Epic;
import task.Subtask;
import task.Task;
import task.Status;

import java.nio.file.Path;

public class Main {
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
}
