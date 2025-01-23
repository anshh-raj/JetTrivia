package com.example.jettrivia.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.jettrivia.model.QuestionIndex
import kotlinx.coroutines.flow.Flow

@Dao
interface IndexDatabaseDao {
    @Query("SELECT * FROM index_tbl")
    fun getIndex(): Flow<QuestionIndex?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(questionIndex: QuestionIndex)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(questionIndex: QuestionIndex)

    @Query("DELETE FROM index_tbl")
    suspend fun deleteAll()

    @Delete
    suspend fun deleteIndex(questionIndex: QuestionIndex)
}