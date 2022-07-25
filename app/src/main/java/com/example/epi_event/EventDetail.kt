package com.example.epi_event

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import java.io.File


class EventDetail : AppCompatActivity() {

    var EventNameClicked: String = "Event Name"

    //Database Reference
    private lateinit var databaseReference: DatabaseReference

    //ViewBinding
    private lateinit var binding: ActivityEventDetailBinding

    //ActionBar
    private lateinit var actionBar: ActionBar

    private lateinit var isAdmin: String
    private lateinit var userEmail: String

    //Event details To send for edit
    private lateinit var eventDetailSendName: String
    private lateinit var eventDetailSendOrganiser: String
    private lateinit var eventDetailSendDate: String
    private lateinit var eventDetailSendTime: String
    private lateinit var eventDetailSendDescription: String
    private lateinit var eventDetailSendLocation: String
    private lateinit var eventDetailSendType: String
    private lateinit var eventDetailSendPreRegistration: String


    //Set Global
    private lateinit var tvEventName: TextView
    private lateinit var tvEventOrganiser: TextView
    private lateinit var tvEventDate: TextView
    private lateinit var tvEventTime: TextView
    private lateinit var tvEventDescription: TextView
    private lateinit var tvEventLocation: TextView
    private lateinit var tvEventType: TextView
    private lateinit var btnEditEvent: Button
    private lateinit var tvEventPreRegistration: TextView
    private lateinit var eventDetailEventNameSend: String
    private lateinit var btnGenerateQr: Button
    private lateinit var ivQr: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Get Event Name
        EventNameClicked = intent.getStringExtra("eventNamePass").toString()

        //Check if user is admin or not
        isAdmin = intent.getStringExtra("canEdit").toString()
        Log.d("canEdit", isAdmin)
        Log.d("receivedEventName", "" + EventNameClicked)

        userEmail = intent.getStringExtra("userNamePass").toString()
        Log.d("receivedUserName", "" + userEmail)


        bindActivity()
        if (isAdmin == "true") {
            btnEditEvent.visibility = View.VISIBLE
        }
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
        tvEventPreRegistration = findViewById(R.id.activity_event_detail_tv_pre_registration)
        btnGenerateQr = findViewById(R.id.activity_qr_code_generate_btn_create)
        ivQr = findViewById(R.id.activity_event_detail_iv_qr_code)

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
            intent.putExtra("EventDetailEventPreRegistrationSend", eventDetailSendPreRegistration)

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
                val eventPreRegistration =
                    snapshot.child("eventPreRegister").getValue<String>(String::class.java)

                tvEventName.text = eventName
                tvEventOrganiser.text = eventOrganiser
                tvEventDate.text = eventDate
                tvEventTime.text = eventTime
                tvEventDescription.text = eventDescription
                tvEventLocation.text = eventLocation
                tvEventType.text = eventType
                tvEventPreRegistration.text = eventPreRegistration


//                eventDetailEventNameSend = eventName.toString()

                //store event details for edit

                eventDetailSendOrganiser = eventOrganiser.toString()
                eventDetailSendDate = eventDate.toString()
                eventDetailSendTime = eventTime.toString()
                eventDetailSendDescription = eventDescription.toString()
                eventDetailSendType = eventType.toString()
                eventDetailSendLocation = eventLocation.toString()
                eventDetailSendPreRegistration = eventPreRegistration.toString()

                if (eventDetailSendPreRegistration == "Yes") {

                    btnGenerateQr.visibility = View.VISIBLE

                }

                btnGenerateQr.setOnClickListener {
                    ivQr.visibility = View.VISIBLE
                    val writer = QRCodeWriter()
                    try {
                        val bitMatrix =
                            writer.encode("Event name: $eventName\n" +
                                    "Event date: $eventDate\n" +
                                    "Event time: $eventTime\n" +
                                    "Event organiser: $eventOrganiser\n" +
                                    "Registered user email: $userEmail\n",
                                BarcodeFormat.QR_CODE,
                                512,
                                512)
                        val width = bitMatrix.width
                        val height = bitMatrix.height
                        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
                        for (x in 0 until width) {
                            for (y in 0 until height) {
                                bmp.setPixel(x,
                                    y,
                                    if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                            }
                        }

                        ivQr.setImageBitmap(bmp)

                    } catch (e: WriterException) {
                        e.printStackTrace()
                    }
                }


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