package com.tzolas.todoist_aitzol.ui.tasklist;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.tzolas.todoist_aitzol.R;
import com.tzolas.todoist_aitzol.data.local.entities.Task;
import com.tzolas.todoist_aitzol.viewModel.TaskViewModel;

public class TaskListFragment extends Fragment {

    private RecyclerView recyclerView;  // ✅ Declarado correctamente
    private TaskAdapter taskAdapter;
    private TaskViewModel taskViewModel;

    public TaskListFragment() {
        // Constructor público vacío requerido
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewTasks); // Asegúrate de que este ID coincide con tu XML
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        taskAdapter = new TaskAdapter(this::onTaskClick);
        recyclerView.setAdapter(taskAdapter);

        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String sortOrder = prefs.getString("pref_sort_order", "date");

        taskViewModel.getSortedTasks(sortOrder).observe(getViewLifecycleOwner(), tasks -> {
            taskAdapter.submitList(tasks);
        });

        return view;
    }

    // Método para manejar clics en las tareas
    private void onTaskClick(Task task) {
        // Aquí puedes abrir el fragmento de detalles de la tarea
        // Por ejemplo: navigateToTaskDetail(task.getId());
    }
}
