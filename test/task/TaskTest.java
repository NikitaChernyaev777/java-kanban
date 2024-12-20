package task;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void tasksShouldBeEqualIfIdIsEqual() {
        Task task1 = new Task("Задача 1", "Описание 1", Status.NEW);
        task1.setId(1);
        Task task2 = new Task("Задача 2", "Описание 2", Status.DONE);
        task2.setId(1);

        assertEquals(task1, task2, "Задачи с одинаковыми id должны быть равны");
    }

    @Test
    void taskStatusShouldNotChangeAfterAdditionToManager() {
        Task task1 = new Task("Задача 1", "Описание 1", Status.NEW);
        TaskManager manager = Managers.getDefault();
        manager.addTask(task1);

        Task retrievedTask = manager.getTaskById(task1.getId());

        assertEquals(Status.NEW, retrievedTask.getStatus(),
                "Статус задачи не должен измениться после добавления");
        assertEquals(task1.getDescription(), retrievedTask.getDescription(),
                "Описание задачи должно совпадать");
    }
}