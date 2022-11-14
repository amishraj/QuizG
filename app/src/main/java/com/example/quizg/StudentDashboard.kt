package com.example.quizg

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class StudentDashboard : AppCompatActivity() {

    private var mUsername:String?= null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_dashboard)

        mUsername= intent.getStringExtra(Constants.USER_NAME)

        var btn_goToQuiz= findViewById<Button>(R.id.btn_goToQuiz)
        btn_goToQuiz.setOnClickListener{
            val intent = Intent(this, QuizQuestionsActivity::class.java)
            intent.putExtra(Constants.USER_NAME, mUsername)
            startActivity(intent)
        }
    }
}