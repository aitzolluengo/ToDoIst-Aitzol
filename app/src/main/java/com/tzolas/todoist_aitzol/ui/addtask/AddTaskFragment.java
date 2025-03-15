package com.tzolas.todoist_aitzol.ui.addtask;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.tzolas.todoist_aitzol.R;
import com.tzolas.todoist_aitzol.data.local.entities.Task;
import com.tzolas.todoist_aitzol.viewModel.TaskViewModel;

public class AddTaskFragment extends Fragment {

    private EditText editTextTitle;
    private EditText editTextDescription;
    private Button buttonSave;

    public AddTaskFragment() {
        // Constructor público vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el layout para este fragmento
        View view = inflater.inflate(R.layout.fragment_add_task, container, false);

        // Referencias a las vistas
        editTextTitle = view.findViewById(R.id.editTextTitle);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        buttonSave = view.findViewById(R.id.buttonSave);

        // Manejar clic en el botón Guardar
        buttonSave.setOnClickListener(v -> saveTask());

        return view;
    }

    // Método para guardar la tarea
    private void saveTask() {
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();

        if (!title.isEmpty()) {
            Task task = new Task(title, description, false, null);
            TaskViewModel taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
            taskViewModel.insert(task);

            // Regresar a la lista de tareas
            requireActivity().getSupportFragmentManager().popBackStack();
        }
    }
}