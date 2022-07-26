package com.example.epi_event.qr_code

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.epi_event.R
import android.content.ClipData
import android.content.ClipboardManager
import android.content.DialogInterface
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.epi_event.databinding.FragmentQrResultBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*

const val ARG_SCANNING_RESULT = "scanning_result"

class ScannerResultDialog(private val listener: DialogDismissListener) :
    BottomSheetDialogFragment() {

    private lateinit var binding: FragmentQrResultBinding
    private var eventListArray = LinkedList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentQrResultBinding.inflate(
            inflater, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val scannedResult = arguments?.getString(ARG_SCANNING_RESULT)
        binding.edtResult.setText(scannedResult)
        binding.btnCopy.setOnClickListener {
            val clipboard =
                ContextCompat.getSystemService(requireContext(), ClipboardManager::class.java)
            val clip = ClipData.newPlainText("label", scannedResult)
            clipboard?.setPrimaryClip(clip)
            Toast.makeText(requireContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show()
            dismissAllowingStateLoss()
        }
    }


    companion object {

        fun newInstance(
            scanningResult: String,
            listener: DialogDismissListener,
        ): ScannerResultDialog =
            ScannerResultDialog(listener).apply {
                arguments = Bundle().apply {
                    putString(ARG_SCANNING_RESULT, scanningResult)
                    Log.d("dataQR", scanningResult)

                    //Database Reference
                    var currentUserId: String =
                        FirebaseAuth.getInstance().currentUser!!.uid.toString();
                    Log.d("testFirebaseID", currentUserId)
                    var databaseReference: DatabaseReference =
                        FirebaseDatabase.getInstance("https://epita-event-signup-default-rtdb.europe-west1.firebasedatabase.app/")
                            .getReference("Registration")

                    Log.d("scannedResult", scanningResult)
                    var out = scanningResult.split("\n");
                    Log.d("splitString", out.get(1))

                    //Get event name

                    var scannedEventNameInfo = out.get(0).split(":")
                    var scannedEventName: String = scannedEventNameInfo.get(1)

                    //Get event date

                    var scannedEventDateInfo = out.get(1).split(":")
                    var scannedEventDate: String = scannedEventDateInfo.get(1)

                    //get Registered email

                    var scannedUserEmailInfo = out.get(4).split(":")
                    var scannedUserEmail: String = scannedUserEmailInfo.get(1)

                    //get unique key

                    var scannedUserKeyInfo = out.get(5).split(":")
                    var scannedUserKey: String = scannedUserKeyInfo.get(1)

                    Log.d("checkDataExtract",
                        scannedEventName + scannedEventDate + " " + scannedUserEmail + "" + scannedUserKey)


                    databaseReference.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
//                    Log.d("DataSnapshotName", dataSnapshot.toString())
                            for (childSnapshot in dataSnapshot.children) {
                                Log.d("childName", childSnapshot.key.toString())
                                eventListArray.add(childSnapshot.key.toString())
                            }


                            //Check event name
                            if (eventListArray.contains(scannedEventName.trim())) {
                                Log.d("eventNameFound", "found")

                                //Check details of event with userID

                                var eventRef = databaseReference.child(scannedEventName.trim())
                                    .child(currentUserId.trim())

                                eventRef.addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val registeredUserEmail =
                                            snapshot.child("registeredUserEmail")
                                                .getValue<String>(String::class.java)
                                        val registeredUserNumber =
                                            snapshot.child("registeredUserNumber")
                                                .getValue<String>(String::class.java)

                                        Log.d("keyCheck",
                                            registeredUserNumber.toString() + scannedUserKey)

                                        if (registeredUserEmail != null) {
                                            if (registeredUserNumber != null) {
                                                if (registeredUserEmail.trim() == scannedUserEmail.trim()) {
                                                    if (registeredUserNumber.trim() == scannedUserKey.trim()) {
                                                        binding.fragmentQrResultTvVerification.setText(
                                                            "Verified")
                                                    } else {
                                                        binding.fragmentQrResultTvVerification.setText(
                                                            "Not Verified.\nInvalid registration number")
                                                    }
                                                } else {
                                                    binding.fragmentQrResultTvVerification.setText("Not Verified.\nInvalid user email")
                                                }
                                            }

                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }

                                })


                            } else {
                                binding.fragmentQrResultTvVerification.setText("Not Verified.\nEvent not found")

                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.d("errorGettingChild", error.message)
                        }
                    })


                }
            }

        private fun <T> isPresent(arr: Array<T>, target: T): Boolean {
            return arr.contains(target)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener.onDismiss()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        listener.onDismiss()
    }

    interface DialogDismissListener {
        fun onDismiss()
    }
}