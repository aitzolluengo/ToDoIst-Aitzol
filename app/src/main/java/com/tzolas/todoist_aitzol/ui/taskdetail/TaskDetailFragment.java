package com.tzolas.todoist_aitzol.ui.taskdetail;

 // Asegúrate de que el paquete sea correcto

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

import com.tzolas.todoist_aitzol.R;

public class TaskDetailFragment extends Fragment {

    public TaskDetailFragment() {
        // Constructor público vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el layout para este fragmento
        return inflater.inflate(R.layout.fragment_task_detail, container, false);
    }
}