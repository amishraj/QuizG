package com.example.quizg

import android.util.Log
import android.widget.Toast
import java.util.*
import kotlin.collections.ArrayList
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
const val algorithm = "AES/CBC/PKCS5Padding"
val key = SecretKeySpec("1234567890123456".toByteArray(), "AES")
val iv = IvParameterSpec(ByteArray(16))

object Constants{

    const val USER_NAME:String="user_name"
    const val TOTAL_QUESTIONS:String="total_question"
    const val CORRECT_ANSWERS:String= "correct_answers"
    const val NAME_OF_USER:String="name_of_user"
    const val CURRENT_QUIZ_TITLE:String="current_quiz_title"

    private val listOfQuizzes:ArrayList<Quiz>?= arrayListOf()

    fun getQuestions(quizTitle :String): ArrayList<Question>? {
        for(quiz in listOfQuizzes!!){
            if(quiz.title== quizTitle){
                //shuffle the options for each question in the quiz
                for(question in quiz.questions){
                    Collections.shuffle(question.options)
                }
                return quiz.questions
            }
        }

        return null
    }

    fun getQuizzes(courseName:String): ArrayList<Quiz> {
        //TODO: dummy data here, to be replaced by database fetched quizzes
        listOfQuizzes?.clear()

        val questionsList1= ArrayList<Question>()
        val questionsList2= ArrayList<Question>()

        val options1= ArrayList<Option>()
        val options2= ArrayList<Option>()
        val options3= ArrayList<Option>()
        val options4= ArrayList<Option>()
        val options5= ArrayList<Option>()

        val option1= Option(1, "New Delhi")
        val option2= Option(2, "Jaipur")
        val option3= Option(3,"Bangalore")
        val option4= Option(4, "Kolkata")
        options1.add(option1); options1.add(option2); options1.add(option3); options1.add(option4)

        val option5= Option(5, "120")
        val option6= Option(6, "360")
        val option7= Option(7,"365")
        val option8= Option(8, "365.25")
        options2.add(option5); options2.add(option6); options2.add(option7); options2.add(option8)

        val option9= Option(9, "Samsung")
        val option10= Option(10, "Sony")
        val option11= Option(11,"Nokia")
        options3.add(option9); options3.add(option10); options3.add(option11)

        val option12= Option(12, "China")
        val option13= Option(13, "Turkey")
        val option14= Option(14, "Greece")
        val option15= Option(15, "India")
        val option16= Option(16, "Nepal")
        options4.add(option12); options4.add(option13); options4.add(option14); options4.add(option15); options4.add(option16)

        val option17= Option(17, "Greek")
        val option18= Option(18, "Russian")
        val option19= Option(19, "Arabic")
        val option20= Option(20, "Spanish")
        options5.add(option17); options5.add(option18); options5.add(option19); options5.add(option20)

        val que1= Question(1, "What is the capital of India?", options1, option1)
        val que2= Question(2, "How many days are there in a year?", options2, option8)
        val que3= Question(3, "What company makes the Xperia model of smartphone?", options3, option10)
        val que4= Question(4, "Where was the first example of paper money used?", options4, option12)
        val que5= Question(5, "Which of the following languages has the longest alphabet?", options5, option18)

        questionsList1.add(que1);  questionsList1.add(que2);  questionsList1.add(que3)
        questionsList2.add(que4); questionsList2.add(que5)

        val quiz1= Quiz(1, "Graphs and Trees", questionsList1, "CSCI6212", "Design and Analysis of Algorithms", 3)
        val quiz2= Quiz(2, "Kotlin and its Benefits", questionsList2, "CSCI6221", "Advanced Software Paradigms", 3)
        val quiz3= Quiz(3, "Scrum and Agile", questionsList1, "CSCI6221", "Advanced Software Paradigms", 3)
        val quiz4= Quiz(4, "Cache Registers", questionsList2, "CSCI6461", "Computer Architectures", 3)
        val quiz5= Quiz(5, "Scrum and Agile3", questionsList1, "CSCI6221", "Advanced Software Paradigms", 3)
        val quiz6= Quiz(6, "Scrum and Agile1", questionsList1, "CSCI6221", "Advanced Software Paradigms", 3)
        val quiz7= Quiz(7, "Scrum and Agile2", questionsList1, "CSCI6221", "Advanced Software Paradigms", 3)


        listOfQuizzes?.add(quiz1); listOfQuizzes?.add(quiz2); listOfQuizzes?.add(quiz3); listOfQuizzes?.add(quiz4)

        //code starts here
        val tempQuizList= ArrayList<Quiz>()
        tempQuizList.clear()
        for(quiz in listOfQuizzes!!){
            if(quiz.courseName== courseName){
                tempQuizList.add(quiz)
            }
        }

        return tempQuizList

    }

    fun getAllCourses(): Array<String> {
        var courseList= arrayOf<String>()
        var quizList= getQuizzes("Design and Analysis of Algorithms")

        if (listOfQuizzes != null) {
            for(quiz in listOfQuizzes){
                if(!courseList.contains(quiz.courseName)){
                    courseList+= quiz.courseName
                }
            }
            return courseList
        }
        return courseList
    }
}