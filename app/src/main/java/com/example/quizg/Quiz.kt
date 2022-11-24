package com.example.quizg

data class Quiz (
    val id:Int,
    val title:String,
    val questions: ArrayList<Question>,
    val courseCode:String,
    val courseName:String,
    val attempts:Int
    )