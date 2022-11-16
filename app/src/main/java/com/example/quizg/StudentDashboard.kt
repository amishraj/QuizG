package com.example.quizg

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class StudentDashboard : AppCompatActivity() {

    private lateinit var autoCompleteTextViewCourse: AutoCompleteTextView
    private lateinit var autoCompleteTextViewQuiz: AutoCompleteTextView

    private var mUsername:String?= null
    private var courseSelection:String?=null
    private var quizSelection:String?=null

    private var quizList:ArrayList<Quiz>?=null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_dashboard)

        //get student username from previous activity
        mUsername= intent.getStringExtra(Constants.USER_NAME)

        //set welcome text
        var tv_student_name= findViewById<TextView>(R.id.tv_student_name)
        tv_student_name.text="Welcome "+mUsername

        //set Course dropdown
        autoCompleteTextViewCourse= findViewById(R.id.autoCompleteTextCourse)
        var optionCourse= Constants.getAllCourses()
        var arrayAdapterCourse= ArrayAdapter(this, R.layout.option_item, optionCourse)
        autoCompleteTextViewCourse.setText(arrayAdapterCourse.getItem(0).toString(), false)
        autoCompleteTextViewCourse.setAdapter(arrayAdapterCourse)

        //default course selection
        courseSelection=arrayAdapterCourse.getItem(0).toString()

        //course dropdown click listener
        autoCompleteTextViewCourse.setOnItemClickListener(OnItemClickListener { parent, view, position, rowId ->
            courseSelection = parent.getItemAtPosition(position) as String
            quizList= Constants.getQuizzes(courseSelection!!)
            setQuizDropdown()
        })

        quizList= Constants.getQuizzes(courseSelection!!)
        setQuizDropdown()

        //goto quiz button
        var btn_goToQuiz= findViewById<Button>(R.id.btn_goToQuiz)
        btn_goToQuiz.setOnClickListener{
            val intent = Intent(this, QuizQuestionsActivity::class.java)
            intent.putExtra(Constants.USER_NAME, mUsername)
            intent.putExtra(Constants.CURRENT_QUIZ_TITLE, quizSelection)
            startActivity(intent)
        }
    }

    private fun setQuizDropdown() {
        //set Quiz dropdown
        autoCompleteTextViewQuiz = findViewById(R.id.autoCompleteTextQuiz)
        var optionQuiz= arrayOf<String>()
        optionQuiz = getQuizTitles(quizList!!)

        var arrayAdapterQuiz = ArrayAdapter(this, R.layout.option_item, optionQuiz)
        autoCompleteTextViewQuiz.setText(arrayAdapterQuiz.getItem(0).toString(), false)
        autoCompleteTextViewQuiz.setAdapter(arrayAdapterQuiz)

        //default quiz selection
        quizSelection= arrayAdapterQuiz.getItem(0).toString()

        //quiz dropdown click listener
        autoCompleteTextViewQuiz.setOnItemClickListener(OnItemClickListener { parent, view, position, rowId ->
            quizSelection = parent.getItemAtPosition(position) as String
        })
    }

    private fun getQuizTitles(quizList: ArrayList<Quiz>): Array<String> {
        var quizTitles= arrayOf<String>()

        for(quiz in quizList){
            quizTitles+= quiz.title
        }

        return quizTitles
    }
}