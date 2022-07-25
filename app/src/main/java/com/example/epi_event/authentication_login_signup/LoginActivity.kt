package com.example.epi_event.authentication_login_signup

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.Patterns
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.epi_event.ProfileActivity
import com.example.epi_event.databinding.ActivityLoginBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {
    //ViewBinding
    private lateinit var binding: ActivityLoginBinding

    //ActionBar
    private lateinit var actionBar: ActionBar

    //ProgressDialog
    private lateinit var progressDialog: ProgressDialog

    //FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth
    private var email = ""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindActivity()

    }

    private fun bindActivity() {

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //configure ActionBar
//        actionBar = supportActionBar!!
//        actionBar.title = "Login"

        //Configure progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Signing In")
        progressDialog.setMessage("Please wait..")
        progressDialog.setCanceledOnTouchOutside(false)

        //init FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        //Handle click open signup activity
        binding.activityLoginTvSignup.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()

        }

        //handle login begin login
        binding.activityLoginBtnLogin.setOnClickListener {
            //before logging in, validate data
            validateData()

        }

        //handle forget password

        binding.activityLoginTvForgetPassword.setOnClickListener {
            showRecoverableDialogue()
        }
    }

    private fun showRecoverableDialogue() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Email address to recover")
        val linearLayout = LinearLayout(this)
        val emailLet = EditText(this)

        // write the email using which you registered
        emailLet.hint = "Email"
        emailLet.minEms = 16
        emailLet.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        linearLayout.addView(emailLet)
        linearLayout.setPadding(10, 10, 10, 10)
        builder.setView(linearLayout)

        // Click on Recover and a email will be sent to your registered email id
        builder.setPositiveButton("Recover", DialogInterface.OnClickListener { dialog, which ->
            val email = emailLet.text.toString().trim { it <= ' ' }
            beginRecovery(email)
        })

        builder.setNegativeButton("Cancel",
            DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() })
        builder.create().show()
    }

    private fun beginRecovery(email: String) {
        progressDialog.show()
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener(OnCompleteListener<Void?> { task ->
                progressDialog.dismiss()
                if (task.isSuccessful) {

                    Toast.makeText(this@LoginActivity,
                        "Password reset mail has been sent to your email",
                        Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@LoginActivity,
                        "Please check your email and try again",
                        Toast.LENGTH_SHORT).show()
                }
            }).addOnFailureListener(OnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this@LoginActivity,
                    "Failed because ${e.message}",
                    Toast.LENGTH_SHORT)
                    .show()
            })

    }


    private fun validateData() {
        //get Data
        email = binding.activityLoginEtEmail.text.toString().trim()
        password = binding.activityLoginEtPassword.text.toString().trim()

        //validate data
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            //Invalid email format
            binding.activityLoginEtEmail.error = "Invalid Email format"
        } else if (TextUtils.isEmpty(password)) {
            //No password entered
            binding.activityLoginEtPassword.error = "Please enter password"
        } else {
            //data is validated begin login
            firebaseLogin()
        }

    }

    private fun firebaseLogin() {
        progressDialog.show()
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                //Login Success
                progressDialog.dismiss()

                //get user info
                val firebaseUser = firebaseAuth.currentUser
                val email = firebaseUser!!.email
                Toast.makeText(this, "Welcome $email", Toast.LENGTH_SHORT).show()

                //open profile
                val intent = Intent(this, ProfileActivity::class.java)
                intent.putExtra("userEmailPass",email.toString())
                startActivity(intent)
                finish()

            }
            .addOnFailureListener { e ->
                //Login Failed
                progressDialog.dismiss()
                Toast.makeText(this,
                    "Login failed \nInvalid username or password \n please try again",
                    Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkUser() {
        //If user is already logged in goto profile activity
        //get current user

        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            //USer is already logged in
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}