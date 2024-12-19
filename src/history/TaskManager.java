package history;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.List;

public interface TaskManager {

    void addTask(Task task);

    void addEpic(Epic epic);

    boolean addSubtask(Subtask subtask);

    List<Task> getListOfAllTasks();

    List<Epic> getListOfAllEpics();

    List<Subtask> getListOfAllSubtasks();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    List<Subtask> getListOfEpicSubstaks(int epicId);

    void removeListOfAllTasks();

    void removeListOfAllEpics();

    void removeListOfAllSubtasks();

    boolean removeTaskById(int id);

    boolean removeEpicById(int id);

    boolean removeSubtaskById(int id);

    List<Task> getPrioritizedTasks();

    boolean updateTask(Task task);

    boolean updateEpic(Epic epic);

    boolean updateSubtask(Subtask subtask);

    List<Task> getHistory();
}