package com.example.quizg

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.w3c.dom.Text
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class QuizQuestionsActivity : AppCompatActivity(), OnClickListener {

    private var mCurrentPosition:Int =1
    private var mQuestionsList:ArrayList<Question>?=null
    private var mSelectedOptionPosition:Int=0
    private var optionsList=ArrayList<TextView>()
    private var mCorrectAnswers: Int=0
    private var mUsername:String?=null
    private var correctStatus:Boolean=false
    private var submittedStatus: Boolean=false
    private var mUniversity:String?=null
    private var courseSelected:String?=null
    private var mProfessorUN:String?=null
    private var quizSelection:String?=null
    private var mName:String?=null

    private lateinit var database : DatabaseReference
    private lateinit var reference : DatabaseReference

    private var generatedOptionID:Int=1

    private var timerStarted=false
    private lateinit var serviceIntent: Intent
    private var time= 0.0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_questions)

        mUniversity = intent.getStringExtra(Constants.UNIVERSITY)
        courseSelected = intent.getStringExtra(Constants.COURSE)
        mProfessorUN = intent.getStringExtra(Constants.PROF_UNAME)
        quizSelection = intent.getStringExtra(Constants.CURRENT_QUIZ_TITLE)
        mName =intent.getStringExtra(Constants.NAME_OF_USER)

        //timer
        serviceIntent = Intent(applicationContext, TimerService::class.java)
        registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))
        resetTimer()
        startStopTimer()

        mUsername= intent.getStringExtra(Constants.USER_NAME)
        var currentQuizTitle= intent.getStringExtra(Constants.CURRENT_QUIZ_TITLE)

        mQuestionsList= Constants.getQuestions(currentQuizTitle.toString())
        setQuestion()

        for(option in optionsList){
            option.setOnClickListener(this)
        }

        var btn_submit= findViewById<Button>(R.id.btn_submit)
        btn_submit.setOnClickListener(this)
    }

    fun setQuestion(){
        optionsList.clear()
        submittedStatus=false
        val question= mQuestionsList!![mCurrentPosition-1]

        //progress bar
        var progressBar = findViewById<ProgressBar>(R.id.progressBar) as ProgressBar
        progressBar.progress= mCurrentPosition
        progressBar.max= mQuestionsList!!.size
        var tv_progress= findViewById<TextView>(R.id.tv_progress)
        tv_progress.text= "$mCurrentPosition"+"/" + progressBar.max

        //set question text
        var tv_question= findViewById<TextView>(R.id.tv_question)
        tv_question.text= question.question

        var ll_main_layout= findViewById<LinearLayout>(R.id.ll_main_layout)

        //remove unwanted views
        removeUnwantedViews(question)

        //assign options
        if(mCurrentPosition>1){ //checking whether option text views have already been created
            for(i in 1..question.options.size){
                var tv_dynamic= findViewById<TextView>(i)
                tv_dynamic.text= question.options[i-1].text
                optionsList.add(tv_dynamic)
            }
        } else{
            for(i in 1..question.options.size){
                ll_main_layout.addView(createOptionTextView(question.options[i-1].text, question.options.size))
            }
            ll_main_layout.addView(createSubmitButton())
        }

        defaultOptionsView()
    }

    fun defaultOptionsView(){
        for(option in optionsList){
            val textColor = ContextCompat.getColor(this, R.color.greyText)
            option.setTextColor(textColor)
            option.typeface= Typeface.DEFAULT
            option.background= ContextCompat.getDrawable(this, R.drawable.default_option_border_bg)
        }
    }

    fun removeUnwantedViews(question:Question){
        var maxOptionsNum=0
        var ll_main_layout= findViewById<LinearLayout>(R.id.ll_main_layout)

        for(question in mQuestionsList!!){
            if(question.options.size>maxOptionsNum){
                maxOptionsNum= question.options.size
            }
        }

        var unwantedView:TextView
        for(i in question.options.size+1..maxOptionsNum){
            unwantedView= findViewById<TextView>(i)
            ll_main_layout.removeView(unwantedView)
        }
    }

    fun createOptionTextView(text: String, optionSize: Int):TextView{
        val tv_dynamic = TextView(this)

        var generatedId=generatedOptionID++
        generatedId= generatedId%optionSize
        if(generatedId==0){
            generatedId=optionSize
        }

        tv_dynamic.setId(generatedId)
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

        optionsList.add(tv_dynamic)
        return tv_dynamic
    }

    private fun resetOptionIds() {
        generatedOptionID=1
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

    private fun answerView(answer:TextView, drawableView:Int){
        answer.background=ContextCompat.getDrawable(this, drawableView)
        val textColor = ContextCompat.getColor(this, R.color.white)
        answer.setTextColor(textColor)
    }

    private fun selectedOptionView(tv:TextView, selectedOptionNum:Int){
        var btn_submit= findViewById<Button>(R.id.btn_submit)
        btn_submit.text="SUBMIT"
        defaultOptionsView()
        mSelectedOptionPosition= selectedOptionNum

        val textColor = ContextCompat.getColor(this, R.color.darkGrey)
        tv.setTextColor(textColor)
        tv.setTypeface(tv.typeface, Typeface.BOLD)
        tv.background= ContextCompat.getDrawable(this, R.drawable.selected_option_border_bg)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            if(v.id==R.id.btn_submit){
                if(mSelectedOptionPosition==0){
                    if(submittedStatus){
                        mCurrentPosition++
                        resetOptionIds()
                        if(mCurrentPosition <= mQuestionsList!!.size){
                            setQuestion()
                        } else{

                            val intent = Intent(this, ResultActivity::class.java)
                            intent.putExtra(Constants.USER_NAME, mUsername)
                            intent.putExtra(Constants.CORRECT_ANSWERS, mCorrectAnswers)
                            intent.putExtra(Constants.TOTAL_QUESTIONS, mQuestionsList!!.size)
                            intent.putExtra(Constants.TIME_TAKEN, getTimeStringFromDouble(time))
                            intent.putExtra(Constants.UNIVERSITY, mUniversity)
                            val TimeVal = getTimeStringFromDouble(time)
                            resetTimer()

                            var resultData = Result(mName.toString(),mCorrectAnswers.toString(),TimeVal.toString())
                            database = FirebaseDatabase.getInstance().getReference("Result")
                            database.child(mUniversity.toString()).child(courseSelected.toString()).child(mProfessorUN.toString()).child(mUsername.toString()).get()
                                .addOnCompleteListener(OnCompleteListener<DataSnapshot?> { task ->
                                    if (task.isSuccessful) {
                                        if (task.result.exists()) {  //if <University>/<Course>/<ProfUsername>/<StudUsername> exists
                                            database.child(mUniversity.toString()).child(courseSelected.toString()).child(mProfessorUN.toString()).child(mUsername.toString()).child(quizSelection.toString()).setValue(resultData).addOnSuccessListener {
                                                Toast.makeText(this, "Saved Result", Toast.LENGTH_SHORT).show();
                                                startActivity(intent)
                                                finish()
                                            }.addOnFailureListener{
                                                Toast.makeText(this, "Failed to save result", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        else{  //if <University>/<Course>/<ProfUsername>/<StudUsername> does not exists
                                            database.child(mUniversity.toString()).get()
                                                .addOnCompleteListener(OnCompleteListener<DataSnapshot?> { task ->
                                                    if (task.isSuccessful) {
                                                        if (task.result.exists())
                                                        { //if <University> exists
                                                            database.child(mUniversity.toString()).child(courseSelected.toString()).get()
                                                                .addOnCompleteListener(OnCompleteListener<DataSnapshot?> { task ->
                                                                    if (task.isSuccessful) {
                                                                        if (task.result.exists())
                                                                        { //if <University>/<Course> exists
                                                                            database.child(mUniversity.toString()).child(courseSelected.toString()).child(mProfessorUN.toString()).get()
                                                                                .addOnCompleteListener(OnCompleteListener<DataSnapshot?> { task ->
                                                                                    if (task.isSuccessful) {
                                                                                        if (task.result.exists())
                                                                                        { //if <University>/<Course>/<ProfUsername>  exists
                                                                                            database.child(mUniversity.toString()).child(courseSelected.toString()).child(mProfessorUN.toString()).child(mUsername.toString()).get()
                                                                                                .addOnCompleteListener(OnCompleteListener<DataSnapshot?> { task ->
                                                                                                    if (task.isSuccessful) {
                                                                                                        if (task.result.exists())
                                                                                                        { //if <University>/<Course>/<ProfUsername>/<StudUsername>  exists
                                                                                                            database.child(mUniversity.toString()).child(courseSelected.toString()).child(mProfessorUN.toString()).child(mUsername.toString()).child(quizSelection.toString()).setValue(resultData).addOnSuccessListener {
                                                                                                                Toast.makeText(this, "Saved Result", Toast.LENGTH_SHORT).show();
                                                                                                                startActivity(intent)
                                                                                                                finish()
                                                                                                            }.addOnFailureListener{
                                                                                                                Toast.makeText(this, "Failed to save result", Toast.LENGTH_SHORT).show();
                                                                                                            }
                                                                                                        }
                                                                                                        else{//if <University>/<Course>/<ProfUsername>/<StudUsername>  does not exists
                                                                                                            database.child(mUniversity.toString()).child(courseSelected.toString()).child(mProfessorUN.toString()).child(mUsername.toString()).setValue(mUsername).addOnSuccessListener {
                                                                                                                Toast.makeText(this, "Saved Student Username", Toast.LENGTH_SHORT).show();
                                                                                                                database.child(mUniversity.toString()).child(courseSelected.toString()).child(mProfessorUN.toString()).child(mUsername.toString()).child(quizSelection.toString()).setValue(resultData).addOnSuccessListener {
                                                                                                                    Toast.makeText(this, "Saved result", Toast.LENGTH_SHORT).show();
                                                                                                                    startActivity(intent)
                                                                                                                    finish()
                                                                                                                }.addOnFailureListener{
                                                                                                                    Toast.makeText(this, "Failed to save result", Toast.LENGTH_SHORT).show();
                                                                                                                }
                                                                                                            }.addOnFailureListener{
                                                                                                                Toast.makeText(this, "Failed to save student username", Toast.LENGTH_SHORT).show();
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                })
                                                                                        }
                                                                                        else
                                                                                        { //if <University>/<Course>/<ProfUsername> does not exists
                                                                                            database.child(mUniversity.toString()).child(courseSelected.toString()).child(mProfessorUN.toString()).setValue(mProfessorUN).addOnSuccessListener {
                                                                                                Toast.makeText(this, "Saved Prof Username", Toast.LENGTH_SHORT).show();
                                                                                                database.child(mUniversity.toString()).child(courseSelected.toString()).child(mProfessorUN.toString()).child(mUsername.toString()).setValue(mUsername).addOnSuccessListener {
                                                                                                    Toast.makeText(this, "Saved Student Username", Toast.LENGTH_SHORT).show();
                                                                                                    database.child(mUniversity.toString()).child(courseSelected.toString()).child(mProfessorUN.toString()).child(mUsername.toString()).child(quizSelection.toString()).setValue(resultData).addOnSuccessListener {
                                                                                                        Toast.makeText(this, "Saved result", Toast.LENGTH_SHORT).show();
                                                                                                        startActivity(intent)
                                                                                                        finish()
                                                                                                    }.addOnFailureListener{
                                                                                                        Toast.makeText(this, "Failed to save result", Toast.LENGTH_SHORT).show();
                                                                                                    }
                                                                                                }.addOnFailureListener{
                                                                                                    Toast.makeText(this, "Failed to save student username", Toast.LENGTH_SHORT).show();
                                                                                                }
                                                                                            }.addOnFailureListener{
                                                                                                Toast.makeText(this, "Failed to save prof Username", Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                })
                                                                        }
                                                                        else
                                                                        { //if <University>/<Course> does not exists
                                                                            database.child(mUniversity.toString()).child(courseSelected.toString()).setValue(courseSelected).addOnSuccessListener {
                                                                                Toast.makeText(this, "Saved Course", Toast.LENGTH_SHORT).show();
                                                                                database.child(mUniversity.toString()).child(courseSelected.toString()).child(mProfessorUN.toString()).setValue(mProfessorUN).addOnSuccessListener {
                                                                                    Toast.makeText(this, "Saved Prof Username", Toast.LENGTH_SHORT).show();
                                                                                    database.child(mUniversity.toString()).child(courseSelected.toString()).child(mProfessorUN.toString()).child(mUsername.toString()).setValue(mUsername).addOnSuccessListener {
                                                                                        Toast.makeText(this, "Saved Student Username", Toast.LENGTH_SHORT).show();
                                                                                        database.child(mUniversity.toString()).child(courseSelected.toString()).child(mProfessorUN.toString()).child(mUsername.toString()).child(quizSelection.toString()).setValue(resultData).addOnSuccessListener {
                                                                                            Toast.makeText(this, "Saved result", Toast.LENGTH_SHORT).show();
                                                                                            startActivity(intent)
                                                                                            finish()
                                                                                        }.addOnFailureListener{
                                                                                            Toast.makeText(this, "Failed to save result", Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    }.addOnFailureListener{
                                                                                        Toast.makeText(this, "Failed to save student username", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                }.addOnFailureListener{
                                                                                    Toast.makeText(this, "Failed to save prof Username", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }.addOnFailureListener{
                                                                                Toast.makeText(this, "Failed to save Course", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    }
                                                                })
                                                        }
                                                        else
                                                        { //if <University> does not exists
                                                            database.child(mUniversity.toString()).setValue(mUniversity).addOnSuccessListener {
                                                                Toast.makeText(this, "Saved University", Toast.LENGTH_SHORT).show();
                                                                database.child(mUniversity.toString()).child(courseSelected.toString()).setValue(courseSelected).addOnSuccessListener {
                                                                    Toast.makeText(this, "Saved Course", Toast.LENGTH_SHORT).show();
                                                                    database.child(mUniversity.toString()).child(courseSelected.toString()).child(mProfessorUN.toString()).setValue(mProfessorUN).addOnSuccessListener {
                                                                        Toast.makeText(this, "Saved Professor Username", Toast.LENGTH_SHORT).show();
                                                                        database.child(mUniversity.toString()).child(courseSelected.toString()).child(mProfessorUN.toString()).child(mUsername.toString()).setValue(mUsername).addOnSuccessListener {
                                                                            Toast.makeText(this, "Saved Student Username", Toast.LENGTH_SHORT).show();
                                                                            database.child(mUniversity.toString()).child(courseSelected.toString()).child(mProfessorUN.toString()).child(mUsername.toString()).child(quizSelection.toString()).setValue(resultData).addOnSuccessListener {
                                                                                Toast.makeText(this, "Saved result", Toast.LENGTH_SHORT).show();
                                                                                startActivity(intent)
                                                                                finish()
                                                                            }.addOnFailureListener{
                                                                                Toast.makeText(this, "Failed to save result", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }.addOnFailureListener{
                                                                            Toast.makeText(this, "Failed to save student username", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }.addOnFailureListener{
                                                                        Toast.makeText(this, "Failed to save Prof Username", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }.addOnFailureListener{
                                                                    Toast.makeText(this, "Failed to save Course", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }.addOnFailureListener{
                                                                Toast.makeText(this, "Failed to save University", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    }
                                                })
                                        }
                                    }
                                    else{
                                        Toast.makeText(this, "Error encountered", Toast.LENGTH_SHORT).show();
                                    }
                                })
                            startActivity(intent)
                            finish()
                        }
                    } else{
                        Toast.makeText(this, "Please select an option", Toast.LENGTH_SHORT).show()
                    }
                } else{
                    val question= mQuestionsList?.get(mCurrentPosition-1)

                    if(question!!.correctAnswer != mQuestionsList?.get(mCurrentPosition-1)?.options?.get(mSelectedOptionPosition-1)){
                        answerView(optionsList[mSelectedOptionPosition-1], R.drawable.wrong_option_border_bg)
                        correctStatus=false
                        submittedStatus=true
                    } else{
                        answerView(optionsList[mSelectedOptionPosition-1], R.drawable.correct_option_border_bg)
                        mCorrectAnswers++
                        correctStatus=true
                        submittedStatus=true
                    }

                    mSelectedOptionPosition=0
                    if(mCurrentPosition != mQuestionsList!!.size){
                        var btn_submit= findViewById<Button>(R.id.btn_submit)
                        btn_submit.text="GO TO NEXT QUESTION"
                    }

                    //assign submit button text
                    var btn_submit= findViewById<Button>(R.id.btn_submit)
                    if(mCurrentPosition == mQuestionsList!!.size){
                        btn_submit.text="FINISH"
                    }
                }
            } else{
                if(submittedStatus==false){
                    selectedOptionView(v as TextView, (v as TextView).id)
                }
            }
        }
    }

    private fun startStopTimer(){
        if(timerStarted)
            stopTimer()
        else
            startTimer()
    }

    private fun startTimer() {
        serviceIntent.putExtra(TimerService.TIME_EXTRA, time)
        startService(serviceIntent)
        timerStarted=true
    }

    private fun stopTimer() {
        stopService(serviceIntent)
        timerStarted=false
    }

    private fun resetTimer(){
        stopTimer()
        time= 0.0
        var tv_time= findViewById<TextView>(R.id.tv_time)
        tv_time.text= getTimeStringFromDouble(time)
    }

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            time = intent!!.getDoubleExtra(TimerService.TIME_EXTRA, 0.0)
            var tv_time= findViewById<TextView>(R.id.tv_time)
            tv_time.text= getTimeStringFromDouble(time)
        }
    }

    private fun getTimeStringFromDouble(time: Double): String {
        val resultInt= time.roundToInt()
        val hours= resultInt % 86400 / 3600
        val minutes= resultInt % 86400 % 3600 /60
        val seconds= resultInt % 86400 % 3600 % 60

        return makeTimeString(hours, minutes, seconds)
    }

    private fun makeTimeString(hour: Int, min: Int, sec: Int): String= String.format("%02d:%02d:%02d", hour, min, sec)
}