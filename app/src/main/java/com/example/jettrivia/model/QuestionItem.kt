package com.example.jettrivia.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class QuestionItem(
    val answer: String,
    val category: String,
    val choices: List<String>,
    val question: String
)


@Entity(tableName = "index_tbl")
data class QuestionIndex(
    @PrimaryKey
    val index: Int
)