package com.tzolas.todoist_aitzol.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.tzolas.todoist_aitzol.data.local.entities.Task;
import com.tzolas.todoist_aitzol.data.repository.TaskRepository;
import java.util.List;

public class TaskViewModel extends AndroidViewModel {

    private final TaskRepository taskRepository;  // ✅ Declara correctamente la variable
    private final LiveData<List<Task>> allTasks;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        taskRepository = new TaskRepository(application);  // ✅ Inicializa el repositorio
        allTasks = taskRepository.getAllTasks();
    }

    public LiveData<List<Task>> getSortedTasks(String sortOrder) {
        if (sortOrder.equals("name")) {
            return taskRepository.getTasksSortedByName();
        } else {
            return taskRepository.getTasksSortedByDate();
        }
    }

    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    public void insert(Task task) {
        taskRepository.insert(task);
    }

    public void update(Task task) {
        taskRepository.update(task);
    }

    public void delete(Task task) {
        taskRepository.delete(task);
    }

    public void deleteAllCompletedTasks() {
        taskRepository.deleteAllCompletedTasks();
    }
}
