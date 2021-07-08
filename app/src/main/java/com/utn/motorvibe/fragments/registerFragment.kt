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
import com.google.firebase.firestore.FirebaseFirestore
import com.utn.motorvibe.R
import com.utn.motorvibe.entities.User

class registerFragment : Fragment() {
    private val db_fb = FirebaseFirestore.getInstance()

    lateinit var v: View

    lateinit var usernameInput: EditText
    lateinit var passwordInput: EditText
    lateinit var verifyPasswordInput: EditText
    lateinit var emailInput: EditText

    lateinit var signupButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = layoutInflater.inflate(R.layout.fragment_register, container, false)

        usernameInput = v.findViewById(R.id.txt_register_email)
        passwordInput = v.findViewById(R.id.txt_password)
        signupButton = v.findViewById(R.id.btn_register)
        verifyPasswordInput = v.findViewById(R.id.txt_verify_password)
        emailInput = v.findViewById(R.id.txt_register_email)

        return v
    }

    override fun onStart() {
        super.onStart()

        // For debug
        emailInput.setText("adminuser@gmail.com")
        passwordInput.setText("adminuser")
        verifyPasswordInput.setText("adminuser")

        signupButton.setOnClickListener {
            if ((emailInput.text.toString().isEmpty()) or (passwordInput.text.toString()
                    .isEmpty())
            ) {
                Snackbar.make(v, "Email/Password invalid syntax", Snackbar.LENGTH_LONG).show()
            } else {
                if (passwordInput.text.toString() != verifyPasswordInput.text.toString()) {
                    Snackbar.make(v, "Passwords do not match", Snackbar.LENGTH_LONG).show()
                } else {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                        emailInput.text.toString(),
                        passwordInput.text.toString()
                    ).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Snackbar.make(v, "User registered", Snackbar.LENGTH_LONG).show()

                            val action =
                                registerFragmentDirections.actionRegisterFragment2ToLoginFragment2()

                            // Firestore
                            db_fb.collection("Users").document(emailInput.text.toString()).set(
                                hashMapOf(
                                    "email" to emailInput.text.toString(),
                                    "password" to passwordInput.text.toString()
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