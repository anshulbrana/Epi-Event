package com.example.epi_event.user_profile


import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.epi_event.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class UserProfile : AppCompatActivity() {
    //Database Reference
    private lateinit var databaseReference: DatabaseReference
    private lateinit var uid: String
    private lateinit var email: String
    private lateinit var epitaEmail: String
    private lateinit var userName: String

    //For getting id
    private lateinit var tvUserName: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var tvUserEpitaEmail: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        val user = FirebaseAuth.getInstance().currentUser
        uid = user!!.uid

        bindActivity()
        getData()


    }

    private fun bindActivity() {
        tvUserName = findViewById(R.id.activity_user_profile_user_name)
        tvUserEmail = findViewById(R.id.activity_user_profile_user_email)
        tvUserEpitaEmail = findViewById(R.id.activity_user_profile_user_epita_email)
    }

    private fun getData() {
        databaseReference =
            FirebaseDatabase.getInstance("https://epita-event-signup-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Users")
        var eventUserId = databaseReference.child(uid)

        eventUserId.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                email = snapshot.child("email").getValue<String>(String::class.java).toString()
                epitaEmail =
                    snapshot.child("epitaEmail").getValue<String>(String::class.java).toString()
                userName =
                    snapshot.child("userName").getValue<String>(String::class.java).toString()

                tvUserName.text = userName
                tvUserEmail.text = email
                tvUserEpitaEmail.text = epitaEmail

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }
}