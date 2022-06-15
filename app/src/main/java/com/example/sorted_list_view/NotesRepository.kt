package com.example.sorted_list_view

import androidx.lifecycle.LiveData

class NotesRepository(private val notesDao: NotesDao) {
    suspend fun insertNotes(notes: Notes) {
        notesDao.insertNote(notes)
    }

    suspend fun deleteNode(notes: Notes) = notesDao.deleteNode(notes)
    fun getNotes(): LiveData<List<Notes>> {
        return notesDao.getNotes()
    }
}


