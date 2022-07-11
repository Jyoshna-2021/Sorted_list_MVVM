package com.example.sorted_list_view

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NotesDao {
    @Insert
    suspend fun insertNote(notes: Notes)
    @Delete
    suspend fun deleteNode(notes: Notes)
    @Query("SELECT * FROM notes ORDER BY LOWER(title) ASC")

    fun getNotes(): LiveData<List<Notes>>
}
