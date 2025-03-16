package com.tzolas.todoist_aitzol.ui.taskdetail;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.core.app.NotificationCompat;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_detail, container, false);

        TextView textViewTitle = view.findViewById(R.id.textViewTitle);
        TextView textViewDescription = view.findViewById(R.id.textViewDescription);
        CheckBox checkBoxCompleted = view.findViewById(R.id.checkBoxCompleted);
        Button buttonEdit = view.findViewById(R.id.buttonEdit);
        Button buttonDelete = view.findViewById(R.id.buttonDelete);

        taskViewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);

        if (getArguments() != null) {
            task = getArguments().getParcelable("task");
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

        buttonDelete.setOnClickListener(v ->
                new AlertDialog.Builder(requireContext())
                        .setTitle(getString(R.string.delete_task_title))
                        .setMessage(getString(R.string.delete_task_message))
                        .setPositiveButton(getString(R.string.delete_task_confirm), (dialog, which) -> {
                            if (task != null) {
                                taskViewModel.delete(task);
                                requireActivity().getSupportFragmentManager().popBackStack();
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .show()
        );

        createNotificationChannel(); // Solo creará el canal si la API es 29+

        return view;
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Task Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        channel.setDescription("Notificaciones de tareas completadas");

        NotificationManager notificationManager = requireContext().getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showNotification(String message) {
        NotificationManager notificationManager = (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Tarea completada")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify((int) (System.currentTimeMillis() % Integer.MAX_VALUE), builder.build());
    }
}
