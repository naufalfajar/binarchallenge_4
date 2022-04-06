package id.nphew.binar.challenge4.database

import androidx.room.*

@Dao
interface NoteDao {
    @Query("SELECT * FROM Note")
    fun getAllNote() : List<Note>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNote(Note: Note) : Long

    @Update
    fun updateNote(Note: Note) : Int

    @Delete
    fun deleteNote(Note: Note) : Int
}