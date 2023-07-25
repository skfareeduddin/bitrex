package com.example.bitrex.activities


import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.bitrex.R
import com.example.bitrex.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class SignInActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        auth = FirebaseAuth.getInstance()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val signInButton: Button = findViewById(R.id.btn_sign_in)
        signInButton.setOnClickListener {
            signInRegisteredUser()
        }

        setupActionBar()
    }

//    Setting up action bar
    private fun setupActionBar() {
        val toolBar : Toolbar = findViewById(R.id.toolbar_sign_in_activity)

        setSupportActionBar(toolBar)

        val actionBar = supportActionBar

        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        toolBar.setNavigationOnClickListener{
            onBackPressed()
        }
    }

//    When Sign In successful
    fun signInSuccess(user: User) {
        hideProgressDialog()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

//    Signing In the registered user
    private fun signInRegisteredUser() {
        val emailEditText: EditText = findViewById(R.id.et_sign_in_email)
        val email: String = emailEditText.text.toString().trim {it <= ' '}

        val passwordEditText: EditText = findViewById(R.id.et_sign_in_password)
        val password: String = passwordEditText.text.toString().trim {it <= ' '}

        if(validateForm(email, password)) {
            showProgressDialog(resources.getString(R.string.please_wait))

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    hideProgressDialog()
                    if (task.isSuccessful) {
                        // Sign in success, update the signed-in user's information
                        Log.d("Sign in", "createUserWithEmail:success")
                        val user = auth.currentUser
                        startActivity(Intent(this, MainActivity::class.java))
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Sign in", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, task.exception!!.message,
                            Toast.LENGTH_SHORT).show()
                    }
                }


        }

    }

//    Validating form & displaying SnackBar
    private fun validateForm(email: String, password: String): Boolean {
        return when {
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