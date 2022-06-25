package com.example.epi_event.authentication_login_signup

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.example.epi_event.R
import com.example.epi_event.databinding.ActivityChangePasswordBinding
import com.example.epi_event.databinding.ActivityLoginBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class ChangePasswordActivity : AppCompatActivity() {

    //ViewBinding
    private lateinit var binding: ActivityChangePasswordBinding

    //FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth
    private var email = ""
    private var oldPassword = ""
    private var newPassword = ""
    private var reNewPassword = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bindActivity()
    }

    private fun bindActivity() {
        firebaseAuth = FirebaseAuth.getInstance()
        var user = firebaseAuth.currentUser!!



        email = user.email!!

        binding.activityChangePasswordTvEmail.text = email


        //data is validated begin login
        binding.activityChangePasswordBtnChangePassword.setOnClickListener {
            authoriseData()

        }


    }

    private fun authoriseData() {
        oldPassword = binding.activityChangePasswordEtOldPassword.text.toString().trim()
        newPassword = binding.activityChangePasswordEtNewPassword.text.toString().trim()
        reNewPassword = binding.activityChangePasswordEtNewRePassword.text.toString().trim()

        if (TextUtils.isEmpty(oldPassword)) {
            //No password entered
            binding.activityChangePasswordEtOldPassword.error = "Please enter old password"
        } else if (oldPassword.length < 6) {
            //password condition
            binding.activityChangePasswordEtOldPassword.error =
                "Please enter old password"
        } else if (TextUtils.isEmpty(newPassword)) {
            //No new password entered
            binding.activityChangePasswordEtNewPassword.error = "Please enter new password"
        } else if (newPassword.length < 6) {
            //password condition
            binding.activityChangePasswordEtNewPassword.error =
                "Please enter new password of more than 6 characters"
        } else if (TextUtils.isEmpty(reNewPassword)) {
            //No new password re entered
            binding.activityChangePasswordEtNewRePassword.error = "Please re-enter new password"
        } else if (reNewPassword.length < 6) {
            //re password condition
            binding.activityChangePasswordEtNewRePassword.error =
                "Please enter new password of more than 6 characters"
        } else if (newPassword != reNewPassword) {
            binding.activityChangePasswordEtNewRePassword.error =
                "Password do not match"
        } else {
            changePassword()
        }
    }

    private fun changePassword() {


        val user = FirebaseAuth.getInstance().currentUser!!
        email = user.email!!


        val credential = EmailAuthProvider
            .getCredential(email, oldPassword)

        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
            .addOnCompleteListener {

            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error ${e.message}", Toast.LENGTH_SHORT).show()

            }

        user!!.updatePassword(newPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Password has been updated!!", Toast.LENGTH_SHORT).show()
                    firebaseAuth.signOut()
                    finish()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
            .addOnFailureListener { e ->

                Toast.makeText(this, "Error ${e.message}", Toast.LENGTH_SHORT).show()

            }
    }
}