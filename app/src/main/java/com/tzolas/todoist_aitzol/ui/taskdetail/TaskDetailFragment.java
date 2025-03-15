package com.tzolas.todoist_aitzol.ui.taskdetail;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.tzolas.todoist_aitzol.R;
import com.tzolas.todoist_aitzol.data.local.entities.Task;
import com.tzolas.todoist_aitzol.viewModel.TaskViewModel;

public class TaskDetailFragment extends Fragment {

    private static final String CHANNEL_ID = "task_notifications";
    private TaskViewModel taskViewModel;
    private Task task;

    public TaskDetailFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_detail, container, false);

        TextView textViewTitle = view.findViewById(R.id.textViewTitle);
        TextView textViewDescription = view.findViewById(R.id.textViewDescription);
        CheckBox checkBoxCompleted = view.findViewById(R.id.checkBoxCompleted);
        Button buttonEdit = view.findViewById(R.id.buttonEdit);
        Button buttonDelete = view.findViewById(R.id.buttonDelete);

        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        if (getArguments() != null) {
            task = (Task) getArguments().getSerializable("task");
            if (task != null) {
                textViewTitle.setText(task.getTitle());
                textViewDescription.setText(task.getDescription());
                checkBoxCompleted.setChecked(task.isCompleted());
            }
        }

        checkBoxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (task != null) {
                task.setCompleted(isChecked);
                taskViewModel.update(task);
                showNotification("Has completado: " + task.getTitle());
            }
        });

        buttonEdit.setOnClickListener(v -> {
            // Implementar lógica de edición
        });

        buttonDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Eliminar Tarea")
                    .setMessage("¿Estás seguro de que deseas eliminar esta tarea?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        if (task != null) {
                            taskViewModel.delete(task);
                            requireActivity().getSupportFragmentManager().popBackStack();
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        requestNotificationPermission();
        createNotificationChannel();

        return view;
    }


    @SuppressWarnings("ConstantConditions")
    private void createNotificationChannel() {
        CharSequence name = "Task Notifications";
        String description = "Notificaciones de tareas completadas";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = requireContext().getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }


    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Log.d("Permissions", "Permiso de notificación concedido");
                } else {
                    Log.d("Permissions", "Permiso de notificación denegado");
                }
            });

    private void showNotification(String message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                return; // No enviar la notificación si no hay permisos
            }
        }

        NotificationManager notificationManager = (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Tarea completada")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        int notificationId = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
        notificationManager.notify(notificationId, builder.build());
    }
}