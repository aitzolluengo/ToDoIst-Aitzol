package com.tzolas.todoist_aitzol.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.tzolas.todoist_aitzol.data.local.database.AppDatabase;
import com.tzolas.todoist_aitzol.data.local.dao.TaskDao;
import com.tzolas.todoist_aitzol.data.local.entities.Task;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskRepository {
    private final TaskDao taskDao;
    private final LiveData<List<Task>> allTasks;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public TaskRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        taskDao = db.taskDao();
        allTasks = taskDao.getAllTasks();
    }

    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    public void insert(Task task) {
        executorService.execute(() -> taskDao.insert(task));
    }

    public void update(Task task) {
        executorService.execute(() -> taskDao.update(task));
    }

    public void delete(Task task) {
        executorService.execute(() -> taskDao.delete(task));
    }

    public void deleteAllCompletedTasks() {
        executorService.execute(() -> taskDao.deleteAllCompleted());
    }
    public LiveData<List<Task>> getTasksSortedByName() {
        return taskDao.getTasksSortedByName();
    }

    public LiveData<List<Task>> getTasksSortedByDate() {
        return taskDao.getTasksSortedByDate();
    }


}