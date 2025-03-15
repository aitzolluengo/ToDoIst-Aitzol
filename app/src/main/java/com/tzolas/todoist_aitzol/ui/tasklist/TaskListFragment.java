package com.tzolas.todoist_aitzol.ui.tasklist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.tzolas.todoist_aitzol.R;
import com.tzolas.todoist_aitzol.data.local.entities.Task;
import com.tzolas.todoist_aitzol.viewModel.TaskViewModel;
import java.util.ArrayList;
import java.util.List;

public class TaskListFragment extends Fragment {

    private TaskViewModel taskViewModel;
    private TaskAdapter taskAdapter;
    private List<Task> taskList = new ArrayList<>();

    public TaskListFragment() {
        // Constructor público vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el layout para este fragmento
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);

        // Configurar el RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        taskAdapter = new TaskAdapter(taskList, this::onTaskClick);
        recyclerView.setAdapter(taskAdapter);

        // Obtener el ViewModel
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        // Observar cambios en la lista de tareas
        taskViewModel.getAllTasks().observe(getViewLifecycleOwner(), tasks -> {
            taskList.clear();
            taskList.addAll(tasks);
            taskAdapter.notifyDataSetChanged();
        });

        return view;
    }

    // Método para manejar clics en las tareas
    private void onTaskClick(Task task) {
        // Aquí puedes abrir el fragmento de detalles de la tarea
        // Por ejemplo: navigateToTaskDetail(task.getId());
    }
}