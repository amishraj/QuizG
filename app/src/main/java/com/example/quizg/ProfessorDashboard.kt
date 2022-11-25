package com.example.quizg

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*

class ProfessorDashboard : AppCompatActivity() {

    private lateinit var autoCompleteTextViewCourse: AutoCompleteTextView
    private lateinit var autoCompleteTextViewQuiz: AutoCompleteTextView

    private var mUsername:String?= null
    private var courseSelection:String?=null
    private var quizSelection:String?=null

    private var quizList:ArrayList<Quiz>?=null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_professor_dashboard)

        mUsername = intent.getStringExtra(Constants.USER_NAME)

        //set welcome text
        var tv_professor_name = findViewById<TextView>(R.id.tv_welcome_prof)
        tv_professor_name.text = "Welcome " + mUsername

        //set Course dropdown
        autoCompleteTextViewCourse = findViewById(R.id.autoCompleteTextCourse)
        var optionCourse = Constants.getAllCourses()
        var arrayAdapterCourse = ArrayAdapter(this, R.layout.option_item, optionCourse)
        autoCompleteTextViewCourse.setText(arrayAdapterCourse.getItem(0).toString(), false)
        autoCompleteTextViewCourse.setAdapter(arrayAdapterCourse)

        //default course selection
        courseSelection = arrayAdapterCourse.getItem(0).toString()

        val btn_goToQuizCreation = findViewById<Button>(R.id.create_quiz)
        btn_goToQuizCreation.setOnClickListener {
            val intent = Intent(this, CreateQuiz::class.java)
            intent.putExtra(Constants.USER_NAME, mUsername)
            intent.putExtra(Constants.CURRENT_QUIZ_TITLE, quizSelection)
            Toast.makeText(this, "Setting new quiz", Toast.LENGTH_SHORT).show();
            startActivity(intent)
        }
    }
}