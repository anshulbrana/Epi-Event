package com.example.epi_event

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.epi_event.authentication_login_signup.ChangePasswordActivity
import com.example.epi_event.authentication_login_signup.LoginActivity
import com.example.epi_event.create_event.CreateEvent
import com.example.epi_event.create_event.CreateEventActivity
import com.example.epi_event.databinding.ActivityProfileBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileActivity : AppCompatActivity() {

    //ViewBinding
    private lateinit var binding: ActivityProfileBinding

    //ActionBar
    private lateinit var actionBar: ActionBar

    //ProgressDialog
    private lateinit var progressDialog: ProgressDialog

    //FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth
    private var email = ""
    private var password = ""

    //Database Reference
    private lateinit var databaseReference: DatabaseReference

    //Recycler View
    private lateinit var eventRecyclerView: RecyclerView

    //Array List
    private lateinit var eventsArrayList: ArrayList<EventObject>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bindActivity()

    }

    private fun bindActivity() {

        //configure ActionBar
        actionBar = supportActionBar!!
        actionBar.title = "Profile"

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        //For gmail verification

        confirmEmailVerification()

        //click logout
        binding.activityProfileBtnLogout.setOnClickListener {
            firebaseAuth.signOut()
            Toast.makeText(this, "Logging out", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        //For changing password

        binding.activityProfileBtnChangePassword.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        //Add Event

        binding.activityProfileBtnAddEvent.setOnClickListener {
            val intent = Intent(this, CreateEventActivity::class.java)
            startActivity(intent)
//            finish()
        }

        //For getting event data From Firebase

        eventRecyclerView = findViewById(R.id.activity_show_all_events_rv_events)
        eventRecyclerView.layoutManager = LinearLayoutManager(this)
        eventRecyclerView.hasFixedSize()

        eventsArrayList = arrayListOf<EventObject>()
        getEventData()


    }

    private fun getEventData() {
        databaseReference =
            FirebaseDatabase.getInstance("https://epita-event-signup-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Events")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {

                        val event = userSnapshot.getValue(EventObject::class.java)
                        eventsArrayList.add(event!!)
                    }
                    // For on click response on recycler view
                    val onItemClickListener = View.OnClickListener {
                        val position: Int = it.tag as Int
                        val clickedEvent = eventsArrayList[position]
                        val eventNameClicked = clickedEvent.eventName

                        Toast.makeText(this@ProfileActivity, "" + eventNameClicked,
                            Toast.LENGTH_SHORT).show()

                        val intent: Intent = Intent(this@ProfileActivity, EventDetail::class.java)
                        intent.putExtra("eventNamePass", eventNameClicked)
                        startActivity(intent)

                    }

                    eventRecyclerView.adapter =
                        EventRecyclerViewAdapter(eventsArrayList,
                            this@ProfileActivity,
                            onItemClickListener)


                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun confirmEmailVerification() {
        val firebaseUserEmail = firebaseAuth.currentUser

        Log.d("userNameFirebase", "" + firebaseUserEmail)

        if (firebaseUserEmail != null) {
            if (firebaseUserEmail.isEmailVerified) {

                Log.d("insideCheck", "done")

                binding.activityProfileTvEmailNotVerified.visibility = View.GONE
                binding.activityProfileTvClickToVerify.visibility = View.GONE

            } else {

                //Resend mail verification on click on link

                binding.activityProfileTvClickToVerify.setOnClickListener {
                    val firebaseUserEmail = firebaseAuth.currentUser

                    Log.d("checkVerification", firebaseUserEmail.toString())

                    firebaseUserEmail!!.sendEmailVerification().addOnSuccessListener {

                        Toast.makeText(this,
                            "Verification email has been send to your mail",
                            Toast.LENGTH_SHORT).show()
                        firebaseAuth.signOut()
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()

                    }.addOnFailureListener { e ->
                        Toast.makeText(this,
                            "Email verification failed due to ${e.message}",
                            Toast.LENGTH_SHORT).show()

                    }

                    //Reload activity after 2 minute

//                    val handler = Handler()
//                    handler.postDelayed(Runnable {
//                        finish()
//                        startActivity(intent);
//                    }, 120000)

                }

            }
        }
    }

    private fun checkUser() {
        //If user is already logged in goto profile activity
        //get current user

        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            //User is already logged in
            val email = firebaseUser.email

            //set username
            binding.activityProfileTvEmail.text = email

        } else {
            //user is null so not signed in
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}