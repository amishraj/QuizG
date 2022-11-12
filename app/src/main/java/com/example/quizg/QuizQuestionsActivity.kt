package com.example.quizg

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class QuizQuestionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_questions)

        val questionsList= Constants.getQuestions()
        Log.i("Questions size", "${questionsList.size}")

        val currentPosition=1
        val question:Question= questionsList[currentPosition-1]

        var progressBar = findViewById<ProgressBar>(R.id.progressBar) as ProgressBar
        progressBar.progress= currentPosition
        var tv_progress= findViewById<TextView>(R.id.tv_progress)
        tv_progress.text= "$currentPosition"+"/" + progressBar.max

        var tv_question= findViewById<TextView>(R.id.tv_question)
        tv_question.text= question.question

        var iv_image= findViewById<ImageView>(R.id.iv_image)
        if(iv_image!=null){
            iv_image.setImageResource(question.image)
        }

        var ll_main_layout= findViewById<LinearLayout>(R.id.ll_main_layout)
        //assign options
        for(i in 1..question.options.size){
//            var tvid= "tv_option_"+i
//            var resIDmt = resources.getIdentifier(tvid, "id", packageName)
//            var currOption:TextView = (findViewById(resIDmt))
//            currOption.text=question.options[i-1].text
            ll_main_layout.addView(createOptionTextView(question.options[i-1].text))
        }

        ll_main_layout.addView(createSubmitButton())

    }

    fun createOptionTextView(text:String):TextView{
        val tv_dynamic = TextView(this)

        val generatedId=View.generateViewId()
        tv_dynamic.setId(generatedId);
        tv_dynamic.textSize = 18f
        val margin = resources.getDimension(R.dimen.option_text_margin).toInt()
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(margin, margin, margin, margin)
        tv_dynamic.layoutParams = layoutParams
        val padding = resources.getDimension(R.dimen.option_text_padding).toInt()
        tv_dynamic.setPadding(padding, padding, padding, padding)
        tv_dynamic.gravity = Gravity.CENTER
        tv_dynamic.setTextColor(ContextCompat.getColor(this, R.color.greyText))
        tv_dynamic.background= ContextCompat.getDrawable(this, R.drawable.default_option_border_bg)
        tv_dynamic.text = text

        return tv_dynamic
    }

    fun createSubmitButton():Button{
        val submitButton= Button(this)
        submitButton.setId(R.id.btn_submit)
        val margin = resources.getDimension(R.dimen.option_text_margin).toInt()
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(margin, margin, margin, margin)
        submitButton.layoutParams = layoutParams
        submitButton.text="Submit"
        submitButton.setTextColor(ContextCompat.getColor(this, R.color.white))
        submitButton.textSize = 18f
        submitButton.setTypeface(Typeface.DEFAULT_BOLD);
        submitButton.background= ContextCompat.getDrawable(this, R.color.purple_500)

        return submitButton
    }
}