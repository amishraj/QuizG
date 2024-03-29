package com.example.quizg

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.*


class StudentDashboard : AppCompatActivity() {

    private lateinit var autoCompleteTextViewCourse: AutoCompleteTextView
    private lateinit var autoCompleteTextViewProfessor: AutoCompleteTextView
    private lateinit var autoCompleteTextViewQuiz: AutoCompleteTextView
    private lateinit var reference : DatabaseReference
    private lateinit var referencetemp : DatabaseReference
    private lateinit var referencetemp2 : DatabaseReference

    private var mUsername:String?= null
    private var courseSelection:String?=null
    private var mUniversity:String?= null
    private var quizSelection:String?=null
    private var mProfessor:String?=null
    private var mProfessorUN:String?=null
    private var mName:String?=null

    private var quizList:ArrayList<Quiz>?=null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_dashboard)

        //get student username from previous activity
        mUsername= intent.getStringExtra(Constants.USER_NAME)
        mName =intent.getStringExtra(Constants.NAME_OF_USER)
        mUniversity = intent.getStringExtra(Constants.UNIVERSITY)

        //set welcome text
        var tv_student_name= findViewById<TextView>(R.id.tv_student_name)
        tv_student_name.text="Welcome "+mUsername

        //set Course dropdown
        autoCompleteTextViewCourse= findViewById(R.id.autoCompleteTextCourse)
        var optionCourse= Constants.getAllCourses(mUniversity)
        var arrayAdapterCourse= ArrayAdapter(this, R.layout.option_item, optionCourse)
        autoCompleteTextViewCourse.setAdapter(arrayAdapterCourse)

        //course dropdown click listener
        autoCompleteTextViewCourse.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, rowId ->
            courseSelection = parent.getItemAtPosition(position) as String
            autoCompleteTextViewProfessor= findViewById(R.id.autoCompleteTextProfessor)
            var optionProf= Constants.getAllProfessors(mUniversity,courseSelection)
            var arrayAdapterProf= ArrayAdapter(this, R.layout.option_item, optionProf)
            autoCompleteTextViewProfessor.setAdapter(arrayAdapterProf)
        })

        //set Professor dropdown
        autoCompleteTextViewProfessor= findViewById(R.id.autoCompleteTextProfessor)
        var optionProf= Constants.getAllProfessors(mUniversity,courseSelection)
        var arrayAdapterProf= ArrayAdapter(this, R.layout.option_item, optionProf)
        autoCompleteTextViewProfessor.setAdapter(arrayAdapterProf)

        //course dropdown click listener
        autoCompleteTextViewProfessor.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, rowId ->
            mProfessor = parent.getItemAtPosition(position) as String
            autoCompleteTextViewQuiz= findViewById(R.id.autoCompleteTextQuiz)
            var optionquiz= getQuizTitles()
            var arrayAdapterQuiz= ArrayAdapter(this, R.layout.option_item, optionquiz)
            autoCompleteTextViewQuiz.setAdapter(arrayAdapterQuiz)
        })

        autoCompleteTextViewQuiz= findViewById(R.id.autoCompleteTextQuiz)
        var optionquiz= getQuizTitles()
        var arrayAdapterQuiz= ArrayAdapter(this, R.layout.option_item, optionquiz)
        autoCompleteTextViewQuiz.setAdapter(arrayAdapterQuiz)

        ////quizList= Constants.getQuizzes(courseSelection!!)
        ////setQuizDropdown()

        //quiz dropdown click listener
        autoCompleteTextViewQuiz.setOnItemClickListener(OnItemClickListener { parent, view, position, rowId ->
            quizSelection = parent.getItemAtPosition(position) as String
        })

        //goto quiz button
        var btn_goToQuiz= findViewById<Button>(R.id.btn_goToQuiz)
        btn_goToQuiz.setOnClickListener{
            if(courseSelection==null){
                Toast.makeText(this, "Course not selected", Toast.LENGTH_SHORT)
                    .show();
            }
            else if(mProfessor==null){
                Toast.makeText(this, "Professor not selected", Toast.LENGTH_SHORT)
                    .show();
            }
            else if(quizSelection==null){
                Toast.makeText(this, "Quiz not selected", Toast.LENGTH_SHORT)
                    .show();
            }
            else{
                reference = FirebaseDatabase.getInstance().getReference("Result")
                reference.child(mUniversity.toString()).child(courseSelection.toString()).child(mProfessorUN.toString()).child(mUsername.toString()).child(quizSelection.toString()).get()
                .addOnCompleteListener(OnCompleteListener<DataSnapshot?> { task ->
                    if (task.isSuccessful) {
                        Constants.getQuizzes(
                            mUniversity.toString(),
                            courseSelection.toString(),
                            mProfessor.toString(),
                            quizSelection.toString(),
                            mProfessorUN.toString()
                        )
                        if (task.result.exists()) {
                            val intent = Intent(this, ResultActivity::class.java)
                            val dataSnapshot = task.result
                            val correctanswers = dataSnapshot.child("score").value.toString()
                            val timetaken = dataSnapshot.child("time").value.toString()
                            intent.putExtra(Constants.USER_NAME, mUsername)
                            intent.putExtra(Constants.CORRECT_ANSWERS, correctanswers)
                            intent.putExtra(Constants.TIME_TAKEN, timetaken)
                            intent.putExtra(Constants.TOTAL_QUESTIONS, 10)
                            intent.putExtra(Constants.UNIVERSITY, mUniversity)
                            intent.putExtra(Constants.NAME_OF_USER, mName)
                            startActivity(intent)
                            finish()
                        }
                        else{
                            val intent = Intent(this, QuizQuestionsActivity::class.java)
                            intent.putExtra(Constants.USER_NAME, mUsername)
                            intent.putExtra(Constants.CURRENT_QUIZ_TITLE, quizSelection)
                            intent.putExtra(Constants.UNIVERSITY, mUniversity)
                            intent.putExtra(Constants.COURSE, courseSelection)
                            intent.putExtra(Constants.PROF_NAME, mProfessor)
                            intent.putExtra(Constants.PROF_UNAME, mProfessorUN)
                            intent.putExtra(Constants.NAME_OF_USER, mName)
                            intent.putExtra(Constants.QNo, "1")
                            Constants.getQuizzes(
                                mUniversity.toString(),
                                courseSelection.toString(),
                                mProfessor.toString(),
                                quizSelection.toString(),
                                mProfessorUN.toString()
                            )
                            startActivity(intent)
                            finish()
                        }
                    }
                    else{
                        val intent = Intent(this, QuizQuestionsActivity::class.java)
                        intent.putExtra(Constants.USER_NAME, mUsername)
                        intent.putExtra(Constants.CURRENT_QUIZ_TITLE, quizSelection)
                        intent.putExtra(Constants.UNIVERSITY, mUniversity)
                        intent.putExtra(Constants.COURSE, courseSelection)
                        intent.putExtra(Constants.PROF_NAME, mProfessor)
                        intent.putExtra(Constants.PROF_UNAME, mProfessorUN)
                        intent.putExtra(Constants.NAME_OF_USER, mName)
                        intent.putExtra(Constants.QNo, "1")
                        Constants.getQuizzes(
                            mUniversity.toString(),
                            courseSelection.toString(),
                            mProfessor.toString(),
                            quizSelection.toString(),
                            mProfessorUN.toString()
                        )
                        startActivity(intent)
                        finish()
                    }
                })
            }
        }

        //logout button
        var btn_logout= findViewById<Button>(R.id.btn_logout)
        btn_logout.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    /*private fun setQuizDropdown() {
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
    }*/

    private fun getQuizTitles(): MutableList<String> {
        var quizList= mutableListOf<String>()
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference?.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot){
                for(ds in snapshot.child("Professor").children){
                    val prof_username = ds.key.toString()
                    referencetemp = FirebaseDatabase.getInstance().getReference("Users");
                    referencetemp.child("Professor").child(prof_username).get()
                        .addOnCompleteListener(OnCompleteListener<DataSnapshot?> { task ->
                            if (task.isSuccessful) {
                                if (task.result.exists()) {
                                    val dataSnapshot = task.result
                                    val firstname = dataSnapshot.child("firstname").value.toString()
                                    val lastname = dataSnapshot.child("lastname").value.toString()
                                    val name=firstname+" "+lastname
                                    if(name==mProfessor){
                                        mProfessorUN=prof_username
                                        referencetemp2 = FirebaseDatabase.getInstance().getReference("Quiz");
                                        referencetemp2?.addValueEventListener(object: ValueEventListener {
                                            override fun onDataChange(snapshot: DataSnapshot){
                                                for(ds in snapshot.child(mUniversity.toString()).child(courseSelection.toString()).child(mProfessorUN.toString()).children){
                                                    val quiz = ds.key.toString()
                                                    val published = ds.child("Published").exists()
                                                    if(published==true) {
                                                        quizList.add(quiz.toString())
                                                    }
                                                }
                                            }
                                            override fun onCancelled(error: DatabaseError) {
                                                TODO("Not yet implemented")
                                            }
                                        })
                                    }
                                }
                            }
                        })
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        return quizList
    }
}