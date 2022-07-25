package com.example.epi_event.create_event

import android.R
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.epi_event.ProfileActivity
import com.example.epi_event.databinding.ActivityCreateEventBinding
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.time.Month
import java.util.*


class CreateEventActivity : AppCompatActivity() {

    //ViewBinding
    private lateinit var binding: ActivityCreateEventBinding

    //ActionBar
    private lateinit var actionBar: ActionBar

    //FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth

    //Database Reference
    private lateinit var databaseReference: DatabaseReference

    //ProgressDialog
    private lateinit var progressDialog: ProgressDialog

    //Get image
    private val pickImage = 100
    private var imageUri: Uri? = null

    //get name of event for edit event

    private var eventDetailEventStatus: Int = 0
    private lateinit var eventDetailEventName: String
    private lateinit var eventDetailEventOrganiser: String
    private lateinit var eventDetailEventDate: String
    private lateinit var eventDetailEventTime: String
    private lateinit var eventDetailEventLocation: String
    private lateinit var eventDetailEventDescription: String
    private lateinit var eventDetailEventType: String
    private lateinit var eventDetailPreRegister: String


    //Get edit text data
    private var eventName = ""
    private var eventOrganiser = ""
    private var eventDate = ""
    private var eventTime = ""
    private var eventDescription = ""
    private var eventLocation = ""
    private var eventType = ""
    private var eventPreRegister = ""


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get data from event details

        eventDetailEventStatus =
            intent.getIntExtra("EventDetailEventConditionSend", 0)

        eventDetailEventName = intent.getStringExtra("EventDetailEventNameSend").toString()

        eventDetailEventOrganiser =
            intent.getStringExtra("EventDetailEventOrganiserSend").toString()

        eventDetailEventDate = intent.getStringExtra("EventDetailEventDateSend").toString()

        eventDetailEventTime = intent.getStringExtra("EventDetailEventTimeSend").toString()

        eventDetailEventLocation = intent.getStringExtra("EventDetailEventLocationSend").toString()

        eventDetailEventDescription =
            intent.getStringExtra("EventDetailEventDescriptionSend").toString()

        eventDetailEventType = intent.getStringExtra("EventDetailEventTypeSend").toString()

        eventDetailPreRegister = intent.getStringExtra("EventDetailEventPreRegistrationSend").toString()


        Log.d("receivedNameEvent", eventDetailEventName)


        bindActivity()


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun bindActivity() {
//        actionBar = supportActionBar!!
//        actionBar.title = "Create Event"

        //get switch id
//        switchPreRegister = binding.activityCreateEventSwitchPreRegister

        //Configure progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Creating Event")
        progressDialog.setMessage("Please wait..")
        progressDialog.setCanceledOnTouchOutside(false)

        if (eventDetailEventStatus == 1) {

            editData()
            binding.activityCreateEventIvDeleteEvent.visibility = View.VISIBLE

        }

        binding.activityCreateEventIvDeleteEvent.setOnClickListener {
            deleteData()

        }

        //Set Date

        binding.activityCreateEventIvDate.setOnClickListener {
            //getting current day,month and year.
            setDateField()

        }

        //Set Time
        binding.activityCreateEventIvTime.setOnClickListener {
            setTimeField()
        }

        //            get pre-register switch

        binding.activityCreateEventSwitchPreRegister.setOnClickListener {
            if (binding.activityCreateEventSwitchPreRegister.isChecked) {
                binding.activityCreateEventTvPreRegister.text = "Yes"
                Toast.makeText(this, "Pre-Registration enabled", Toast.LENGTH_SHORT).show();
            } else {
                binding.activityCreateEventTvPreRegister.text = "No"
                Toast.makeText(this, "Pre-Registration disabled", Toast.LENGTH_SHORT).show();
            }
        }

        //submit Event Data
        binding.activityCreateEventBtnSubmitEvent.setOnClickListener {
            //Get Data

            getData()

        }

        //Select image

        binding.activityCreateEventIvImage.setOnClickListener {
            selectImage()
        }

    }

    private fun deleteData() {
        Log.d("nameDelete", eventDetailEventName)
        databaseReference =
            FirebaseDatabase.getInstance("https://epita-event-signup-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Events/").child(eventDetailEventName)

        databaseReference.removeValue()
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
        finish()

        Toast.makeText(this, "Deleted event", Toast.LENGTH_SHORT).show()


    }

    private fun editData() {
        binding.activityCreateEventEtName.setText(eventDetailEventName)
        binding.activityCreateEventEtName.isEnabled = false

        binding.activityCreateEventEtOrganiser.setText(eventDetailEventOrganiser,
            TextView.BufferType.EDITABLE)

        Log.d("dateCheck", eventDetailEventDate)

        //make date and time view visible
        binding.activityCreateEventTvDate.visibility = View.VISIBLE
        binding.activityCreateEventTvTime.visibility = View.VISIBLE

        binding.activityCreateEventTvDate.text = eventDetailEventDate

        binding.activityCreateEventTvTime.text = eventDetailEventTime

        binding.activityCreateEventEtLocation.setText(eventDetailEventLocation,
            TextView.BufferType.EDITABLE)

        binding.activityCreateEventEtLocation.setText(eventDetailEventLocation,
            TextView.BufferType.EDITABLE)

        binding.activityCreateEventEtDescription.setText(eventDetailEventDescription,
            TextView.BufferType.EDITABLE)

        binding.activityCreateEventEtType.setText(eventDetailEventType,
            TextView.BufferType.EDITABLE)


//        eventDescription = binding.activityCreateEventEtDescription.text.toString().trim()
//        eventLocation = binding.activityCreateEventEtLocation.text.toString().trim()
//        eventType = binding.activityCreateEventEtType.text.toString().trim()
    }

    private fun selectImage() {

        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, pickImage)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK) {

            imageUri = data?.data!!
            binding.activityCreateEventIvImage.setImageURI(imageUri)

        }
    }

    private fun getData() {

        //Get Data

        eventName = binding.activityCreateEventEtName.text.toString().trim()
        eventOrganiser = binding.activityCreateEventEtOrganiser.text.toString().trim()
        eventDate = binding.activityCreateEventTvDate.text.toString().trim()
        eventTime = binding.activityCreateEventTvTime.text.toString().trim()
        eventDescription = binding.activityCreateEventEtDescription.text.toString().trim()
        eventLocation = binding.activityCreateEventEtLocation.text.toString().trim()
        eventType = binding.activityCreateEventEtType.text.toString().trim()
        eventPreRegister = binding.activityCreateEventTvPreRegister.text.toString().trim()

        //Validate Data

        if (TextUtils.isEmpty(eventName)) {
            //empty Event Name
            binding.activityCreateEventEtName.error = "Event name cannot be empty"
        } else if (eventName.length < 3) {
            //Event Name condition
            binding.activityCreateEventEtName.error =
                "Please enter event name of more than 3 characters"
        } else if (TextUtils.isEmpty(eventOrganiser)) {
            //No event organiser entered
            binding.activityCreateEventEtOrganiser.error = "Event organiser cannot be empty"
        } else if (eventOrganiser.length < 3) {
            //Event organiser  condition
            binding.activityCreateEventEtOrganiser.error =
                "Please enter event organiser name of more than 3 characters"
        } else if (eventDate == null) {
            binding.activityCreateEventTvDate.error = "Event date cannot be empty"
        } else if (eventTime == null) {
            binding.activityCreateEventTvTime.error = "Event time cannot be empty"
        } else if (TextUtils.isEmpty(eventDescription)) {
            //No event description entered
            binding.activityCreateEventEtDescription.error = "Event description cannot be empty"
        } else if (eventDescription.length < 5) {
            //Event description condition
            binding.activityCreateEventEtDescription.error =
                "Please enter event description  of more than 5 characters"
        } else if (TextUtils.isEmpty(eventLocation)) {
            //No event location entered
            binding.activityCreateEventEtLocation.error = "Event location cannot be empty"
        } else if (eventLocation.length < 3) {
            //Event location condition
            binding.activityCreateEventEtLocation.error =
                "Please enter event location  of more than 3 characters"
        } else if (TextUtils.isEmpty(eventType)) {
            //No event type entered
            binding.activityCreateEventEtType.error = "Event type cannot be empty"
        } else if (eventType.length < 3) {
            //Event type condition
            binding.activityCreateEventEtType.error =
                "Please enter event type  of more than 3 characters"
        } else if (imageUri == null) {
            Toast.makeText(this, "Please select image of the event", Toast.LENGTH_SHORT).show()
        } else {
            progressDialog.show()

            saveEventData()

            //open profile activity Activity
            progressDialog.dismiss()
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            finish()


        }

    }

    private fun saveEventData() {

        //Store data

//        val uid = firebaseAuth.currentUser!!.uid


        val createNewEvent = CreateEvent(eventName,
            eventOrganiser,
            eventDate,
            eventTime,
            eventDescription,
            eventLocation,
            eventType,
            eventPreRegister)

        databaseReference =
            FirebaseDatabase.getInstance("https://epita-event-signup-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Events")

        databaseReference.child(eventName).setValue(createNewEvent)
            .addOnSuccessListener {

                saveImageToFirebase(imageUri!!)
                Toast.makeText(this, "saved event", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this,
                    "Saving to database failed because ${e.message}",
                    Toast.LENGTH_SHORT)
                    .show()
            }


    }

    private fun saveImageToFirebase(fileUri: Uri) {

//        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
//        val now = Date()
//        val fileName = formatter.format(now)
//        val storageReference = FirebaseS

        if (fileUri != null) {
//            val fileName = UUID.randomUUID().toString() + ".jpg"

//            val database = FirebaseDatabase.getInstance()
            val refStorage =
                FirebaseStorage.getInstance("gs://epita-event-signup.appspot.com")
                    .reference.child("event-image/$eventName")

            refStorage.putFile(fileUri)
                .addOnSuccessListener {
                    binding.activityCreateEventIvImage.setImageURI(null)

                }

                .addOnFailureListener(OnFailureListener { e ->

                    Toast.makeText(this,
                        "Image cannot be uploaded because ${e.message}",
                        Toast.LENGTH_SHORT).show()
                })
        }


    }

    private fun setTimeField() {
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            binding.activityCreateEventTvTime.visibility = View.VISIBLE
            binding.activityCreateEventTvTime.text = SimpleDateFormat("HH:mm").format(cal.time)
        }
        TimePickerDialog(this,
            timeSetListener,
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true).show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setDateField() {
        val calendar = Calendar.getInstance()

        val yearNow = calendar.get(Calendar.YEAR)
        val monthNow = calendar.get(Calendar.MONTH)
        val dayNow = calendar.get(Calendar.DAY_OF_MONTH)
        Log.d("currentDate", "$monthNow $dayNow ")
        val datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener
        { _, year, monthOfYear, dayOfMonth ->

            Log.d("chooseDate", "$year $monthOfYear $dayOfMonth")

            //For choosing a valid date


            if (year < yearNow || monthOfYear < monthNow) {

                Toast.makeText(this, "Please choose a valid date", Toast.LENGTH_SHORT).show()

            }
            //day can be less but month cannot be less so put filter
            else if (dayOfMonth < dayNow) {

                if (monthOfYear > monthNow) {
                    val eventMonth = Month.of(monthOfYear + 1)
                    val eventYear = year

                    binding.activityCreateEventTvDate.visibility = View.VISIBLE
                    binding.activityCreateEventTvDate.text = "$dayOfMonth $eventMonth $eventYear"
                } else {
                    Toast.makeText(this, "Please choose a valid date", Toast.LENGTH_SHORT).show()

                }

            } else {
                val eventMonth = Month.of(monthOfYear + 1)
                val eventYear = year

                binding.activityCreateEventTvDate.visibility = View.VISIBLE
                binding.activityCreateEventTvDate.text = "$dayOfMonth $eventMonth $eventYear"
            }

        }, yearNow, monthNow, dayNow)

        datePickerDialog.show()

    }
}