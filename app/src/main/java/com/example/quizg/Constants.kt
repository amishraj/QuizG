package com.example.quizg

import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.*
import java.lang.Double.parseDouble
import java.util.*
import kotlin.collections.ArrayList
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.HashMap

const val algorithm = "AES/CBC/PKCS5Padding"
val key = SecretKeySpec("1234567890123456".toByteArray(), "AES")
val iv = IvParameterSpec(ByteArray(16))
private lateinit var reference : DatabaseReference
private lateinit var referencetemp : DatabaseReference

object Constants{

    const val USER_NAME:String="user_name"
    const val UNIVERSITY:String="university"
    const val COURSE:String="course"
    const val QNo: String ="1"
    const val TOTAL_QUESTIONS:String="total_question"
    const val CORRECT_ANSWERS:String= "correct_answers"
    const val NAME_OF_USER:String="name_of_user"
    const val PROF_NAME:String="professor_name"
    const val CURRENT_QUIZ_TITLE:String="current_quiz_title"

    private val listOfQuizzes:ArrayList<Quiz>?= arrayListOf()

    fun getQuestions(quizTitle :String): ArrayList<Question>? {
        for(quiz in listOfQuizzes!!){
            //shuffle the options for each question in the quiz
            for(question in quiz.questions){
                Collections.shuffle(question.options)
            }
            return quiz.questions
        }

        return null
    }

    fun getQuizzes(universityName:String, courseName:String, professorName:String, quizName:String, professorUserName:String) {
        //TODO: dummy data here, to be replaced by database fetched quizzes
        listOfQuizzes?.clear()

        val questionsList= ArrayList<Question>()

        reference = FirebaseDatabase.getInstance().getReference("Quiz");
        reference?.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot){
                for(ds in snapshot.child(universityName.toString()).child(courseName.toString()).child(professorUserName.toString()).child(quizName.toString()).children){
                    val QNo = ds.key.toString()
                    var numeric = true

                    try {
                        val num = Integer.parseInt(QNo)
                    } catch (e: NumberFormatException) {
                        numeric = false
                    }

                    if(numeric){
                        val questionText = ds.child("question").value
                        val options= ArrayList<Option>()
                        val correctOption = Integer.parseInt(ds.child("correctAnswer").child("id").value.toString())
                        var correctAnswer = Option(1,"")
                        for(optionsVar in ds.child("options").children){
                            var key = optionsVar.key
                            var OptionId = Integer.parseInt(optionsVar.child("id").value.toString())
                            var OptionText = optionsVar.child("text").value
                            val option = Option(OptionId, OptionText.toString())
                            options.add(option)
                            if(correctOption==OptionId){
                                correctAnswer.id = correctOption as Int
                                correctAnswer.text = OptionText.toString()
                            }
                        }
                        val question= Question(Integer.parseInt(QNo),
                            questionText.toString(),options,correctAnswer)
                        questionsList.add(question)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
/*
        val options1= ArrayList<Option>()
        val options2= ArrayList<Option>()
        val options3= ArrayList<Option>()

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

        val que1= Question(1, "What is the capital of India?", options1, option1)
        val que2= Question(2, "How many days are there in a year?", options2, option8)
        val que3= Question(3, "What company makes the Xperia model of smartphone?", options3, option10)
        questionsList.add(que1);  questionsList.add(que2);  questionsList.add(que3)*/
        val quiz= Quiz(1, quizName, questionsList, professorName, courseName, 3)
        listOfQuizzes?.add(quiz);

        /*val questionsList1= ArrayList<Question>()
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

        return tempQuizList*/

    }

    fun getAllCourses(mUniversity: String?): MutableList<String> {
        /*var courseList= arrayOf<String>()
        var quizList= getQuizzes("Design and Analysis of Algorithms")

        if (listOfQuizzes != null) {
            for(quiz in listOfQuizzes){
                if(!courseList.contains(quiz.courseName)){
                    courseList+= quiz.courseName
                }
            }
            return courseList
        }
        return courseList*/
        var Courses= mutableListOf<String>()
        reference = FirebaseDatabase.getInstance().getReference("Universities");
        reference?.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot){
                for(ds in snapshot.child(mUniversity.toString()).child("Courses").children){
                    val courseName = ds.key.toString()
                    Courses.add(courseName.toString())
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        return Courses
    }

    fun getAllProfessors(mUniversity: String?,courseSelection: String?): MutableList<String> {
        /*var courseList= arrayOf<String>()
        var quizList= getQuizzes("Design and Analysis of Algorithms")

        if (listOfQuizzes != null) {
            for(quiz in listOfQuizzes){
                if(!courseList.contains(quiz.courseName)){
                    courseList+= quiz.courseName
                }
            }
            return courseList
        }
        return courseList*/
        var profs_usernames= mutableListOf<String>()
        var profs_names= mutableListOf<String>()
        reference = FirebaseDatabase.getInstance().getReference("Quiz");
        reference?.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot){
                for(ds in snapshot.child(mUniversity.toString()).child(courseSelection.toString()).children){
                    val prof_userName = ds.key.toString()
                    profs_usernames.add(prof_userName.toString())
                    referencetemp = FirebaseDatabase.getInstance().getReference("Users");
                    referencetemp.child("Professor").child(prof_userName).get()
                        .addOnCompleteListener(OnCompleteListener<DataSnapshot?> { task ->
                            if (task.isSuccessful) {
                                if (task.result.exists()) {
                                    val dataSnapshot = task.result
                                    val firstname = dataSnapshot.child("firstname").value.toString()
                                    val lastname = dataSnapshot.child("lastname").value.toString()
                                    val name=firstname+" "+lastname
                                    profs_names.add(name)
                                }
                            }
                        })
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        return profs_names
    }
}