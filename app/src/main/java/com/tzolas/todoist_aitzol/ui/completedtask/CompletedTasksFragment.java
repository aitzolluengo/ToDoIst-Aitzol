package com.tzolas.todoist_aitzol.ui.completedtask;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.tzolas.todoist_aitzol.R;
import com.tzolas.todoist_aitzol.data.local.entities.Task;
import com.tzolas.todoist_aitzol.ui.tasklist.TaskAdapter;
import com.tzolas.todoist_aitzol.viewModel.TaskViewModel;
import java.util.List;
import java.util.stream.Collectors;

public class CompletedTasksFragment extends Fragment {

    private TaskViewModel taskViewModel;
    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private Button buttonDeleteAll;

    public CompletedTasksFragment() {
        // Constructor vacío requerido
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_completed_tasks, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewCompletedTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TaskAdapter(task -> {
            if (task != null) {
                new AlertDialog.Builder(requireContext())
                        .setTitle(getString(R.string.task_selected_title))
                        .setMessage(getString(R.string.task_selected_message, task.getTitle()))
                        .setPositiveButton(getString(R.string.ok_button), null)
                        .show();
            }
        });

        recyclerView.setAdapter(adapter);

        buttonDeleteAll = view.findViewById(R.id.buttonDeleteAllCompleted);

        // Inicializar ViewModel
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        taskViewModel.getAllTasks().observe(getViewLifecycleOwner(), tasks -> {
            List<Task> completedTasks = tasks.stream().filter(Task::isCompleted).collect(Collectors.toList());
            adapter.submitList(completedTasks);
        });

        // Listener para eliminar todas las tareas completadas
        buttonDeleteAll.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.delete_all_tasks_title))
                    .setMessage(getString(R.string.delete_all_tasks_message))
                    .setPositiveButton(getString(R.string.delete_all_tasks_confirm), (dialog, which) -> {
                        taskViewModel.deleteAllCompletedTasks();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        return view;
    }
}
