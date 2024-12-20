package manager;

import org.junit.jupiter.api.AfterEach;

import java.nio.file.Path;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    @Override
    protected FileBackedTaskManager createTaskManager() {
        return new FileBackedTaskManager(Path.of("test_tasks.csv"));
    }

    @AfterEach
    void cleanUp() {
        Path filePath = Path.of("test_tasks.csv");
        if (filePath.toFile().exists()) {
            filePath.toFile().delete();
        }
    }
}