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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tzolas.todoist_aitzol.R;
import com.tzolas.todoist_aitzol.data.local.entities.Task;
import com.tzolas.todoist_aitzol.ui.addtask.AddTaskFragment;
import com.tzolas.todoist_aitzol.ui.taskdetail.TaskDetailFragment;
import com.tzolas.todoist_aitzol.viewModel.TaskViewModel;


public class TaskListFragment extends Fragment {

    private TaskAdapter taskAdapter;
    private TaskViewModel taskViewModel;

    public TaskListFragment() {
        // Constructor público vacío requerido
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        taskAdapter = new TaskAdapter(this::onTaskClick);
        recyclerView.setAdapter(taskAdapter);

        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String sortOrder = prefs.getString("pref_sort_order", "date");

        taskViewModel.getSortedTasks(sortOrder).observe(getViewLifecycleOwner(), tasks -> {
            taskAdapter.submitList(tasks);
        });

        FloatingActionButton fabAddTask = view.findViewById(R.id.fabAddTask);
        fabAddTask.setOnClickListener(v -> {
            AddTaskFragment addTaskFragment = new AddTaskFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, addTaskFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void onTaskClick(Task task) {
        TaskDetailFragment taskDetailFragment = new TaskDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("task", task); // ✅ Usamos Parcelable en lugar de Serializable
        taskDetailFragment.setArguments(bundle);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, taskDetailFragment)
                .addToBackStack(null)
                .commit();
    }
}
