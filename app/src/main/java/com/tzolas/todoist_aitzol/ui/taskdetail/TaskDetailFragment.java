package com.tzolas.todoist_aitzol.ui.taskdetail;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.tzolas.todoist_aitzol.R;
import com.tzolas.todoist_aitzol.data.local.entities.Task;
import com.tzolas.todoist_aitzol.ui.EditTaskFragment;
import com.tzolas.todoist_aitzol.viewModel.TaskViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

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

        Button buttonShare = view.findViewById(R.id.buttonShare);
        buttonShare.setOnClickListener(v -> shareTask());

        Button buttonExport = view.findViewById(R.id.buttonExport);
        buttonExport.setOnClickListener(v -> exportTaskToTxt());

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
            EditTaskFragment editTaskFragment = new EditTaskFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("task", task); // Pasamos la tarea actual
            editTaskFragment.setArguments(bundle);

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, editTaskFragment) // Reemplazamos el fragmento actual
                    .addToBackStack(null) // Permite volver atrás
                    .commit();
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

    private void shareTask() {
        if (task != null) {
            String statusText = task.isCompleted() ? getString(R.string.task_completed) : getString(R.string.task_not_completed);
            String shareText = getString(R.string.share_task_text, task.getTitle(), task.getDescription(), statusText);

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, getString(R.string.share_task_title));
            startActivity(shareIntent);
        }
    }

    private void exportTaskToTxt() {
        if (task == null) return;

        String fileName = "task_" + task.getId() + ".txt";
        String content = "Tarea: " + task.getTitle() + "\n" +
                "Descripción: " + task.getDescription() + "\n" +
                "Estado: " + (task.isCompleted() ? getString(R.string.task_completed) : getString(R.string.task_not_completed));

        File file = new File(requireContext().getExternalFilesDir(null), fileName);

        try (FileOutputStream fos = new FileOutputStream(file);
             OutputStreamWriter writer = new OutputStreamWriter(fos)) {
            writer.write(content);
            Toast.makeText(requireContext(), "Tarea exportada: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(requireContext(), "Error al exportar tarea", Toast.LENGTH_SHORT).show();
        }
    }


}
