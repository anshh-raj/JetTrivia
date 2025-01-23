package com.example.jettrivia.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.jettrivia.model.QuestionIndex


@Database(entities = [QuestionIndex::class], version = 1, exportSchema = false)
abstract class IndexDatabase: RoomDatabase() {
    abstract fun indexDao(): IndexDatabaseDao
}