package com.example.quizg

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class MainActivity : AppCompatActivity() {
    private lateinit var reference : DatabaseReference
    lateinit var radioGroup: RadioGroup
    lateinit var radioButton: RadioButton

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.decorView.systemUiVisibility= View.SYSTEM_UI_FLAG_FULLSCREEN;

        val signup : TextView = findViewById(R.id.signupbutton)
        signup.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
        radioGroup = findViewById(R.id.groupradio);
        radioGroup.setOnCheckedChangeListener { group, checkedId ->

            // on below line we are getting radio button from our group.
            radioButton = findViewById<RadioButton>(checkedId)

            // on below line we are displaying a toast message.
            Toast.makeText(
                this@MainActivity,
                "Selected user type is : " + radioButton.text,
                Toast.LENGTH_SHORT
            ).show()
        }
        val btn_start:Button= findViewById(R.id.btn_start);
        btn_start.setOnClickListener{StartClick()};
    }

    private fun StartClick(){
        val username: TextInputEditText= findViewById(R.id.username);
        val password: TextInputEditText= findViewById(R.id.password);
        if(username.text.toString().isEmpty()){
            Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show();
        }
        else if(radioGroup.getCheckedRadioButtonId() == -1){
            Toast.makeText(this, "Please select user type", Toast.LENGTH_SHORT).show();
        }
        else{
            val username = username.text.toString()
            val password = password.text.toString()
            val userType : String
            val intent : Intent
            if(radioButton.text=="Student"){
                userType="Student"
                intent  = Intent(this, StudentDashboard::class.java)
            }
            else{
                userType="Professor"
                //TODO : Below intent should go to Professor/Admin Dashboard
                intent  = Intent(this, ProfessorDashboard::class.java)
            }
            intent.putExtra(Constants.USER_NAME, username)

            reference = FirebaseDatabase.getInstance().getReference("Users")
            reference.child(userType).child(username).get()
                .addOnCompleteListener(OnCompleteListener<DataSnapshot?> { task ->
                    if (task.isSuccessful) {
                        if (task.result.exists()) {
                            val dataSnapshot = task.result
                            val passwordval = dataSnapshot.child("password").value.toString()
                            var Aes1 = AESEncDec()
                            var DecryptedPassword = Aes1.decrypt(algorithm, passwordval, key, iv)
                            if(password==DecryptedPassword){
                                val university = dataSnapshot.child("university").value.toString()
                                intent.putExtra(Constants.UNIVERSITY, university)
                                Toast.makeText(this, "User Logging in", Toast.LENGTH_SHORT)
                                    .show()
                                startActivity(intent)
                                finish()
                            }
                            else{
                                Toast.makeText(this, "Incorrect Password", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        } else {
                            Toast.makeText(this, "User Doesn't Exist", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        Toast.makeText(this, "Failed to read", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}
