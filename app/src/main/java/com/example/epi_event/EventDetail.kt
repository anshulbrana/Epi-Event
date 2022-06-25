package com.example.epi_event

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.epi_event.create_event.CreateEventActivity
import com.example.epi_event.databinding.ActivityEventDetailBinding
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.io.File


class EventDetail : AppCompatActivity() {

    var EventNameClicked: String = "Event Name"

    //Database Reference
    private lateinit var databaseReference: DatabaseReference

    //ViewBinding
    private lateinit var binding: ActivityEventDetailBinding

    //ActionBar
    private lateinit var actionBar: ActionBar

    //Event details To send for edit
    private lateinit var eventDetailSendName: String
    private lateinit var eventDetailSendOrganiser: String
    private lateinit var eventDetailSendDate: String
    private lateinit var eventDetailSendTime: String
    private lateinit var eventDetailSendDescription: String
    private lateinit var eventDetailSendLocation: String
    private lateinit var eventDetailSendType: String


    //Set Global
    private lateinit var tvEventName: TextView
    private lateinit var tvEventOrganiser: TextView
    private lateinit var tvEventDate: TextView
    private lateinit var tvEventTime: TextView
    private lateinit var tvEventDescription: TextView
    private lateinit var tvEventLocation: TextView
    private lateinit var tvEventType: TextView
    private lateinit var btnEditEvent: Button
    private lateinit var eventDetailEventNameSend: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Get Event Name
        EventNameClicked = intent.getStringExtra("eventNamePass").toString()
        Log.d("receivedEventName", "" + EventNameClicked)

        bindActivity()

    }

    private fun bindActivity() {

        tvEventName = findViewById(R.id.activity_event_detail_tv_name)
        tvEventOrganiser = findViewById(R.id.activity_event_detail_tv_organiser)
        tvEventDate = findViewById(R.id.activity_event_detail_tv_date)
        tvEventTime = findViewById(R.id.activity_event_detail_tv_time)
        tvEventDescription = findViewById(R.id.activity_event_detail_tv_description)
        tvEventLocation = findViewById(R.id.activity_event_detail_tv_location)
        tvEventType = findViewById(R.id.activity_event_detail_tv_type)
        btnEditEvent = findViewById(R.id.activity_event_detail_btn_edit_event)


//        getEventData()

        getEventDetails()
        getImage()


        btnEditEvent.setOnClickListener {
            val intent = Intent(this, CreateEventActivity::class.java)
            intent.putExtra("EventDetailEventConditionSend", 1)
            intent.putExtra("EventDetailEventNameSend", EventNameClicked)
            intent.putExtra("EventDetailEventOrganiserSend", eventDetailSendOrganiser)
            intent.putExtra("EventDetailEventDateSend", eventDetailSendDate)
            intent.putExtra("EventDetailEventTimeSend", eventDetailSendTime)
            intent.putExtra("EventDetailEventLocationSend", eventDetailSendLocation)
            intent.putExtra("EventDetailEventDescriptionSend", eventDetailSendDescription)
            intent.putExtra("EventDetailEventTypeSend", eventDetailSendType)


            Log.d("eventDetailEventName", EventNameClicked)

            startActivity(intent)
        }
    }

    private fun getImage() {
        val ivEventImage: ImageView = findViewById(R.id.activity_event_detail_iv_image)

        val refStorage =
            FirebaseStorage.getInstance("gs://epita-event-signup.appspot.com")
                .reference.child("event-image/$EventNameClicked")

        val localFile = File.createTempFile(EventNameClicked, "jpg")

        refStorage.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
//            ivEventImage.setImageBitmap(rotateImage(bitmap, 90F))
            ivEventImage.setImageBitmap(bitmap)


        }
            .addOnFailureListener { e ->

                Toast.makeText(this, "Image loading failed ${e.message}", Toast.LENGTH_SHORT).show()

            }

    }

    //To rotate image if necessary

    fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    private fun getEventDetails() {
        databaseReference =
            FirebaseDatabase.getInstance("https://epita-event-signup-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Events/")
        var eventRef = databaseReference.child(EventNameClicked)
        eventRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val eventName = snapshot.child("eventName").getValue<String>(String::class.java)
                val eventOrganiser =
                    snapshot.child("eventOrganiser").getValue<String>(String::class.java)
                val eventDate = snapshot.child("eventDate").getValue<String>(String::class.java)
                val eventTime = snapshot.child("eventTime").getValue<String>(String::class.java)
                val eventDescription =
                    snapshot.child("eventDescription").getValue<String>(String::class.java)
                val eventLocation =
                    snapshot.child("eventLocation").getValue<String>(String::class.java)
                val eventType = snapshot.child("eventType").getValue<String>(String::class.java)

                tvEventName.text = eventName
                tvEventOrganiser.text = eventOrganiser
                tvEventDate.text = eventDate
                tvEventTime.text = eventTime
                tvEventDescription.text = eventDescription
                tvEventLocation.text = eventLocation
                tvEventType.text = eventType


//                eventDetailEventNameSend = eventName.toString()

                //store event details for edit

                eventDetailSendOrganiser = eventOrganiser.toString()
                eventDetailSendDate = eventDate.toString()
                eventDetailSendTime = eventTime.toString()
                eventDetailSendDescription = eventDescription.toString()
                eventDetailSendType = eventType.toString()
                eventDetailSendLocation = eventLocation.toString()

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


    private fun getEventData() {
        databaseReference =
            FirebaseDatabase.getInstance("https://epita-event-signup-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Events").child(EventNameClicked)



        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {

                        Log.d("clickedChildrenName1", EventNameClicked)

                        if (userSnapshot.hasChild(EventNameClicked)) {

                            userSnapshot.child(EventNameClicked)

                            tvEventName.text =
                                userSnapshot.child(EventNameClicked).childrenCount.toString()
                        }


//                        val event = userSnapshot.getValue(EventObject::class.java)
//                        eventsArrayList.add(event!!)
                    }
//
//                    eventRecyclerView.adapter =
//                        EventRecyclerViewAdapter(eventsArrayList,
//                            this@ProfileActivity,
//                            onItemClickListener)


                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

}