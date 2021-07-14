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

    private lateinit var edtName: EditText
    private lateinit var edtStatus: EditText
    private lateinit var edtModel: EditText

    private lateinit var btnAdd: Button
    private lateinit var btnDelete: Button
    private lateinit var btnEdit: Button

    lateinit var defaultMotorStatus: String

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

        btnAdd.setOnClickListener {
            val inputName = edtName.text.toString()
            val inputModel = edtModel.text.toString()
            val inputStatus = edtStatus.text.toString()

            if (inputName.isNotEmpty() and (inputModel != "model")) {
                if (motorExists(inputName)) {
                    Snackbar.make(
                        v,
                        resources.getString(R.string.edt_motor_exists),
                        Snackbar.LENGTH_LONG
                    ).show()
                } else {
                    defaultMotorStatus = if (inputStatus.isEmpty()) {
                        prefs.getString("default_motor_status", "OK").toString()
                    } else {
                        inputStatus
                    }

                    // Add Motor to collection
                    db.collection("motors").document(inputName).set(
                        hashMapOf(
                            "name" to inputName,
                            "model" to inputModel,
                            "status" to defaultMotorStatus
                        )
                    )

                    // Create a empty readings collection for that Motor
                    db.collection("readings").document(inputName).set(
                        hashMapOf(
                            "name" to inputName,
                            "readings" to arrayListOf(0.0)
                        )
                    )
                    Snackbar.make(
                        v,
                        resources.getString(R.string.edt_motor_added),
                        Snackbar.LENGTH_LONG
                    ).show()
                    clearFields()
                }
            } else {
                Snackbar.make(
                    v,
                    resources.getString(R.string.edt_invalid_syntax),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        btnDelete.setOnClickListener {
            val inputName = edtName.text.toString()

            if (inputName.isNotEmpty()) {
                if (motorExists(inputName)) {
                    db.collection("motors").document(inputName).delete()
                    Snackbar.make(
                        v,
                        resources.getString(R.string.edt_motor_deleted),
                        Snackbar.LENGTH_LONG
                    ).show()
                    clearFields()
                } else {
                    Snackbar.make(
                        v,
                        resources.getString(R.string.edt_motor_not_exists),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            } else {
                Snackbar.make(
                    v,
                    resources.getString(R.string.edt_invalid_syntax),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        btnEdit.setOnClickListener {
            val inputName = edtName.text.toString()
            val inputModel = edtModel.text.toString()
            val inputStatus = edtStatus.text.toString()

            if (inputName.isNotEmpty() and (inputModel.isNotEmpty())) {
                if (motorExists(inputName)) {
                    defaultMotorStatus = if (inputStatus.isEmpty()) {
                        prefs.getString("default_motor_status", "OK").toString()
                    } else {
                        inputStatus
                    }
                    db.collection("motors").document(inputName).set(
                        hashMapOf(
                            "name" to inputName,
                            "model" to inputModel,
                            "status" to defaultMotorStatus
                        )
                    )
                    Snackbar.make(v, "Motor edited", Snackbar.LENGTH_LONG).show()
                    clearFields()
                } else {
                    Snackbar.make(
                        v,
                        resources.getString(R.string.edt_motor_not_exists),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            } else {
                Snackbar.make(
                    v,
                    resources.getString(R.string.edt_invalid_syntax),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun motorExists(motor_name: String): Boolean {
        val docRef = db.collection("motors").document(motor_name)
        var motorExist = false
        try {
            docRef.get().addOnSuccessListener {
                motorExist = true
            }.addOnFailureListener {
                motorExist = false
            }
        } catch (e: Exception) {
            //do nothing
        }
        return motorExist
    }

    private fun clearFields() {
        edtName.text.clear()
        edtStatus.text.clear()
        edtModel.text.clear()
    }
}
