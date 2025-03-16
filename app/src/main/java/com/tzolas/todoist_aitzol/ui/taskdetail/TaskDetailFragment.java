package com.tzolas.todoist_aitzol.ui.taskdetail;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.tzolas.todoist_aitzol.R;
import com.tzolas.todoist_aitzol.data.local.entities.Task;
import com.tzolas.todoist_aitzol.ui.EditTaskFragment;
import com.tzolas.todoist_aitzol.viewModel.TaskViewModel;


import java.io.File;

import java.io.FileWriter;
import java.io.IOException;

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

        // Referencias a UI
        TextView textViewTitle = view.findViewById(R.id.textViewTitle);
        TextView textViewDescription = view.findViewById(R.id.textViewDescription);
        CheckBox checkBoxCompleted = view.findViewById(R.id.checkBoxCompleted);
        Button buttonEdit = view.findViewById(R.id.buttonEdit);
        Button buttonDelete = view.findViewById(R.id.buttonDelete);
        FloatingActionButton buttonShare = view.findViewById(R.id.buttonShare);
        FloatingActionButton buttonExport = view.findViewById(R.id.buttonExport);

        // Obtener ViewModel
        taskViewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);

        // 🔥 DEPURACIÓN: Verificamos si `task` llega nulo
        if (getArguments() != null && getArguments().containsKey("task")) {
            task = getArguments().getParcelable("task");

            if (task == null) {
                Log.e("TaskDetailFragment", "⚠️ ERROR: La tarea recibida es NULL");
                Toast.makeText(requireContext(), "Error al cargar la tarea", Toast.LENGTH_LONG).show();
                requireActivity().getSupportFragmentManager().popBackStack(); // Volver atrás si la tarea no existe
                return view;
            }

            // Mostrar datos de la tarea
            textViewTitle.setText(task.getTitle());
            textViewDescription.setText(task.getDescription());
            checkBoxCompleted.setChecked(task.isCompleted());
        } else {
            Log.e("TaskDetailFragment", "⚠️ ERROR: No se pasaron argumentos al fragmento");
            Toast.makeText(requireContext(), "No se encontró la tarea", Toast.LENGTH_LONG).show();
            requireActivity().getSupportFragmentManager().popBackStack(); // Volver atrás si no hay argumentos
        }

        // Marcar tarea como completada
        checkBoxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (task != null) {
                task.setCompleted(isChecked);
                taskViewModel.update(task);
                showNotification(getString(R.string.task_completed_notification, task.getTitle()));
            }
        });

        // Editar tarea
        buttonEdit.setOnClickListener(v -> openEditTaskFragment());

        // Eliminar tarea con confirmación
        buttonDelete.setOnClickListener(v -> showDeleteConfirmationDialog());

        // Compartir tarea
        buttonShare.setOnClickListener(v -> shareTask());

        // Exportar tarea a texto
        buttonExport.setOnClickListener(v -> exportTaskToText());

        // Crear canal de notificación (solo en API 29+)
        createNotificationChannel();

        return view;
    }


    // ---------------------------
    // MÉTODOS PARA GESTIÓN DE TAREAS
    // ---------------------------

    private void openEditTaskFragment() {
        if (task == null) return;

        EditTaskFragment editTaskFragment = new EditTaskFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("task", task);
        editTaskFragment.setArguments(bundle);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, editTaskFragment)
                .addToBackStack(null)
                .commit();
    }

    private void showDeleteConfirmationDialog() {
        if (task == null) return;

        new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.delete_task_title))
                .setMessage(getString(R.string.delete_task_message))
                .setPositiveButton(getString(R.string.delete_task_confirm), (dialog, which) -> {
                    taskViewModel.delete(task);
                    requireActivity().getSupportFragmentManager().popBackStack();
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }

    // ---------------------------
    // MÉTODOS PARA COMPARTIR Y EXPORTAR
    // ---------------------------

    private void shareTask() {
        if (task == null) return;
        String statusText = task.isCompleted() ? getString(R.string.task_completed) : getString(R.string.task_not_completed);
        String shareText = getString(R.string.share_task_text, task.getTitle(), task.getDescription());
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getString(R.string.share_task_title)));
    }





    private void exportTaskToText() {
        if (task != null) {
            File file = new File(requireContext().getExternalFilesDir(null), "task_export.txt");
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("Tarea: " + task.getTitle() + "\n" +
                        "Descripción: " + task.getDescription() + "\n" +
                        "Estado: " + (task.isCompleted() ? "Completada" : "No Completada"));
                writer.flush();
                Toast.makeText(requireContext(), "Archivo guardado en:\n" + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(requireContext(), "Error al exportar tarea", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void shareTextFile(File file) {
        Uri uri = FileProvider.getUriForFile(requireContext(), "com.tzolas.todoist_aitzol.fileprovider", file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, getString(R.string.share_task_txt)));
    }


    // ---------------------------
    // NOTIFICACIONES
    // ---------------------------

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
        );
        channel.setDescription(getString(R.string.notification_channel_description));

        NotificationManager notificationManager = requireContext().getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showNotification(String message) {
        NotificationManager notificationManager = (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(getString(R.string.task_notification_title))
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify((int) (System.currentTimeMillis() % Integer.MAX_VALUE), builder.build());
    }
}
