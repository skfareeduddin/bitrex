package com.example.bitrex.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.bitrex.R
import com.example.bitrex.firebase.FirestoreClass
import com.example.bitrex.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignUpActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        setupActionBar()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

//    When signup successful
    fun userRegisteredSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this,
            "You have successfully registered",
            Toast.LENGTH_LONG
        ).show()
        FirebaseAuth.getInstance().signOut()
        finish()
    }

//    Setting up action bar
    private fun setupActionBar() {

        val toolBar : Toolbar = findViewById(R.id.toolbar_sign_up_activity)

        setSupportActionBar(toolBar)

        val actionBar = supportActionBar

        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        toolBar.setNavigationOnClickListener{
            onBackPressed()
        }

        val signUpButton: Button = findViewById(R.id.btn_sign_up)
        signUpButton.setOnClickListener {
            registerUser()
        }
    }

//    Registering the user in Firebase
    private fun registerUser() {
        val nameEditText: EditText = findViewById(R.id.et_sign_up_name)
        val name: String = nameEditText.text.toString().trim {it <= ' '}

        val emailEditText: EditText = findViewById(R.id.et_sign_up_email)
        val email: String = emailEditText.text.toString().trim {it <= ' '}

        val passwordEditText: EditText = findViewById(R.id.et_sign_up_password)
        val password: String = passwordEditText.text.toString().trim {it <= ' '}

        if(validateForm(name, email, password)) {
            showProgressDialog(resources.getString(R.string.please_wait))

            FirebaseAuth
                .getInstance()
                .createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    hideProgressDialog()
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        val registeredEmail = firebaseUser.email!!
                        val user = User(firebaseUser.uid, name, registeredEmail)

                        FirestoreClass().registerUser(this, user)
                    } else {
                        Toast.makeText(this, task.exception!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

//    Validating form & showing SnackBar
    private fun validateForm(name: String, email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar("Please enter your name")
                false
            }

            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter your email address")
                false
            }

            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter your password")
                false
            } else -> {
                true
            }
        }
    }

}