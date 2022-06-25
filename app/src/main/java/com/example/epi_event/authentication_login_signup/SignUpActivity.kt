package com.example.epi_event.authentication_login_signup

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.epi_event.ProfileActivity
import com.example.epi_event.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class SignUpActivity : AppCompatActivity() {
    //ViewBinding
    private lateinit var binding: ActivitySignUpBinding

    //ActionBar
    private lateinit var actionBar: ActionBar

    //ProgressDialog
    private lateinit var progressDialog: ProgressDialog

    //FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth
    private var email = ""
    private var password = ""
    private var userName = ""
    private var epitaEmail = ""

    //Database Reference
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bindActivity()


    }

    private fun bindActivity() {
        //configure ActionBar
        actionBar = supportActionBar!!
        actionBar.title = "Sign Up"


        //Enable back button
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)

        //Configure progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Creating account")
        progressDialog.setMessage("Please wait..")
        progressDialog.setCanceledOnTouchOutside(false)

        //init FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        //handle click begin signup
        binding.activitySignupBtnSignUp.setOnClickListener {
            //validate data
            validateData()
        }
    }

    private fun validateData() {
        //get Data
        email = binding.activitySignupEtEmail.text.toString().trim()
        password = binding.activitySignupEtPassword.text.toString().trim()
        userName = binding.activitySignupEtUserName.text.toString().trim()
        epitaEmail = binding.activitySignupEtEpitaEmail.text.toString().trim()

        //validate data
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            //Invalid email format
            binding.activitySignupEtEmail.error = "Invalid Email format"
        } else if (TextUtils.isEmpty(password)) {
            //No password entered
            binding.activitySignupEtPassword.error = "Please enter password"
        } else if (password.length < 6) {
            //password condition
            binding.activitySignupEtPassword.error =
                "Please enter password of more than 6 characters"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(epitaEmail).matches()) {
            //Invalid email format
            binding.activitySignupEtEpitaEmail.error = "Invalid Email format"
        } else if (userName.length < 4) {
            //password condition
            binding.activitySignupEtEpitaEmail.error =
                "Please enter username more than 4 characters"
        } else {
            val database = FirebaseDatabase.getInstance()
            firebaseSignup()

        }
    }

    private fun firebaseSignup() {
        //show progress bar
        progressDialog.show()

        //create account
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {

                //Send authentication mail

                val firebaseUserEmail = firebaseAuth.currentUser

                Log.d("checkVerification", firebaseUserEmail.toString())

                firebaseUserEmail!!.sendEmailVerification().addOnSuccessListener {

                    Toast.makeText(this,
                        "Verification email has been send to your mail",
                        Toast.LENGTH_SHORT).show()

                }.addOnFailureListener { e ->
                    Toast.makeText(this,
                        "Email verification failed due to ${e.message}",
                        Toast.LENGTH_SHORT).show()

                }

                //get current user
                val firebaseUser = firebaseAuth.currentUser
                val email = firebaseUser!!.email

                //signup success
                progressDialog.dismiss()


                //store username and epita email

                Log.d("testDatabaseValue", "$userName $epitaEmail")



                if (email != null) {

                    val uid = firebaseAuth.currentUser!!.uid

                    val user = User(email, userName, epitaEmail)

                    databaseReference =
                        FirebaseDatabase.getInstance("https://epita-event-signup-default-rtdb.europe-west1.firebasedatabase.app/")
                            .getReference("Users")

                    databaseReference.child(uid).setValue(user)
                        .addOnSuccessListener {

                            Toast.makeText(this, "saved db", Toast.LENGTH_SHORT).show()

                            //open profile
                            // Toast.makeText(this, "Welcome $email", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, ProfileActivity::class.java)
                            startActivity(intent)
                            finish()

                        }
                        .addOnFailureListener { e ->
                            progressDialog.dismiss()
                            Toast.makeText(this,
                                "Saving to database failed because ${e.message}",
                                Toast.LENGTH_SHORT)
                                .show()
                        }
                }


            }
            .addOnFailureListener { e ->
                //signup failed
                progressDialog.dismiss()
                Toast.makeText(this, "Signed up failed due to ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }


    override fun onSupportNavigateUp(): Boolean {
        //Go back to previous activity
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}