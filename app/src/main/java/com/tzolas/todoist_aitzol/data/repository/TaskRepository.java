package com.tzolas.todoist_aitzol.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.tzolas.todoist_aitzol.data.local.database.AppDatabase;
import com.tzolas.todoist_aitzol.data.local.dao.TaskDao;
import com.tzolas.todoist_aitzol.data.local.entities.Task;
import java.util.List;

public class TaskRepository {

    private final TaskDao taskDao; // Campo 'final'
    private final LiveData<List<Task>> allTasks; // Campo 'final'

    public TaskRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        taskDao = db.taskDao();
        allTasks = taskDao.getAllTasks();
    }

    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    public void insert(Task task) {
        AppDatabase.databaseWriteExecutor.execute(() -> taskDao.insert(task));
    }

    public void update(Task task) {
        AppDatabase.databaseWriteExecutor.execute(() -> taskDao.update(task));
    }

    public void delete(Task task) {
        AppDatabase.databaseWriteExecutor.execute(() -> taskDao.delete(task));
    }
}