package com.tzolas.todoist_aitzol.ui.tasklist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.tzolas.todoist_aitzol.R;
import com.tzolas.todoist_aitzol.data.local.entities.Task;

public class TaskAdapter extends ListAdapter<Task, TaskAdapter.TaskViewHolder> {
    private final OnTaskClickListener listener;

    public TaskAdapter(OnTaskClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Task> DIFF_CALLBACK = new DiffUtil.ItemCallback<Task>() {
        @Override
        public boolean areItemsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
            return oldItem.equals(newItem);
        }
    };

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = getItem(position); // Asegúrate de obtener el objeto correcto
        holder.taskTitle.setText(task.getTitle());
        holder.taskDescription.setText(task.getDescription());

        holder.itemView.setOnClickListener(v -> listener.onTaskClick(task));
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView taskTitle;
        private final TextView taskDescription;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.taskTitle);
            taskDescription = itemView.findViewById(R.id.taskDescription);
        }
    }

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
    }
}
