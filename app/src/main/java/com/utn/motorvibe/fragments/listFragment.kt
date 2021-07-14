package com.utn.motorvibe.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.akexorcist.bluetotohspp.library.BluetoothSPP
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.utn.motorvibe.R
import com.utn.motorvibe.adapters.MotorHolder
import com.utn.motorvibe.entities.Motor

class listFragment : Fragment() {

    lateinit var v: View
    lateinit var v2: View
    lateinit var recMotor: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: FirestoreRecyclerAdapter<Motor, MotorHolder>
    private var firestoreListener: ListenerRegistration? = null
    private lateinit var motor_image: ImageView
    lateinit var bt: BluetoothSPP

    private var motorList = mutableListOf<Motor>()

    companion object {
        fun newInstance() = listFragment()
        var selected_motor: Int = 0
        var selected_motor_name: String = ""
        lateinit var selected_motor_list: Motor
    }

    @SuppressLint("ResourceType")
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

        loadMotorList()
    }

    private fun onItemClick(position: Int, motor: Motor): Boolean {
        selected_motor = position
        selected_motor_name = motor.name
        selected_motor_list = motor
        val action = listFragmentDirections.actionListFragmentToDetailFragment()
        v.findNavController().navigate(action)
        return true
    }

    private fun loadMotorList() {

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

                /*
                //Load form storage
                val img = v.findViewById<ImageView>(R.id.img_item);
                val motor_image = holder.getImageView()

                val storageRef = FirebaseStorage.getInstance().reference
                val pathReference = storageRef.child(selectedImage)
                Glide.with(v.context)
                    .load(storageRef)
                    .into(motor_image)
                */

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
    }

}