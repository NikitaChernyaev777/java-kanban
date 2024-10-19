package history;

import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int nextId = 1;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
    }

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public void addTask(Task task) {
        task.setId(nextId++);
        tasks.put(task.getId(), task);
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
    }

    @Override
    public boolean addSubtask(Subtask subtask) {
        if (epics.containsKey(subtask.getEpicId())) {
            subtask.setId(nextId++);
            subtasks.put(subtask.getId(), subtask);
            epics.get(subtask.getEpicId()).addSubtask(subtask.getId());
            updateEpicStatus(subtask.getEpicId());
            return true;
        }
        return false;
    }

    @Override
    public List<Task> getListOfAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getListOfAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getListOfAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public ArrayList<Subtask> getListOfEpicSubstaks(int epicId) {
        ArrayList<Subtask> list = new ArrayList<>();
        if (epics.containsKey(epicId)) {
            for (Integer subtaskId : epics.get(epicId).getSubtasksId()) {
                list.add(subtasks.get(subtaskId));
            }
        }
        return list;
    }

    @Override
    public void removeListOfAllTasks() {
        tasks.clear();
    }

    @Override
    public void removeListOfAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeListOfAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtasksId().clear();
            updateEpicStatus(epic.getId());
        }
    }

    @Override
    public boolean removeTaskById(int id) {
        return tasks.remove(id) != null;
    }

    @Override
    public boolean removeEpicById(int id) {
        if (epics.containsKey(id)) {
            for (Integer subtaskId : epics.get(id).getSubtasksId()) {
                subtasks.remove(subtaskId);
            }
            epics.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.remove(id);
            Epic epic = epics.get(subtask.getEpicId());
            epic.getSubtasksId().remove((Integer) id);
            updateEpicStatus(subtask.getEpicId());
            return true;
        }
        return false;
    }

    @Override
    public boolean updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            updateEpicStatus(subtask.getEpicId());
            return true;
        }
        return false;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Integer> subtasksIds = epic.getSubtasksId();

        if (subtasksIds.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean isAllSubtasksDone = true;
        boolean isAllSubtasksNew = true;

        for (Integer subtaskId : subtasksIds) {
            Status subtaskStatus = subtasks.get(subtaskId).getStatus();
            if (subtaskStatus != Status.NEW) {
                isAllSubtasksNew = false;
            }
            if (subtaskStatus != Status.DONE) {
                isAllSubtasksDone = false;
            }
        }

        if (isAllSubtasksDone) {
            epic.setStatus(Status.DONE);
        } else if (isAllSubtasksNew) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}