package main;

import history.TaskManager;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        // Создание задач
        Task task1 = new Task("Ремонт", "Подготовиться к ремонту", Status.NEW);
        taskManager.addTask(task1);
        Task task2 = new Task("Подарок", "Купить подарок", Status.NEW);
        taskManager.addTask(task2);

        // Создание эпика с подзадачами
        ArrayList<Integer> subtaskIds = new ArrayList<>();
        Epic epic1 = new Epic("Задание на работе", "Выполнть важное задание");
        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Сделать фотографии", "Подготовить фотографии",
                Status.NEW, epic1.getId());
        taskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("Создать альбом", "Выбрать подходящие фотографии для альюома",
                Status.NEW, epic1.getId());
        taskManager.addSubtask(subtask2);

        System.out.println("Все задачи: " + taskManager.getListOfAllTasks());
        System.out.println("Все эпики: " + taskManager.getListOfAllEpics());
        System.out.println("Все подзадачи эпика 1: " + taskManager.getListOfEpicSubstaks(epic1.getId()));

        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        System.out.println("Эпик после обновления подзадачи: " + taskManager.getEpicById(epic1.getId()));

        taskManager.removeTaskById(task1.getId());
        taskManager.removeEpicById(epic1.getId());

        System.out.println("Все задачи после удаления: " + taskManager.getListOfAllTasks());
        System.out.println("Все эпики после удаления: " + taskManager.getListOfAllEpics());
    }
}
