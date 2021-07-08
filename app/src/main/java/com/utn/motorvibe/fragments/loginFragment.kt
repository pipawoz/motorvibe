package com.utn.motorvibe.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.utn.motorvibe.MainActivity
import com.utn.motorvibe.R

class loginFragment : Fragment() {

    private val GOOGLE_SIGN_IN = 100

    lateinit var v: View

    lateinit var emailInput: EditText
    lateinit var passwordInput: EditText

    lateinit var loginButton: Button
    lateinit var signupButton: Button
    lateinit var googleButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = layoutInflater.inflate(R.layout.fragment_login, container, false)
        emailInput = v.findViewById(R.id.txt_login_email)
        passwordInput = v.findViewById(R.id.txt_password)
        loginButton = v.findViewById(R.id.btn_back)
        signupButton = v.findViewById(R.id.btn_register)
        googleButton = v.findViewById(R.id.btn_google)

        return v
    }

    override fun onStart() {
        super.onStart()

        // For debug
        emailInput.setText("adminuser@gmail.com")
        passwordInput.setText("adminuser")

        loginButton.setOnClickListener {
            if ((emailInput.text.toString().isEmpty()) or (passwordInput.text.toString()
                    .isEmpty())
            ) {
                Snackbar.make(v, "Email/Password invalid syntax", Snackbar.LENGTH_LONG).show()
            } else {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    emailInput.text.toString(),
                    passwordInput.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        startActivity(Intent(context, MainActivity::class.java))
                    } else {
                        Snackbar.make(
                            v,
                            "There was a problem with user authentication",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        googleButton.setOnClickListener {
            val googleConfg = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
            val googleClient = GoogleSignIn.getClient(requireContext(), googleConfg)
            googleClient.signOut()
            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
        }

        signupButton.setOnClickListener {
            val action = loginFragmentDirections.actionLoginFragment2ToRegisterFragment2()
            v.findNavController().navigate(action)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                startActivity(Intent(context, MainActivity::class.java))
                            } else {
                                Snackbar.make(
                                    v,
                                    "There was a problem with user authentication",
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }
                        }
                }
            } catch (e: ApiException) {
                // no hago nada. entro aca cuando cancelo el registro de usuario por Google
            }
        }
    }
}
