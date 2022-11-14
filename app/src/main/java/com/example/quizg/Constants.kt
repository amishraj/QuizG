package com.example.quizg

object Constants{

    const val USER_NAME:String="user_name"
    const val TOTAL_QUESTIONS:String="total_question"
    const val CORRECT_ANSWERS:String= "correct_answers"

    fun getQuestions():ArrayList<Question>{
        val questionsList= ArrayList<Question>()
        val options= ArrayList<Option>()
        val options2= ArrayList<Option>()

        val option= Option(1, "India")
        val option2= Option(2, "USA")
        val option3= Option(3,"South Africa")
        val option4= Option(4, "Australia")

        val option5= Option(1, "Tim Cook")
        val option6= Option(2, "Satya Nadela")
        val option7= Option(3,"Bill Gates")
        val option8= Option(4, "Mark Zuckerberg")

        options.add(option)
        options.add(option2)
        options.add(option3)
        options.add(option4)

        options2.add(option5)
        options2.add(option6)
        options2.add(option7)
        options2.add(option8)

        val que1:Question= Question(1, "What country does this flag belong to?", options, option3)
        val que2:Question= Question(2, "Who is the CEO of Apple?", options2, option5)

        questionsList.add(que1)
        questionsList.add(que2)
        return questionsList
    }
}