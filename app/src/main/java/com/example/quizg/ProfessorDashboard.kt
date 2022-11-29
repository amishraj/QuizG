package com.example.quizg

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ProfessorDashboard : AppCompatActivity() {

    private lateinit var autoCompleteTextViewCourse: AutoCompleteTextView
    private lateinit var autoCompleteTextViewQuiz: AutoCompleteTextView
    private lateinit var database : DatabaseReference
    private lateinit var reference : DatabaseReference
    private lateinit var referencetemp : DatabaseReference
    private var courseSelected:String?=null
    private lateinit var selCourse: AutoCompleteTextView

    private var mUsername:String?= null
    private var mUniversity:String?= null
    private var quizSelection:String?=null
    private var quizSelected:String?=null
    private var quizNull:Boolean = true
    private val STORAGE_PERMISSION_CODE:Int =1000;

    private var quizList:ArrayList<Quiz>?=null
    private var Results = mutableListOf<Result>()

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
        var Quizzes= mutableListOf<String>()
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
            reference = FirebaseDatabase.getInstance().getReference("Quiz");
            reference?.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot){
                    Quizzes?.clear()
                    quizNull = true
                    for(ds in snapshot.child(mUniversity.toString()).child(courseSelected.toString()).child(mUsername.toString()).children){
                        val quiz = ds.key.toString()
                        val published = ds.child("Published").exists()
                        if(published==true) {
                            quizNull = false
                            Quizzes.add(quiz.toString())
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        })

        //set Quiz dropdown
        autoCompleteTextViewQuiz = findViewById(R.id.autoCompleteTextPastQuiz)
        var arrayAdapterQuiz = ArrayAdapter(this, R.layout.option_item, Quizzes)
        autoCompleteTextViewQuiz.setAdapter(arrayAdapterQuiz)

        autoCompleteTextViewQuiz.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, rowId ->
            quizSelected = parent.getItemAtPosition(position) as String
        })

        val quizname: TextInputEditText= findViewById(R.id.QuizName);

        //Download Result button
        var btn_downloadResult= findViewById<Button>(R.id.get_result)
        btn_downloadResult.setOnClickListener{
            if(quizNull){
                Toast.makeText(this,"No quiz selected", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this,"Downloading Result..", Toast.LENGTH_SHORT).show();


                reference = FirebaseDatabase.getInstance().getReference("Result");
                Results.clear()
                reference?.addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (ds in snapshot.child(mUniversity.toString()).child(courseSelected.toString()).child(mUsername.toString()).children) {
                            val student_username = ds.key.toString()
                            val quizCompleted = ds.child(quizSelected.toString()).exists()
                            if (quizCompleted == true) {
                                var result = Result(ds.child(quizSelected.toString()).child("name").value as String?,ds.child(quizSelected.toString()).child("score").value as String?, ds.child(quizSelected.toString()).child("time").value as String?)
                                Results.add(result)
                            }
                        }

                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
                                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
                            }
                            else{
                                startDownloading();
                            }
                        }
                        else{
                            startDownloading();
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
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
            reference = FirebaseDatabase.getInstance().getReference("Quiz")
            reference.child(mUniversity.toString()).child(courseSelected.toString()).child(mUsername.toString()).child(quizSelection.toString()).get()
                .addOnCompleteListener(OnCompleteListener<DataSnapshot?> { task ->
                    if (task.isSuccessful) {
                        if (task.result.exists()) {
                            Toast.makeText(this, "Quiz name already exists!", Toast.LENGTH_SHORT)
                                .show();
                        }
                        else{
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
                })

        }
    }

    private fun startDownloading() {
        val COLUMNs = arrayOf<String>("Id","Name","Score","Time Taken")
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet(quizSelected.toString())
        val headerFont = workbook.createFont()
        headerFont.setBold(true)

        val headerCellStyle = workbook.createCellStyle()
        headerCellStyle.setFont(headerFont)

        val headerRow = sheet.createRow(0)

        for(col in COLUMNs.indices)
        {
            val cell = headerRow.createCell(col)
            cell.setCellValue(COLUMNs[col])
            cell.setCellStyle(headerCellStyle)
        }

        var rowIdx = 1
        for(result in Results)
        {
            val row = sheet.createRow(rowIdx++)
            var id = (rowIdx - 1)
            row.createCell(0).setCellValue(id.toString())
            row.createCell(1).setCellValue(result.name)
            row.createCell(2).setCellValue(result.score)
            row.createCell(3).setCellValue(result.time)

        }
        var fos: FileOutputStream? = null
        fos.use {
            try {
                val str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                val file: File
                file = File(str_path, "Result" + ".xlsx");
                if (!file.getParentFile().exists())
                    file.getParentFile().mkdirs();
                if (!file.exists())
                    file.createNewFile();
                fos = FileOutputStream(file)
                workbook.write(fos)
                Toast.makeText(this, "Excel Sheet Generated:  ${file.path}", Toast.LENGTH_LONG).show()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode)
            {
                STORAGE_PERMISSION_CODE ->
                {
                    if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                        startDownloading()
                    }
                    else{
                        Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
