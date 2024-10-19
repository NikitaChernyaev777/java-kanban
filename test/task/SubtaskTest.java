package task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @Test
    void subtaskShouldBeEqualIfIdIsEqual() {
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание 1", Status.NEW, 1);
        subtask1.setId(1);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание 2", Status.DONE, 1);
        subtask2.setId(1);

        assertEquals(subtask1, subtask2, "Подзадачи с одинаковыми id должны быть равны");
    }

    @Test
    public void subtaskShouldNotHaveItselfAsItsEpic() {
        Epic epic1 = new Epic("Эпик 1", "Описание 1");
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание 1", Status.NEW, 2);

        epic1.setId(1);
        subtask1.setId(2);

        assertNotEquals(epic1.getId(), subtask1.getEpicId(), "Подзадача не должна быть своим же эпиком");
    }
}