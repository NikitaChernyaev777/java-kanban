package main;

import history.InMemoryTaskManager;
import history.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;
import task.Status;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new InMemoryTaskManager();

        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        Task task2 = new Task("Задача 2", "Описание задачи 2", Status.IN_PROGRESS);
        Epic epic1 = new Epic("Эпик 1", "Эпик с 3 подзадачами");
        Epic epic2 = new Epic("Эпик 2", "Эпик без подзадач");

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", Status.IN_PROGRESS, epic1.getId());
        Subtask subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3", Status.DONE, epic1.getId());

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        System.out.println("Получаем задачу 1:");
        taskManager.getTaskById(task1.getId());
        printHistory(taskManager);

        System.out.println("\nПолучаем эпик 1:");
        taskManager.getEpicById(epic1.getId());
        printHistory(taskManager);

        System.out.println("\nПолучаем подзадачу 2:");
        taskManager.getSubtaskById(subtask2.getId());
        printHistory(taskManager);

        System.out.println("\nПолучаем задачу 2:");
        taskManager.getTaskById(task2.getId());
        printHistory(taskManager);

        System.out.println("\nПолучаем эпик 2:");
        taskManager.getEpicById(epic2.getId());
        printHistory(taskManager);

        System.out.println("\nУдаляем задачу 1:");
        taskManager.removeTaskById(task1.getId());
        printHistory(taskManager);

        System.out.println("\nУдаляем эпик 1 и его подзадачи:");
        taskManager.removeEpicById(epic1.getId());
        printHistory(taskManager);
    }

    private static void printHistory(TaskManager taskManager) {
        List<Task> history = taskManager.getHistory();
        System.out.println("История:");
        for (Task task : history) {
            System.out.println(task);
        }
    }
}
