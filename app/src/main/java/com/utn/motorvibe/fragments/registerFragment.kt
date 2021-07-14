package com.utn.motorvibe.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.utn.motorvibe.R

class registerFragment : Fragment() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth

    private lateinit var v: View

    private lateinit var passwordInput: EditText
    private lateinit var verifyPasswordInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var signupButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = layoutInflater.inflate(R.layout.fragment_register, container, false)

        passwordInput = v.findViewById(R.id.txt_password)
        signupButton = v.findViewById(R.id.btn_register)
        verifyPasswordInput = v.findViewById(R.id.txt_verify_password)
        emailInput = v.findViewById(R.id.txt_register_email)

        return v
    }

    override fun onStart() {
        super.onStart()
        val auth = Firebase.auth

        // For debug
        emailInput.setText("adminuser@gmail.com")
        passwordInput.setText("adminuser")
        verifyPasswordInput.setText("adminuser")

        signupButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            val verifyPassword = verifyPasswordInput.text.toString()

            if ((email.isEmpty()) or (password.isEmpty())) {
                Snackbar.make(
                    v,
                    resources.getString(R.string.login_error_invalid_syntax),
                    Snackbar.LENGTH_LONG
                ).show()
            } else {
                if (password != verifyPassword) {
                    Snackbar.make(
                        v,
                        resources.getString(R.string.login_error_passwords),
                        Snackbar.LENGTH_LONG
                    ).show()
                } else {
                    auth.createUserWithEmailAndPassword(
                        email, password
                    ).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Snackbar.make(
                                v,
                                resources.getString(R.string.login_user_registered),
                                Snackbar.LENGTH_LONG
                            ).show()

                            val action =
                                registerFragmentDirections.actionRegisterFragment2ToLoginFragment2()

                            // Create a user db in firestore also
                            db.collection("Users").document(email)
                                .set(
                                    hashMapOf(
                                        "email" to email,
                                        "password" to password
                                    )
                                )
                            v.findNavController().navigate(action)
                        } else {
                            Snackbar.make(
                                v,
                                it.exception.message.toString(),
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }
}