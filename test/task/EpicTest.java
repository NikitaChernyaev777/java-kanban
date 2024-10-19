package task;

import history.InMemoryTaskManager;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void epicsShouldBeEqualIfIdIsEqual() {
        Epic epic1 = new Epic("Эпик 1", "Описание 1");
        epic1.setId(1);
        Epic epic2 = new Epic("Эпик 2", "Описание 2");
        epic2.setId(1);

        assertEquals(epic1, epic2, "Эпики с одинаковыми id должны быть равны");
    }

    @Test
    void epicShouldNotBeAddedAsSubtaskToItself() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic1 = new Epic("Эпик 1", "Описание 1");
        manager.addEpic(epic1);

        epic1.addSubtask(epic1.getId());

        List<Subtask> subtasks = manager.getListOfEpicSubstaks(epic1.getId());

        assertTrue(subtasks.isEmpty(), "Эпик не должен быть добавлен как своя подзадача.");
    }
}