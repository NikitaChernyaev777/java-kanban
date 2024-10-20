package main;

import history.Managers;
import history.TaskManager;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        // Создание задач
        Task task1 = new Task("Ремонт", "Подготовиться к ремонту", Status.NEW);
        taskManager.addTask(task1);
        Task task2 = new Task("Подарок", "Купить подарок", Status.NEW);
        taskManager.addTask(task2);

        // Создание эпика с подзадачами
        ArrayList<Integer> subtaskIds = new ArrayList<>();
        Epic epic1 = new Epic("Задание на работе", "Выполнить важное задание");
        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Сделать фотографии",
                "Подготовить фотографии", Status.NEW, epic1.getId());
        taskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("Создать альбом",
                "Выбрать подходящие фотографии для альбома", Status.NEW, epic1.getId());
        taskManager.addSubtask(subtask2);

        // Вызов методов TaskManager и печать всех задач и истории после каждого вызова
        printAllTasks(taskManager);

        System.out.println("\n*** Получаем Task 1 ***");
        taskManager.getTaskById(task1.getId());
        printAllTasks(taskManager);

        System.out.println("\n*** Получаем Epic 1 ***");
        taskManager.getEpicById(epic1.getId());
        printAllTasks(taskManager);

        System.out.println("\n*** Получаем Subtask 1 ***");
        taskManager.getSubtaskById(subtask1.getId());
        printAllTasks(taskManager);

        System.out.println("\n*** Обновляем статус Subtask 1 ***");
        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        printAllTasks(taskManager);

        System.out.println("\n*** Удаляем Task 1 ***");
        taskManager.removeTaskById(task1.getId());
        printAllTasks(taskManager);

        System.out.println("\n*** Удаляем Epic 1 ***");
        taskManager.removeEpicById(epic1.getId());
        printAllTasks(taskManager);

        // Итоговое состояние всех задач и история
        System.out.println("\n*** Итоговое состояние всех задач и истории ***");
        printAllTasks(taskManager);
    }

    // Метод для вывода всех задач, эпиков, подзадач и истории
    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getListOfAllTasks()) {
            System.out.println(task);
        }

        System.out.println("Эпики:");
        for (Epic epic : manager.getListOfAllEpics()) {
            System.out.println(epic);
            for (Subtask subtask : manager.getListOfEpicSubstaks(epic.getId())) {
                System.out.println("    Подзадача: " + subtask);
            }
        }

        System.out.println("Подзадачи:");
        for (Subtask subtask : manager.getListOfAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}