package com.dicoding.asclepius.view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.asclepius.database.Note
import com.dicoding.asclepius.repository.NoteRepository
import com.dicoding.asclepius.database.NoteRoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NoteRepository
    val allNotes: LiveData<List<Note>>

    init {
        val notesDao = NoteRoomDatabase.getDatabase(application).noteDao()
        repository = NoteRepository(notesDao)
        allNotes = repository.allNotes
    }

    fun insert(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(note)
        }
    }

    fun delete(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteById(id)
    }

}
