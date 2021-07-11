package com.utn.motorvibe.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.utn.motorvibe.R
import com.utn.motorvibe.adapters.MotorHolder
import com.utn.motorvibe.entities.Motor

class listFragment : Fragment() {

    lateinit var v: View
    lateinit var recMotor: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: FirestoreRecyclerAdapter<Motor, MotorHolder>
    private var firestoreListener: ListenerRegistration? = null

    private var motorList = mutableListOf<Motor>()


    companion object {
        fun newInstance() = listFragment()
        var selected_motor: Int = 0
        var selected_motor_name: String = ""
        lateinit var selected_motor_list : Motor
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.list_fragment, container, false)
        recMotor = v.findViewById(R.id.rec_motors)
        recMotor.setHasFixedSize(true)
        recMotor.layoutManager = LinearLayoutManager(context)
        return v
    }

    override fun onStart() {
        super.onStart()
        val db = FirebaseFirestore.getInstance()

        load_motor_list()

        /*
        firestoreListener = db!!.collection("motors")
            .addSnapshotListener(EventListener { documentSnapshots, e ->
                if (e != null) {
                    Log.e("TAG", "Listen failed!", e)
                    return@EventListener
                }

                motorList = mutableListOf()

                if (documentSnapshots != null) {
                    for (doc in documentSnapshots) {
                        val fb_motor = doc.toObject(Motor::class.java)
                        motorList.add(fb_motor)
                    }
                }
                adapter!!.notifyDataSetChanged()
            })

         */
    }

    private fun onItemClick(position: Int, motor: Motor): Boolean {
        selected_motor = position
        selected_motor_name = motor.name
        selected_motor_list = motor
        val action = listFragmentDirections.actionListFragmentToContainerFragment()
        v.findNavController().navigate(action)
        return true
    }

    private fun load_motor_list() {
        //val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        //val grid_mode = prefs.getString("grid_mode", "grid_mode_list").toString()
        //val display_images = prefs.getBoolean("display_images_setting", true)

        val docRef = FirebaseFirestore.getInstance()
        val query = docRef.collection("motors")

        val options = FirestoreRecyclerOptions.Builder<Motor>()
            .setQuery(query, Motor::class.java)
            .build()

        adapter = object : FirestoreRecyclerAdapter<Motor, MotorHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MotorHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_motor_list, parent, false)
                return MotorHolder(view)
            }

            override fun onBindViewHolder(holder: MotorHolder, position: Int, motor: Motor) {
                var selectedImage: String? = ""
                var imagesList =
                    listOf("motor1", "motor2", "motor3", "motor4", "motor5", "motor6", "motor7")
                var modelList = listOf(
                    "W22", "IEEE 841", "Cooling Tower", "Crusher Duty",
                    "W40", "Aegis 1", "JP-Aegis"
                )

                Log.d("TAG", "DocumentSnapshot data: ${motor.name}")

                holder.setName(motor.name)
                holder.setModel(motor.model)
                holder.setStatus("Status: ".plus(motor.status))


                selectedImage = imagesList[modelList.indexOf(motor.model)]
                val id = holder.getImageView().context.resources.getIdentifier(
                    selectedImage, "drawable", holder.getImageView().context.packageName
                )

                holder.getImageView().setImageResource(id)

                holder.getCardLayout().setOnClickListener { //For each card, set listener
                    onItemClick(position, motor)
                }
            }

            override fun onDataChanged() {
                super.onDataChanged()
            }
        }

        adapter.startListening()
        recMotor.adapter = adapter
        Log.d("TAG", "Configure adapter")
    }

}