package com.example.supersoiree

import User
import android.content.Intent
import android.nfc.Tag
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.supersoiree.databinding.ActivityAuthBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlin.math.sign

class AuthActivity : AppCompatActivity() {
    companion object{
        private const val TAG = "Authentication activity"
    }

    private lateinit var auth: FirebaseAuth
    //private lateinit var database: Firebase.firestore


    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.emailSignInButton.setOnClickListener(){
            signIn(binding.fieldEmail.text.toString(),binding.fieldPassword.text.toString())
        }
        binding.emailCreateAccountButton.setOnClickListener(){
            createAccount(binding.fieldEmail.text.toString(),binding.fieldPassword.text.toString())
        }









    }



    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_auth)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
    private fun createAccount(email: String, password: String) {
        Log.d(Authentication.TAG, "createAccount:$email")
        if (!validateForm()) {
            return
        }



        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(Authentication.TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    if (user != null) {
                        writeNewUser(user.uid,email,password)
                        signIn(email,password)
                    }

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(Authentication.TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        this,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }


            }
    }
    private fun signIn(email: String, password: String) {
        Log.d(Authentication.TAG, "signIn:$email")
        if (!validateForm()) {
            return
        }



        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(Authentication.TAG, "signInWithEmail:success")

                    val intent = Intent(this,MainActivity::class.java).putExtra("user",auth.currentUser?.uid)
                    startActivity(intent)
                    finish()
                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(Authentication.TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        this,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()

                }



            }
    }


    private fun validateForm(): Boolean {
        var valid = true

        val email = binding.fieldEmail.text.toString()
        if (TextUtils.isEmpty(email)) {
            binding.fieldEmail.error = "Required."
            valid = false
        } else {
            binding.fieldEmail.error = null
        }

        val password = binding.fieldPassword.text.toString()
        if (TextUtils.isEmpty(password)) {
            binding.fieldPassword.error = "Required."
            valid = false
        } else {
            binding.fieldPassword.error = null
        }

        return valid
    }

    fun writeNewUser(userId: String, name: String, password: String) {
        val user = User(name, password)
        val database = Firebase.firestore
        database.collection("users")
            //.add(user)
            .document(userId).set(user)
            .addOnSuccessListener {
                Log.d(TAG, "Document successfully created")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }


    }
}