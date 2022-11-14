package com.example.quizg

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.decorView.systemUiVisibility= View.SYSTEM_UI_FLAG_FULLSCREEN;

        val btn_start:Button= findViewById(R.id.btn_start);
        btn_start.setOnClickListener{StartClick()};
    }

    private fun StartClick(){
        val edit_text: TextInputEditText= findViewById(R.id.edit_text);
        if(edit_text.text.toString().isEmpty()){
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
        } else{
            val intent  = Intent(this, QuizQuestionsActivity::class.java)
            intent.putExtra(Constants.USER_NAME, edit_text.text.toString())
            startActivity(intent)
            finish()
        }
    }
}