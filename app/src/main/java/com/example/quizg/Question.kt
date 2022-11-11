package com.example.quizg

data class Question (
    val id: Int,
    val question: String,
    val image: Int,
    val options: List<Option>,
    val correctAnswer: Option
)