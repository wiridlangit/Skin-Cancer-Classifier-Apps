package com.dicoding.asclepius.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.dicoding.asclepius.database.Note
import com.dicoding.asclepius.database.NoteDao

class NoteRepository(private val noteDao: NoteDao) {

    val allNotes: LiveData<List<Note>> = noteDao.getAllPredictions()

    @WorkerThread
    fun insert(note: Note) {
        noteDao.insert(note)
    }

    @WorkerThread
    suspend fun deleteById(id: Int) {
        noteDao.deleteById(id)
    }

}
