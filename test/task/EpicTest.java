package task;

import history.Managers;
import history.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    private TaskManager taskManager;
    private Epic epic1;
    private Epic epic2;
    private Subtask subtask1;
    private Subtask subtask2;
    private Subtask subtask3;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();

        epic1 = new Epic("Эпик 1", "Описание эпика 1");
        epic1.setId(1);
        epic2 = new Epic("Эпик 2", "Описание эпика 2");
        epic2.setId(2);
        subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", Status.NEW, epic1.getId());
        subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", Status.NEW, epic1.getId());
        subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3", Status.DONE, epic1.getId());

        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
    }

    @Test
    void shouldCalculateStatusOfEpicAllSubtasksNew() {
        subtask1.setStatus(Status.NEW);
        subtask2.setStatus(Status.NEW);
        subtask3.setStatus(Status.NEW);

        epic1.setStatus(Status.NEW);

        assertEquals(Status.NEW, epic1.getStatus(),
                "Статус эпика должен быть NEW, при условии, что все подзадачи NEW");
    }

    @Test
    void shouldCalculateStatusOfEpicAllSubtasksDone() {
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        subtask3.setStatus(Status.DONE);

        epic1.setStatus(Status.DONE);

        assertEquals(Status.DONE, epic1.getStatus(),
                "Статус эпика должен быть DONE, при условии, что все подзадачи DONE");
    }

    @Test
    void shouldCalculateStatusOfEpicSomeSubtasksNewAndDone() {
        subtask1.setStatus(Status.NEW);
        subtask2.setStatus(Status.NEW);
        subtask3.setStatus(Status.DONE);

        epic1.setStatus(Status.IN_PROGRESS);

        assertEquals(Status.IN_PROGRESS, epic1.getStatus(),
                "Статус эпика должен быть IN_PROGRESS, при условии, что подзадачи с разными статусами");
    }

    @Test
    void shouldCalculateStatusOfEpicAllSubtasksInProgress() {
        subtask1.setStatus(Status.IN_PROGRESS);
        subtask2.setStatus(Status.IN_PROGRESS);
        subtask3.setStatus(Status.IN_PROGRESS);

        epic1.setStatus(Status.IN_PROGRESS);

        assertEquals(Status.IN_PROGRESS, epic1.getStatus(),
                "Статус эпика должен быть IN_PROGRESS, при условии, что все подзадачи IN_PROGRESS");
    }


    @Test
    void epicsShouldBeEqualIfIdIsEqual() {
        epic1.setId(1);
        epic2.setId(1);

        assertEquals(epic1, epic2, "Эпики с одинаковыми id должны быть равны");
    }
}