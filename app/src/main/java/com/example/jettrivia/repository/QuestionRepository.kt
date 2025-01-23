package com.example.jettrivia.repository

import android.util.Log
import com.example.jettrivia.data.DataOrException
import com.example.jettrivia.data.IndexDatabaseDao
import com.example.jettrivia.model.QuestionIndex
import com.example.jettrivia.model.QuestionItem
import com.example.jettrivia.network.QuestionApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class QuestionRepository @Inject constructor(private val api: QuestionApi, private val indexDatabaseDao: IndexDatabaseDao) {
    private val dataOrException = DataOrException<ArrayList<QuestionItem>, Boolean, Exception>()

    suspend fun getAllQuestions(): DataOrException<ArrayList<QuestionItem>, Boolean, Exception>{
        try {
            dataOrException.loading = true
            dataOrException.data = api.getAllQuestion()
            if(dataOrException.data.toString().isNotEmpty()) dataOrException.loading = false

        }catch (exception: Exception){
            dataOrException.e = exception
            Log.d("Exc", "getAllQuestions: ${dataOrException.e!!.localizedMessage}")
        }
        return dataOrException
    }

    fun getIndex(): Flow<QuestionIndex?> = indexDatabaseDao.getIndex().flowOn(Dispatchers.IO).conflate()
    suspend fun addIndex(questionIndex: QuestionIndex) = indexDatabaseDao.insert(questionIndex)
    suspend fun updateIndex(questionIndex: QuestionIndex) = indexDatabaseDao.update(questionIndex)
    suspend fun deleteIndex(questionIndex: QuestionIndex) = indexDatabaseDao.deleteIndex(questionIndex)
    suspend fun deleteAll() = indexDatabaseDao.deleteAll()

}