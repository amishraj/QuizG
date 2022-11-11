package com.example.quizg

object Constants{

    fun getQuestions():ArrayList<Question>{
        val questionsList= ArrayList<Question>();
        val options= ArrayList<Option>();
        val que1:Question= Question(1, "What country does this flag belong to?", 1, options, Option(1, "India"))

        questionsList.add(que1)
        return questionsList
    }
}