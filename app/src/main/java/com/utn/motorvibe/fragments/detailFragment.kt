package com.utn.motorvibe.fragments

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
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.utn.motorvibe.R
import com.utn.motorvibe.database.motorDao
import com.utn.motorvibe.database.readingsDao
import com.utn.motorvibe.entities.Motor
import com.utn.motorvibe.entities.Reading
import kotlinx.coroutines.*

class detailFragment : Fragment() {
    lateinit var v: View

    private var db = FirebaseFirestore.getInstance()
    val parentJob = Job()
    val scope = CoroutineScope(Dispatchers.Default + parentJob)

    private var motorDao: motorDao? = null
    var motors: MutableList<Motor> = arrayListOf()

    private var readingsDao: readingsDao? = null
    var readings: MutableList<Reading> = ArrayList<Reading>()

    private lateinit var detail_name: TextView
    private lateinit var detail_model: TextView
    private lateinit var detail_description: TextView
    private lateinit var detail_image: ImageView
    private lateinit var selected_image: String

    lateinit var btnAddReading: Button
    lateinit var edtReading: EditText

    private var images_list =
        listOf("motor1", "motor2", "motor3", "motor4", "motor5", "motor6", "motor7")
    private var model_list =
        listOf("W22", "IEEE 841", "Cooling Tower", "Crusher Duty", "W40", "Aegis 1", "JP-Aegis")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_detail, container, false)
        detail_name = v.findViewById(R.id.detail_title)
        detail_model = v.findViewById(R.id.detail_model)
        detail_description = v.findViewById(R.id.detail_description)

        edtReading = v.findViewById(R.id.new_reading)
        btnAddReading = v.findViewById(R.id.btn_add_reading)

        detail_image = v.findViewById(R.id.detail_image)

        return v
    }

    override fun onStart() {
        super.onStart()


        scope.launch {
            val data = async { get_data() }
            data.await()
        }

        if (detail_description.text.toString() != "OK") {
            detail_description.setTextColor(resources.getColor(R.color.red))
        } else {
            detail_description.setTextColor(resources.getColor(R.color.green))
        }







        btnAddReading.setOnClickListener {
            if (edtReading.text.isEmpty()) {
                Snackbar.make(v, "Reading invalid syntax", Snackbar.LENGTH_LONG).show()
            } else {
                val input_reading: Double = edtReading.text.toString().toDouble()

                db.collection("readings").document(listFragment.selected_motor_name).get().addOnSuccessListener {
                    var readings_list = arrayListOf<Double>()
                    readings_list = it.get("readings") as ArrayList<Double>
                    readings_list.add(input_reading)

                    db.collection("readings").document(listFragment.selected_motor_name).set(
                        hashMapOf(
                            "name" to it.get("name"),
                            "readings" to readings_list
                        )
                    )
                } .addOnFailureListener { exception ->
                    //Log.d("TAG", "Motor didn't have readings",  it)
                    var readings_list = arrayListOf(input_reading)
                    readings_list.add(input_reading)
                    db.collection("readings").document(listFragment.selected_motor_name).set(
                        hashMapOf(
                            "name" to listFragment.selected_motor_name,
                            "readings" to readings_list
                        )
                    )
                }


                edtReading.text.clear()
            }
        }

    }

    suspend fun get_data() {
        db.collection("motors").document(listFragment.selected_motor_name)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("TAG", "DocumentSnapshot data: ${document.data}")

                    // Update text view
                    detail_name.text = document.get("name") as String?
                    detail_model.text = "Model: ".plus(document.get("model") as String?)
                    detail_description.text = document.get("status") as String?
                    selected_image = images_list[model_list.indexOf(document.get("model"))]

                    // Update image
                    val id = requireContext().resources.getIdentifier(
                        selected_image,
                        "drawable",
                        requireContext().packageName
                    )
                    detail_image.setImageResource(id)

                } else {
                    Log.d("TAG", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
    }

    fun get_motors(): List<Motor> {
        var motorList = arrayListOf<Motor>()
        return motorList
    }
}