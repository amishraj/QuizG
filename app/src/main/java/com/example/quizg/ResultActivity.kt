package com.example.quizg

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        window.decorView.systemUiVisibility= View.SYSTEM_UI_FLAG_FULLSCREEN;

        val username= intent.getStringExtra(Constants.USER_NAME)
        val tv_name= findViewById<TextView>(R.id.tv_name)
        tv_name.text= username

        val totalQuestions= intent.getIntExtra(Constants.TOTAL_QUESTIONS,0)
        val correctAnswers= intent.getIntExtra(Constants.CORRECT_ANSWERS,0)

        val tv_score= findViewById<TextView>(R.id.tv_score)
        tv_score.text = "Your score is $correctAnswers out of $totalQuestions"

        val btn_finish= findViewById<Button>(R.id.btn_finish)
        btn_finish.setOnClickListener{
            finish()
        }
    }
}