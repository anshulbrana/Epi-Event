package com.example.epi_event.test_modules

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.epi_event.EventObject
import com.example.epi_event.EventRecyclerViewAdapter
import com.example.epi_event.R
import com.google.firebase.database.*

class TestShowAllEvents : AppCompatActivity() {

    //Database Reference
    private lateinit var databaseReference: DatabaseReference

    //Recycler View
    private lateinit var eventRecyclerView: RecyclerView

    //Array List
    private lateinit var eventsArrayList: ArrayList<EventObject>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_all_events)

        eventRecyclerView = findViewById(R.id.activity_show_all_events_rv_events)
        eventRecyclerView.layoutManager = LinearLayoutManager(this)
        eventRecyclerView.hasFixedSize()

        eventsArrayList = arrayListOf<EventObject>()
        getUserData()

    }

    private fun getUserData() {
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
//                    eventRecyclerView.adapter =
//                        EventRecyclerViewAdapter(eventsArrayList, this@TestShowAllEvents)
                }
            }

            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(this,
//                    "Database error due to ${error.message}",
//                    Toast.LENGTH_SHORT).show()
            }

        })


    }
}