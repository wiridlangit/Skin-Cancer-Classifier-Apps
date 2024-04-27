package com.dicoding.asclepius.database

import androidx.room.*
import androidx.lifecycle.LiveData

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(note: Note)

    @Update
    fun update(note: Note)

    @Query("SELECT * FROM prediction_history")
    fun getAllPredictions(): LiveData<List<Note>>

    @Query("SELECT * FROM prediction_history WHERE timestamp = :timestamp")
    fun getNoteByTimestamp(timestamp: String): LiveData<Note?>

    @Query("DELETE FROM prediction_history WHERE id = :id")
    suspend fun deleteById(id: Int)
}
