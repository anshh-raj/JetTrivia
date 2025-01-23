package com.example.jettrivia.component

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jettrivia.model.QuestionIndex
import com.example.jettrivia.model.QuestionItem
import com.example.jettrivia.screens.QuestionsViewModel
import com.example.jettrivia.util.AppColors

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun Questions(viewModel: QuestionsViewModel) {
    val questions = viewModel.data.value.data?.toMutableList()

    if(viewModel.data.value.loading == true){
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
    }
    else{
        val index = viewModel.index.value

//        Log.d("index_value", "Questions: ${index!!.index}")

        val questionIndex = remember {
            mutableIntStateOf(index!!.index)
        }

        val question = try {
            questions?.get(questionIndex.intValue)
        } catch (ex:Exception){
            null
        }
        if (questions != null){
            QuestionDisplay(
                question = question!!,
                questionIndex = questionIndex,
                viewModel = viewModel,
                onNextClick = {
                    if (questionIndex.intValue < questions.size - 1)
                        questionIndex.intValue += 1
                    viewModel.addIndex(
                        QuestionIndex(
                            index = questionIndex.intValue
                        )
                    )
                },
                onBackClick = {
                    if(questionIndex.intValue > 0)
                        questionIndex.intValue -= 1
                    viewModel.addIndex(
                        QuestionIndex(
                            index = questionIndex.intValue
                        )
                    )
                }
            )
        }
    }
}

//@Preview
@Composable
fun QuestionDisplay(
    question: QuestionItem,
    questionIndex: MutableState<Int>,
    viewModel: QuestionsViewModel,
    onNextClick:() -> Unit = {},
    onBackClick:() -> Unit = {},
){

    val choicesState = remember(question){
        question.choices.toMutableList()
    }

    val answerState = remember(question) {
        mutableStateOf<Int?>(null)
    }

    val correctAnswerState = remember(question) {
        mutableStateOf<Boolean?>(null)
    }

    val updateAnswer: (Int) -> Unit = remember(question) {
        {
            answerState.value = it
            correctAnswerState.value = choicesState[it] == question.answer
        }
    }
    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f,10f), 0f)
    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = AppColors.mDarkPurple
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ){
            ShowProgress(questionIndex.value + 1)

            QuestionTracker(
                counter = questionIndex.value + 1,
                outOf = viewModel.data.value.data!!.size
            )
            DrawDottedLine(pathEffect)

            Column {
                Text(
                    text = question.question,
                    modifier = Modifier
                        .padding(6.dp)
                        .align(Alignment.Start)
                        .fillMaxHeight(0.3f),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 22.sp,
                    color = AppColors.mOffWhite
                )

                //choices
                choicesState.forEachIndexed{index, answerText ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(3.dp)
                            .border(
                                width = 4.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        AppColors.mOffDarkPurple,
                                        AppColors.mOffDarkPurple
                                    )
                                ),
                                shape = RoundedCornerShape(15.dp)
                            )
                            .clip(
                                RoundedCornerShape(
                                    topStartPercent = 50,
                                    topEndPercent = 50,
                                    bottomStartPercent = 50,
                                    bottomEndPercent = 50
                                )
                            )
                            .background(Color.Transparent),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (answerState.value == index),
                            onClick = {
                                updateAnswer(index)
                            },
                            modifier = Modifier
                                .padding(start = 16.dp),
                            colors = RadioButtonDefaults.colors(
                                selectedColor = if(correctAnswerState.value == true
                                    && index == answerState.value){
                                    Color.Green.copy(0.2f)
                                } else {
                                    Color.Red.copy(0.2f)
                                }
                            )
                        )

                        val annotatedString = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Light,
                                    color = if(correctAnswerState.value == true
                                        && index == answerState.value){
                                        Color.Green
                                    } else if(correctAnswerState.value == false
                                        && index == answerState.value){
                                        Color.Red
                                    } else{
                                        AppColors.mOffWhite
                                    },
                                    fontSize = 17.sp
                                )
                            ){
                                append(answerText)
                            }
                        }

                        Text(
                            annotatedString,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Button(
                        onClick = {
                            onBackClick()
                        },
                        modifier = Modifier
                            .padding(3.dp),
                        shape = RoundedCornerShape(34.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppColors.mLightBlue
                        )
                    ) {
                        Text(
                            "Back",
                            modifier = Modifier
                                .padding(4.dp),
                            color = AppColors.mOffWhite,
                            fontSize = 17.sp
                        )
                    }
                    Button(
                        onClick = {
                            onNextClick()
                        },
                        modifier = Modifier
                            .padding(3.dp),
                        shape = RoundedCornerShape(34.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppColors.mLightBlue
                        )
                    ) {
                        Text(
                            "Next",
                            modifier = Modifier
                                .padding(4.dp),
                            color = AppColors.mOffWhite,
                            fontSize = 17.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DrawDottedLine(pathEffect: PathEffect){
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {
        drawLine(
            color = AppColors.mLightGray,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            pathEffect = pathEffect
        )
    }
}


@Composable
fun QuestionTracker(counter: Int = 10, outOf: Int = 100){
    Text(
        text = buildAnnotatedString {
            withStyle(
                style = ParagraphStyle(
                    textIndent = TextIndent.None
                )
            ){
                withStyle(
                    style = SpanStyle(
                        color = AppColors.mLightGray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 27.sp
                    )
                ){
                    append("Question $counter/")

                    withStyle(
                        style = SpanStyle(
                            color = AppColors.mLightGray,
                            fontWeight = FontWeight.Light,
                            fontSize = 14.sp
                        )
                    ){
                        append("$outOf")
                    }
                }
            }
        },
        modifier = Modifier
            .padding(20.dp)
    )
}

@Composable
fun ShowProgress(score:Int){

    val gradient = Brush.linearGradient(
        listOf(
            Color(0xFFF95075),
            Color(0xFFBE6BE5)
        )
    )

    val progressFactor by remember(score) {
        mutableFloatStateOf(score * 0.002f)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp)
            .height(45.dp)
            .border(
                width = 4.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        AppColors.mLightPurple,
                        AppColors.mLightPurple
                    )
                ),
                shape = RoundedCornerShape(34.dp)
            )
            .clip(
                RoundedCornerShape(
                    topStartPercent = 50,
                    topEndPercent = 50,
                    bottomStartPercent = 50,
                    bottomEndPercent = 50
                )
            )
            .background(Color.Transparent),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            contentPadding = PaddingValues(1.dp),
            onClick = {},
            modifier = Modifier
                .fillMaxWidth(progressFactor)
                .background(brush = gradient),
            enabled = false,
            elevation = null,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent
            )
        ) {
//            Text(
//                (score*10).toString(),
//                modifier = Modifier
//                    .clip(RoundedCornerShape(23.dp))
//                    .fillMaxHeight(0.87f)
//                    .fillMaxWidth()
//                    .padding(6.dp),
//                color = AppColors.mOffWhite,
//                textAlign = TextAlign.Center
//            )
        }
    }
}












