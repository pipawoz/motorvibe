package com.utn.motorvibe.fragments


import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import app.akexorcist.bluetotohspp.library.BluetoothSPP
import app.akexorcist.bluetotohspp.library.BluetoothState
import app.akexorcist.bluetotohspp.library.DeviceList
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.polidea.rxandroidble2.RxBleClient
import com.utn.motorvibe.R
import com.utn.motorvibe.database.motorDao
import com.utn.motorvibe.database.readingsDao
import com.utn.motorvibe.entities.Motor
import com.utn.motorvibe.entities.Reading
import com.utn.motorvibe.fragments.listFragment.Companion.selected_motor_list
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job


class detailFragment : Fragment() {
    lateinit var v: View

    private var db = FirebaseFirestore.getInstance()
    private val parentJob = Job()
    private val scope = CoroutineScope(Dispatchers.Default + parentJob)

    private var motorDao: motorDao? = null
    var motors: MutableList<Motor> = arrayListOf()

    private var readingsDao: readingsDao? = null
    var readings: MutableList<Reading> = ArrayList<Reading>()

    private lateinit var detailName: TextView
    private lateinit var detailModel: TextView
    private lateinit var detailDescription: TextView
    private lateinit var detailImage: ImageView
    private lateinit var selectedImage: String

    lateinit var btnAddReading: Button
    lateinit var btnBTReading: Button
    lateinit var edtReading: EditText


    //Bluetooth no anda
    lateinit var bt: BluetoothSPP

    //Otro BT
    lateinit var rxBleClient: RxBleClient

    private var imagesList =
        listOf("motor1", "motor2s", "motor3", "motor4", "motor5", "motor6", "motor7")
    private var modelList =
        listOf("W22", "IEEE 841", "Cooling Tower", "Crusher Duty", "W40", "Aegis 1", "JP-Aegis")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_detail, container, false)
        detailName = v.findViewById(R.id.detail_title)
        detailModel = v.findViewById(R.id.detail_model)
        detailDescription = v.findViewById(R.id.detail_description)

        edtReading = v.findViewById(R.id.new_reading)
        btnAddReading = v.findViewById(R.id.btn_add_reading)
        btnBTReading = v.findViewById(R.id.btn_read_bt)

        detailImage = v.findViewById(R.id.detail_image)

        return v
    }

    override fun onStart() {
        super.onStart()

        bt = BluetoothSPP(requireContext())

        bt.setOnDataReceivedListener { data, message ->
            Snackbar.make(v, "Reading invalid syntax ${message} ${data}", Snackbar.LENGTH_LONG)
                .show()
        }
        //rxBleClient = RxBleClient.create(requireContext())

        // Update text view
        detailName.text = selected_motor_list.name
        detailModel.text = "Model: ".plus(selected_motor_list.model)
        detailDescription.text = selected_motor_list.status
        selectedImage = imagesList[modelList.indexOf(selected_motor_list.model)]

        // Update image
        val id = requireContext().resources.getIdentifier(
            selectedImage,
            "drawable",
            requireContext().packageName
        )
        detailImage.setImageResource(id)

        // color change
        if (detailDescription.text.toString() != "OK") {
            detailDescription.setTextColor(resources.getColor(R.color.red))
        } else {
            detailDescription.setTextColor(resources.getColor(R.color.green))
        }

        // Enable bluetooth
        if (!bt.isBluetoothEnabled) {
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT)
        } else {
            bt.setupService()
            bt.startService(BluetoothState.DEVICE_OTHER)
        }


        // add new readings
        btnAddReading.setOnClickListener {
            if (edtReading.text.isEmpty()) {
                Snackbar.make(v, "Reading invalid syntax", Snackbar.LENGTH_LONG).show()
            } else {
                val inputReading: Double = edtReading.text.toString().toDouble()

                db.collection("readings").document(selected_motor_list.name).get()
                    .addOnSuccessListener {
                        var readingsList: ArrayList<Double> =
                            it.get("readings") as ArrayList<Double>
                        readingsList.add(inputReading)  // Agrego nueva medicion

                        db.collection("readings").document(selected_motor_list.name).set(
                            hashMapOf(
                                "name" to it.get("name"),
                                "readings" to readingsList
                            )
                        )
                    }.addOnFailureListener {
                        // Si no habia mediciones en firebase creo nuevo registro
                        var readingsList = arrayListOf(inputReading)
                        readingsList.add(inputReading)
                        db.collection("readings").document(selected_motor_list.name).set(
                            hashMapOf(
                                "name" to selected_motor_list.name,
                                "readings" to readingsList
                            )
                        )
                    }
                edtReading.text.clear()
            }
        }

        // read from bt
        btnBTReading.setOnClickListener {
            if (bt.serviceState == BluetoothState.STATE_CONNECTED) {
                bt.disconnect()
            } else {
                val intent = Intent(requireContext(), DeviceList::class.java)
                startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE)
            }
        }

        bt.setBluetoothConnectionListener(object : BluetoothSPP.BluetoothConnectionListener {
            override fun onDeviceConnected(name: String, address: String) {
                Log.d("TAG", "Connected: ${address}")
            }

            override fun onDeviceDisconnected() {
                Log.d("TAG", "Disconnected device")
            }

            override fun onDeviceConnectionFailed() {
                Log.d("TAG", "Connection failed")

            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService()
                bt.startService(BluetoothState.DEVICE_OTHER)
                bt.connect(data)
            }
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService()
                bt.startService(BluetoothState.DEVICE_OTHER)
            } else {
                // Do something if user doesn't choose any device (Pressed back)
            }
        }
    }
}