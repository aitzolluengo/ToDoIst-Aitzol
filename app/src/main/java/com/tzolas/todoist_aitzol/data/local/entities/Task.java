package com.tzolas.todoist_aitzol.data.local.entities;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import java.util.Objects;

@Entity(tableName = "task_table")
public class Task implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String description;
    private boolean isCompleted;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    // Constructor vacío requerido por Room
    public Task() {}

    // Constructor usado en la app, ignorado por Room
    @Ignore
    public Task(String title, String description, boolean isCompleted) {
        this.title = title;
        this.description = description;
        this.isCompleted = isCompleted;
        this.createdAt = System.currentTimeMillis();
    }

    // Métodos getter y setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    // Métodos Parcelable
    protected Task(Parcel in) {
        id = in.readInt();
        title = in.readString();
        description = in.readString();
        isCompleted = in.readByte() != 0;
        createdAt = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeByte((byte) (isCompleted ? 1 : 0));
        dest.writeLong(createdAt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    // Métodos equals y hashCode para comparar tareas
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return id == task.id &&
                isCompleted == task.isCompleted &&
                Objects.equals(title, task.title) &&
                Objects.equals(description, task.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, isCompleted);
    }
}
