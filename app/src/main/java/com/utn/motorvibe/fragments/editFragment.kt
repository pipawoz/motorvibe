package com.utn.motorvibe.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.utn.motorvibe.R
import kotlinx.android.synthetic.main.edit_fragment.view.*

class editFragment : Fragment() {

    lateinit var v: View

    private val db = FirebaseFirestore.getInstance()

    // private var motorDao: motorDao? = null
    //var motors: MutableList<Motor> = ArrayList<Motor>()
    //lateinit var motorList: MutableList<Motor>
    //lateinit var tempMotor: Motor

    private lateinit var edtName: EditText
    private lateinit var edtStatus: EditText
    private lateinit var edtModel: EditText

    private lateinit var btnAdd: Button
    private lateinit var btnDelete: Button
    private lateinit var btnEdit: Button

    lateinit var default_motor_status: String

    var i: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.edit_fragment, container, false)

        edtName = v.findViewById(R.id.edt_name)
        edtStatus = v.findViewById(R.id.edt_status)
        edtModel = v.findViewById(R.id.autoCompleteTextView)

        btnAdd = v.findViewById(R.id.btn_add)
        btnDelete = v.findViewById(R.id.btn_delete)
        btnEdit = v.findViewById(R.id.btn_editar)

        val models = resources.getStringArray(R.array.models_array)
        val arrayAdapter = ArrayAdapter(
            requireContext(),
            R.layout.dropdown_item, models
        )
        v.autoCompleteTextView.setAdapter(arrayAdapter)

        return v
    }

    override fun onStart() {
        super.onStart()

        val prefs =
            PreferenceManager.getDefaultSharedPreferences(requireContext())
        Log.d("Test", prefs.getBoolean("small_images", false).toString())

        edtName.text.clear()

        // add
        btnAdd.setOnClickListener {
            if (edtName.text.isNotEmpty() and (edtModel.text.toString() != "model")) {
                if (motorExists(edtName.text.toString())) {
                    Snackbar.make(
                        v,
                        "Motor name already exists. Please select another name",
                        Snackbar.LENGTH_LONG
                    ).show()
                } else {
                    default_motor_status = if (edtStatus.text.isEmpty()) {
                        prefs.getString("default_motor_status", "OK").toString()

                    } else {
                        edtStatus.text.toString()
                    }
                    db.collection("motors").document(edtName.text.toString()).set(
                        hashMapOf(
                            "name" to edtName.text.toString(),
                            "model" to edtModel.text.toString(),
                            "status" to default_motor_status.toString()
                        )
                    )
                    Snackbar.make(v, "Motor added", Snackbar.LENGTH_LONG).show()
                    clearFields()
                }
            } else {
                Snackbar.make(v, "Input invalid syntax", Snackbar.LENGTH_LONG).show()
            }
        }

        btnDelete.setOnClickListener {
            if (edtName.text.isNotEmpty()) {
                if (motorExists(edtName.text.toString())) {
                    db.collection("motors").document(edtName.text.toString()).delete()
                    Snackbar.make(v, "Motor deleted", Snackbar.LENGTH_LONG).show()
                    clearFields()
                } else {
                    Snackbar.make(
                        v,
                        "Motor name does not exist. Please select another name",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            } else {
                Snackbar.make(v, "Name invalid syntax", Snackbar.LENGTH_LONG).show()
            }
        }

        btnEdit.setOnClickListener {
            if (edtName.text.isNotEmpty() and (edtModel.text.isNotEmpty())) {
                if (motorExists(edtName.text.toString())) {
                    default_motor_status = if (edtStatus.text.isEmpty()) {
                        prefs.getString("default_motor_status", "OK").toString()
                    } else {
                        edtStatus.text.toString()
                    }
                    db.collection("motors").document(edtName.text.toString()).set(
                        hashMapOf(
                            "name" to edtName.text.toString(),
                            "model" to edtModel.text.toString(),
                            "status" to default_motor_status.toString()
                        )
                    )
                    Snackbar.make(v, "Motor edited", Snackbar.LENGTH_LONG).show()
                    clearFields()
                } else {
                    Snackbar.make(
                        v,
                        "Motor name does not exist. Please select another name",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            } else {
                Snackbar.make(v, "Input invalid syntax", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun motorExists(motor_name: String): Boolean {
        val docRef = db.collection("motors").document(motor_name)
        var motor_exist = false
        try {
            docRef.get()
            motor_exist = true
        } catch(e: Exception) {
            //do nothing
        }
        return motor_exist
    }

    private fun clearFields() {
        edtName.text.clear()
        edtStatus.text.clear()
        edtModel.text.clear()
    }
}
