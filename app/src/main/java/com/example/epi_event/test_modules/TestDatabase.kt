package com.example.epi_event.test_modules

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.epi_event.R
import com.example.epi_event.authentication_login_signup.User
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class TestDatabase : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_database)

//        databaseTest()
        databaseMain()


    }

    private fun databaseMain() {
        val email = "testdbemail@gmail.com"
        val password = "password@123"
        val userName = "testUserName"
        val epitaEmail = "testEpitaEmail@gmail.com"

        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance("https://epita-event-signup-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Users")


        if (email != null) {

            val user = User(email, userName, epitaEmail)
            databaseReference.child(userName).setValue(user)
                .addOnSuccessListener {

                    Toast.makeText(this, "saved db", Toast.LENGTH_SHORT).show()

                }
                .addOnFailureListener { e ->
                    Toast.makeText(this,
                        "Saving to database failed because ${e.message}",
                        Toast.LENGTH_SHORT)
                        .show()
                }
        }

    }

    private fun databaseTest() {
        val database =
            Firebase.database("https://epita-event-signup-default-rtdb.europe-west1.firebasedatabase.app/")
        val myRef = database.getReference("message")

        myRef.setValue("Hello, World!")
    }
}