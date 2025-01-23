package com.example.jettrivia.screens

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jettrivia.data.DataOrException
import com.example.jettrivia.model.QuestionIndex
import com.example.jettrivia.model.QuestionItem
import com.example.jettrivia.repository.QuestionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionsViewModel @Inject constructor(private val repository: QuestionRepository): ViewModel() {
    val data: MutableState<DataOrException<ArrayList<QuestionItem>, Boolean, Exception>> = mutableStateOf(
        DataOrException(null,true, Exception(""))
    )

    private val _index = MutableStateFlow<QuestionIndex?>(
        QuestionIndex(
            index = 0
        )
    )

    val index = _index.asStateFlow()

    init {
        getAllQuestions()
        getIndex()
    }

    private fun getIndex(){
        viewModelScope.launch {
            repository.getIndex().distinctUntilChanged().collect{index ->
                _index.value = index ?: QuestionIndex(
                    index = 0
                )
            }
        }
    }

    private fun getAllQuestions(){
        viewModelScope.launch {
            data.value.loading = true
            data.value = repository.getAllQuestions()
            if(data.value.data.toString().isNotEmpty()) data.value.loading = false
        }
    }

    fun addIndex(questionIndex: QuestionIndex) = viewModelScope.launch {
        repository.deleteAll()
        repository.addIndex(questionIndex)
    }
}