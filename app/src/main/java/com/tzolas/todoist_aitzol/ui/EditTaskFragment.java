package com.tzolas.todoist_aitzol.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.tzolas.todoist_aitzol.R;
import com.tzolas.todoist_aitzol.data.local.entities.Task;
import com.tzolas.todoist_aitzol.viewModel.TaskViewModel;

public class EditTaskFragment extends Fragment {

    private Task task;
    private EditText editTextTitle, editTextDescription;
    private TaskViewModel taskViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_task, container, false);

        editTextTitle = view.findViewById(R.id.editTextTitle);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        Button buttonSave = view.findViewById(R.id.buttonSave);

        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        if (getArguments() != null) {
            task = getArguments().getParcelable("task");
            if (task != null) {
                editTextTitle.setText(task.getTitle());
                editTextDescription.setText(task.getDescription());
            }
        }

        buttonSave.setOnClickListener(v -> {
            if (task != null) {
                task.setTitle(editTextTitle.getText().toString());
                task.setDescription(editTextDescription.getText().toString());
                taskViewModel.update(task);
            }
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }
}

