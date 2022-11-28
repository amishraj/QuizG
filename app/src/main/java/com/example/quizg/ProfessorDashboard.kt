package com.example.quizg

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*

class ProfessorDashboard : AppCompatActivity() {

    private lateinit var autoCompleteTextViewCourse: AutoCompleteTextView
    private lateinit var autoCompleteTextViewQuiz: AutoCompleteTextView
    private lateinit var database : DatabaseReference
    private lateinit var reference : DatabaseReference
    private var courseSelected:String?=null
    private lateinit var selCourse: AutoCompleteTextView

    private var mUsername:String?= null
    private var mUniversity:String?= null
    private var quizSelection:String?=null

    private var quizList:ArrayList<Quiz>?=null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_professor_dashboard)

        mUsername = intent.getStringExtra(Constants.USER_NAME)
        mUniversity = intent.getStringExtra(Constants.UNIVERSITY)

        //set welcome text
        var tv_professor_name = findViewById<TextView>(R.id.tv_welcome_prof)
        tv_professor_name.text = "Welcome " + mUsername

        //set Course dropdown
        autoCompleteTextViewCourse = findViewById(R.id.autoCompleteTextCourse)
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
        var arrayAdapterCourse = ArrayAdapter(this, R.layout.option_item, Courses)
        autoCompleteTextViewCourse.setAdapter(arrayAdapterCourse)

        autoCompleteTextViewCourse.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, rowId ->
            courseSelected = parent.getItemAtPosition(position) as String
        })

        val quizname: TextInputEditText= findViewById(R.id.QuizName);

        //logout button
        var btn_logout= findViewById<Button>(R.id.btn_logout)
        btn_logout.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val btn_goToQuizCreation = findViewById<Button>(R.id.create_quiz)
        btn_goToQuizCreation.setOnClickListener {
            quizSelection = quizname.text.toString()
            Toast.makeText(this,"quizSelection"+ quizSelection, Toast.LENGTH_SHORT).show();
            val intent = Intent(this, CreateQuiz::class.java)
            intent.putExtra(Constants.USER_NAME, mUsername)
            intent.putExtra(Constants.CURRENT_QUIZ_TITLE, quizSelection)
            intent.putExtra(Constants.UNIVERSITY, mUniversity)
            intent.putExtra(Constants.COURSE, courseSelected)
            intent.putExtra(Constants.QNo, "1")
            database = FirebaseDatabase.getInstance().getReference("Quiz")
            database.child(mUniversity.toString()).child(courseSelected.toString()).child(mUsername.toString()).get()
                .addOnCompleteListener(OnCompleteListener<DataSnapshot?> { task ->
                    if (task.isSuccessful) {
                        if (task.result.exists()) {  //if <University>/<Course>/<Username> exists
                            database.child(mUniversity.toString()).child(courseSelected.toString()).child(mUsername.toString()).child(quizSelection.toString()).setValue(quizSelection).addOnSuccessListener {
                            Toast.makeText(this, "Saved Quiz", Toast.LENGTH_SHORT).show();
                            startActivity(intent)
                            finish()
                            }.addOnFailureListener{
                                Toast.makeText(this, "Failed to save quiz", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{  //if <University>/<Course>/<Username> does not exists
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
                                                            database.child(mUniversity.toString()).child(courseSelected.toString()).child(mUsername.toString()).get()
                                                                .addOnCompleteListener(OnCompleteListener<DataSnapshot?> { task ->
                                                                    if (task.isSuccessful) {
                                                                        if (task.result.exists())
                                                                        { //if <University>/<Course>/<Username>  exists
                                                                            database.child(mUniversity.toString()).child(courseSelected.toString()).child(mUsername.toString()).child(quizSelection.toString()).setValue(quizSelection).addOnSuccessListener {
                                                                            Toast.makeText(this, "Saved Quiz", Toast.LENGTH_SHORT).show();
                                                                            startActivity(intent)
                                                                            finish()
                                                                            }.addOnFailureListener{
                                                                                Toast.makeText(this, "Failed to save quiz", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                        else
                                                                        { //if <University>/<Course>/<Username> does not exists
                                                                            database.child(mUniversity.toString()).child(courseSelected.toString()).child(mUsername.toString()).setValue(mUsername).addOnSuccessListener {
                                                                                Toast.makeText(this, "Saved Username", Toast.LENGTH_SHORT).show();
                                                                                database.child(mUniversity.toString()).child(courseSelected.toString()).child(mUsername.toString()).setValue(quizSelection).addOnSuccessListener {
                                                                                    Toast.makeText(this, "Saved Quiz", Toast.LENGTH_SHORT).show();
                                                                                    startActivity(intent)
                                                                                    finish()
                                                                                }.addOnFailureListener{
                                                                                    Toast.makeText(this, "Failed to save Quiz", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }.addOnFailureListener{
                                                                                Toast.makeText(this, "Failed to save Username", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    }
                                                                })
                                                        }
                                                        else
                                                        { //if <University>/<Course> does not exists
                                                            database.child(mUniversity.toString()).child(courseSelected.toString()).setValue(courseSelected).addOnSuccessListener {
                                                                Toast.makeText(this, "Saved Course", Toast.LENGTH_SHORT).show();
                                                                database.child(mUniversity.toString()).child(courseSelected.toString()).child(mUsername.toString()).setValue(mUsername).addOnSuccessListener {
                                                                    Toast.makeText(this, "Saved Username", Toast.LENGTH_SHORT).show();
                                                                    database.child(mUniversity.toString()).child(courseSelected.toString()).child(mUsername.toString()).child(quizSelection.toString()).setValue(quizSelection).addOnSuccessListener {
                                                                        Toast.makeText(this, "Saved Quiz", Toast.LENGTH_SHORT).show();
                                                                        startActivity(intent)
                                                                        finish()
                                                                    }.addOnFailureListener{
                                                                        Toast.makeText(this, "Failed to save Quiz", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }.addOnFailureListener{
                                                                    Toast.makeText(this, "Failed to save Username", Toast.LENGTH_SHORT).show();
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
                                                    database.child(mUniversity.toString()).child(courseSelected.toString()).child(mUsername.toString()).setValue(mUsername).addOnSuccessListener {
                                                        Toast.makeText(this, "Saved Username", Toast.LENGTH_SHORT).show();
                                                        database.child(mUniversity.toString()).child(courseSelected.toString()).child(mUsername.toString()).child(quizSelection.toString()).setValue(quizSelection).addOnSuccessListener {
                                                            Toast.makeText(this, "Saved Quiz", Toast.LENGTH_SHORT).show();
                                                            startActivity(intent)
                                                            finish()
                                                        }.addOnFailureListener{
                                                            Toast.makeText(this, "Failed to save Quiz", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }.addOnFailureListener{
                                                        Toast.makeText(this, "Failed to save Username", Toast.LENGTH_SHORT).show();
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
        }
    }
}