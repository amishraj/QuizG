package com.example.quizg

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CreateQuiz  : AppCompatActivity(){

    private lateinit var database : DatabaseReference
    lateinit var radioGroup: RadioGroup
    lateinit var radioButton: RadioButton

    private var mUsername:String?= null
    private var mUniversity:String?= null
    private var mQuizName:String?=null
    private var mCourseName:String?=null
    private var mQNo:String?=null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_quiz_professor)

        mUsername = intent.getStringExtra(Constants.USER_NAME)
        mUniversity = intent.getStringExtra(Constants.UNIVERSITY)
        mQuizName = intent.getStringExtra(Constants.CURRENT_QUIZ_TITLE)
        mCourseName = intent.getStringExtra(Constants.COURSE)
        mQNo = intent.getStringExtra(Constants.QNo)

        radioGroup = findViewById(R.id.groupradio);
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            radioButton = findViewById<RadioButton>(checkedId)
            Toast.makeText(
                this,
                "Selected correct answer is : " + radioButton.text,
                Toast.LENGTH_SHORT
            ).show()
        }

        val NextQuestion:Button= findViewById(R.id.nextQuestionBtn);
        NextQuestion.setOnClickListener{SaveQuestion()};

        val PublishTest:Button= findViewById(R.id.publish);
        PublishTest.setOnClickListener{publishTest()};
    }

    private fun SaveQuestion(){
        val Question: TextInputEditText = findViewById(R.id.question_creation);
        val OptionA: TextInputEditText = findViewById(R.id.option_A);
        val OptionB: TextInputEditText = findViewById(R.id.option_B);
        val OptionC: TextInputEditText = findViewById(R.id.option_C);
        val OptionD: TextInputEditText = findViewById(R.id.option_D);

        if(Question.text.toString().isEmpty()){
            Toast.makeText(this, "Please enter a Question", Toast.LENGTH_SHORT).show();
        }
        else if(OptionA.text.toString().isEmpty()){
            Toast.makeText(this, "Please enter Option A", Toast.LENGTH_SHORT).show();
        }
        else if(OptionB.text.toString().isEmpty()){
            Toast.makeText(this, "Please enter Option B", Toast.LENGTH_SHORT).show();
        }
        else if(OptionC.text.toString().isEmpty()){
            Toast.makeText(this, "Please enter Option C", Toast.LENGTH_SHORT).show();
        }
        else if(OptionD.text.toString().isEmpty()){
            Toast.makeText(this, "Please enter Option D", Toast.LENGTH_SHORT).show();
        }
        else if(radioGroup.getCheckedRadioButtonId() == -1){
            Toast.makeText(this, "Please select the correct answer", Toast.LENGTH_SHORT).show();
        }
        else
        {
            var correctAnswer : Option
            if(radioButton.text=="A"){
                correctAnswer =Option(1,"A")
            }
            else if(radioButton.text=="B"){
                correctAnswer =Option(2,"B")
            }
            else if(radioButton.text=="C"){
                correctAnswer =Option(3,"C")
            }
            else{
                correctAnswer =Option(4,"D")
            }
            var optionsList = arrayListOf<Option>()
            optionsList.add(Option(1, OptionA.text.toString()))
            optionsList.add(Option(2, OptionB.text.toString()))
            optionsList.add(Option(3,OptionC.text.toString()))
            optionsList.add(Option(4,OptionD.text.toString()))

            val question = Question(Integer.parseInt(mQNo),Question.text.toString(),optionsList,correctAnswer)

            database = FirebaseDatabase.getInstance().getReference("Quiz")
            database.child(mUniversity.toString()).child(mCourseName.toString()).child(mUsername.toString()).child(mQuizName.toString()).get()
                .addOnCompleteListener(OnCompleteListener<DataSnapshot?>
                { task ->
                    if (task.isSuccessful) {
                        database.child(mUniversity.toString()).child(mCourseName.toString()).child(mUsername.toString()).child(mQuizName.toString()).child(mQNo.toString()).setValue(question).addOnSuccessListener {
                            Toast.makeText(this, "Saved Question", Toast.LENGTH_SHORT).show();
                            val intent = Intent(this, CreateQuiz::class.java)
                            intent.putExtra(Constants.USER_NAME, mUsername)
                            intent.putExtra(Constants.CURRENT_QUIZ_TITLE, mQuizName)
                            intent.putExtra(Constants.UNIVERSITY, mUniversity)
                            intent.putExtra(Constants.COURSE, mCourseName)
                            var qNo = Integer.parseInt(mQNo)+1
                            intent.putExtra(Constants.QNo, qNo.toString())
                            startActivity(intent)
                            finish()
                        }.addOnFailureListener{
                            Toast.makeText(this, "Failed to save question", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
        }
    }

    private fun publishTest(){
        val Question: TextInputEditText = findViewById(R.id.question_creation);
        val OptionA: TextInputEditText = findViewById(R.id.option_A);
        val OptionB: TextInputEditText = findViewById(R.id.option_B);
        val OptionC: TextInputEditText = findViewById(R.id.option_C);
        val OptionD: TextInputEditText = findViewById(R.id.option_D);

        if(Question.text.toString().isEmpty()){
            Toast.makeText(this, "Please enter a Question", Toast.LENGTH_SHORT).show();
        }
        else if(OptionA.text.toString().isEmpty()){
            Toast.makeText(this, "Please enter Option A", Toast.LENGTH_SHORT).show();
        }
        else if(OptionB.text.toString().isEmpty()){
            Toast.makeText(this, "Please enter Option B", Toast.LENGTH_SHORT).show();
        }
        else if(OptionC.text.toString().isEmpty()){
            Toast.makeText(this, "Please enter Option C", Toast.LENGTH_SHORT).show();
        }
        else if(OptionD.text.toString().isEmpty()){
            Toast.makeText(this, "Please enter Option D", Toast.LENGTH_SHORT).show();
        }
        else if(radioGroup.getCheckedRadioButtonId() == -1){
            Toast.makeText(this, "Please select the correct answer", Toast.LENGTH_SHORT).show();
        }
        else
        {
            var correctAnswer : Option
            if(radioButton.text=="A"){
                correctAnswer =Option(1,"A")
            }
            else if(radioButton.text=="B"){
                correctAnswer =Option(2,"B")
            }
            else if(radioButton.text=="C"){
                correctAnswer =Option(3,"C")
            }
            else{
                correctAnswer =Option(4,"D")
            }
            var optionsList = arrayListOf<Option>()
            optionsList.add(Option(1, OptionA.text.toString()))
            optionsList.add(Option(2, OptionB.text.toString()))
            optionsList.add(Option(3,OptionC.text.toString()))
            optionsList.add(Option(4,OptionD.text.toString()))

            val question = Question(Integer.parseInt(mQNo),Question.text.toString(),optionsList,correctAnswer)

            database = FirebaseDatabase.getInstance().getReference("Quiz")
            database.child(mUniversity.toString()).child(mCourseName.toString()).child(mUsername.toString()).child(mQuizName.toString()).get()
                .addOnCompleteListener(OnCompleteListener<DataSnapshot?>
                { task ->
                    if (task.isSuccessful) {
                        database.child(mUniversity.toString()).child(mCourseName.toString()).child(mUsername.toString()).child(mQuizName.toString()).child(mQNo.toString()).setValue(question).addOnSuccessListener {
                            Toast.makeText(this, "Saved Question", Toast.LENGTH_SHORT).show();
                        }.addOnFailureListener{
                            Toast.makeText(this, "Failed to save question", Toast.LENGTH_SHORT).show();
                        }
                    }
                })

            database = FirebaseDatabase.getInstance().getReference("Quiz")
            database.child(mUniversity.toString()).child(mCourseName.toString()).child(mUsername.toString()).child(mQuizName.toString()).get()
                .addOnCompleteListener(OnCompleteListener<DataSnapshot?>
                { task ->
                    if (task.isSuccessful) {
                        database.child(mUniversity.toString()).child(mCourseName.toString()).child(mUsername.toString()).child(mQuizName.toString()).child("Published").setValue("Yes").addOnSuccessListener {
                            Toast.makeText(this, "Quiz Published", Toast.LENGTH_SHORT).show();
                            val intent = Intent(this, ProfessorDashboard::class.java)
                            intent.putExtra(Constants.USER_NAME, mUsername)
                            intent.putExtra(Constants.CURRENT_QUIZ_TITLE, mQuizName)
                            intent.putExtra(Constants.UNIVERSITY, mUniversity)
                            intent.putExtra(Constants.COURSE, mCourseName)
                            startActivity(intent)
                            finish()
                        }.addOnFailureListener{
                            Toast.makeText(this, "Failed to save question", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
        }
    }
}