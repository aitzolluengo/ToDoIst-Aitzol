package com.tzolas.todoist_aitzol.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.tzolas.todoist_aitzol.data.local.entities.Task;
import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    void insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Query("SELECT * FROM task_table ORDER BY id ASC")
    LiveData<List<Task>> getAllTasks();

    @Query("DELETE FROM task_table WHERE isCompleted = 1")
    void deleteAllCompleted();

    @Query("SELECT * FROM task_table ORDER BY title ASC")
    LiveData<List<Task>> getTasksSortedByName();

    @Query("SELECT * FROM task_table ORDER BY created_at DESC")
    LiveData<List<Task>> getTasksSortedByDate();



}
