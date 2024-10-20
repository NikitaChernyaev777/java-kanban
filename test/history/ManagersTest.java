package history;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void taskManagerShouldBeInitialized() {
        TaskManager taskManager = Managers.getDefault();

        assertNotNull(taskManager, "TaskManager должен быть проинициализирован");
    }

    @Test
    void historyManagerShouldBeInitialized() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(historyManager, "HistoryManager должен быть проинициализирован");
    }
}