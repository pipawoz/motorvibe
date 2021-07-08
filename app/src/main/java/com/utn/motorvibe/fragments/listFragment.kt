package com.utn.motorvibe.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.utn.motorvibe.R
import com.utn.motorvibe.adapters.MotorListAdapter
import com.utn.motorvibe.database.appDatabase
import com.utn.motorvibe.database.motorDao
import com.utn.motorvibe.entities.Motor

class listFragment : Fragment() {

    lateinit var v : View
    lateinit var recMotor : RecyclerView  //Recycler view object
    private lateinit var linearLayoutManager: LinearLayoutManager  //List view
    private lateinit var gridLayoutManager : GridLayoutManager // Grid view
    private var db: appDatabase? = null
    private var motorDao: motorDao? = null

    var motors : MutableList<Motor> = ArrayList<Motor>()
    private lateinit var motorListAdapter: MotorListAdapter

    companion object {
        fun newInstance() = listFragment()
        var selected_motor : Int = 0
        var selected_motor_name : String = ""
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v =  inflater.inflate(R.layout.list_fragment, container, false)
        recMotor = v.findViewById(R.id.rec_motors)  // Get recycler from view
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }

    override fun onStart() {  // When fragment is launched
        super.onStart()

        db = appDatabase.getAppDataBase(v.context)
        motorDao = db?.motorDao()

        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val grid_mode = prefs.getString("grid_mode", "grid_mode_list").toString()
        val display_images = prefs.getBoolean("display_images_setting", true)

        // Populate DB with some items
        if(motorDao?.getCount() == 0)
        {
            motorDao?.insertMotor(Motor(name = "Motor Tower", model = "Cooling Tower", status = "OK"))
            motorDao?.insertMotor(Motor(name = "Motor Backup", model = "JP-Aegis", status = "Failure"))
        }
        motors = motorDao?.loadAllMotors() as MutableList<Motor>
        recMotor.setHasFixedSize(true)  // View setting

        if(grid_mode == "grid_mode_list")
        {
            linearLayoutManager = GridLayoutManager(context, 1, RecyclerView.VERTICAL, false)  //Instance of layout manager
            recMotor.layoutManager = linearLayoutManager  //Object
        } else {
            gridLayoutManager = GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)  //Instance of layout manager
            recMotor.layoutManager = gridLayoutManager  //Object
        }

        motorListAdapter = MotorListAdapter(motors, grid_mode, display_images) { pos ->  //User iteration
            onItemClick(pos)
        }

        recMotor.adapter = motorListAdapter  //Send adapter to recycler view
    }

    private fun onItemClick (position : Int ) : Boolean {
        selected_motor = position
        selected_motor_name = motors[position].name
        val action = listFragmentDirections.actionListFragmentToContainerFragment()
        v.findNavController().navigate(action)
        return true
    }

}