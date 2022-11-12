package com.example.quizg

object Constants{

    fun getQuestions():ArrayList<Question>{
        val questionsList= ArrayList<Question>();
        val options= ArrayList<Option>();
        val option= Option(1, "India")
        val option2= Option(2, "USA")
        val option3= Option(3,"South Africa")
        val option4= Option(4, "Australia")


        options.add(option)
        options.add(option2)
        options.add(option3)
        options.add(option4)

        val que1:Question= Question(1, "What country does this flag belong to?", R.drawable.flag, options, option3)
        val que2:Question= Question(2, "Who is the CEO of Apple?", R.drawable.flag, options, option2)

        questionsList.add(que1)
        questionsList.add(que2)
        return questionsList
    }
}