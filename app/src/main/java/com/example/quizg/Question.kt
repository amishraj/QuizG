package com.example.quizg

data class Question (
    val id: Int,
    val question: String,
    val options: ArrayList<Option>,
    val correctAnswer: Option
)