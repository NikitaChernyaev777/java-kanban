package manager;

import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private int nextId = 1;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())));
    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
    }

    private boolean hasTimeIntersection(Task newTask) {
        if (newTask.getStartTime() == null || newTask.getEndTime() == null) {
            return false;
        }
        return prioritizedTasks.stream()
                .filter(availableTask ->
                        availableTask.getStartTime() != null && availableTask.getEndTime() != null)
                .anyMatch(availableTask ->
                        !(newTask.getEndTime().isBefore(availableTask.getStartTime()) ||
                                newTask.getStartTime().isAfter(availableTask.getEndTime())));
    }

    private void validateAndAddToPrioritizedTasks(Task task) {
        if (task.getStartTime() != null && !hasTimeIntersection(task)) {
            prioritizedTasks.add(task);
        } else if (task.getStartTime() != null) {
            throw new IllegalArgumentException("Время выполнения задачи пересекается с существующими задачами");
        }
    }

    @Override
    public void addTask(Task task) {
        task.setId(nextId++);
        tasks.put(task.getId(), task);
        validateAndAddToPrioritizedTasks(task);
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
        epic.calculateEpicFields(new ArrayList<>());
    }

    @Override
    public boolean addSubtask(Subtask subtask) {
        if (epics.containsKey(subtask.getEpicId())) {
            subtask.setId(nextId++);
            subtasks.put(subtask.getId(), subtask);
            epics.get(subtask.getEpicId()).addSubtask(subtask.getId());
            updateEpicStatus(subtask.getEpicId());
            validateAndAddToPrioritizedTasks(subtask);
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
    public List<Subtask> getListOfEpicSubstaks(int epicId) {
        List<Subtask> list = new ArrayList<>();
        if (epics.containsKey(epicId)) {
            for (Integer subtaskId : epics.get(epicId).getSubtasksId()) {
                list.add(subtasks.get(subtaskId));
            }
        }
        return list;
    }

    @Override
    public void removeListOfAllTasks() {
        for (int id : tasks.keySet()) {
            historyManager.remove(id);
        }
        tasks.clear();
    }

    @Override
    public void removeListOfAllEpics() {
        for (int id : epics.keySet()) {
            historyManager.remove(id);
        }
        for (int id : subtasks.keySet()) {
            historyManager.remove(id);
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeListOfAllSubtasks() {
        for (int id : subtasks.keySet()) {
            historyManager.remove(id);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtasksId().clear();
            updateEpicStatus(epic.getId());
        }
    }

    @Override
    public boolean removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            Task removedTask = tasks.remove(id);
            prioritizedTasks.remove(removedTask);
            historyManager.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeEpicById(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.remove(id);
            for (Integer subtaskId : epic.getSubtasksId()) {
                Subtask removedSubtask = subtasks.get(subtaskId);
                prioritizedTasks.remove(removedSubtask);
                historyManager.remove(subtaskId);
            }
            historyManager.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            Subtask removedSubtask = subtasks.remove(id);
            prioritizedTasks.remove(removedSubtask);
            Epic epic = epics.get(removedSubtask.getEpicId());
            epic.getSubtasksId().remove((Integer) id);
            updateEpicStatus(removedSubtask.getEpicId());
            historyManager.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public boolean updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            prioritizedTasks.remove(tasks.get(task.getId()));
            tasks.put(task.getId(), task);
            validateAndAddToPrioritizedTasks(task);
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
            prioritizedTasks.remove(subtasks.get(subtask.getId()));
            subtasks.put(subtask.getId(), subtask);
            validateAndAddToPrioritizedTasks(subtask);
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
        List<Subtask> subtasksList = epic.getSubtasksId().stream()
                .map(subtasks::get)
                .collect(Collectors.toList());

        epic.calculateEpicFields(subtasksList);

        boolean allNew = subtasksList.stream().allMatch(s -> s.getStatus() == Status.NEW);
        boolean allDone = subtasksList.stream().allMatch(s -> s.getStatus() == Status.DONE);

        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (allNew) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}