package com.example.bitrex.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.bitrex.R
import com.example.bitrex.firebase.FirestoreClass
import com.example.bitrex.models.User
import com.example.bitrex.utils.Constants
import com.example.bitrex.utils.Constants.PICK_IMAGE_REQUEST_CODE
import com.example.bitrex.utils.Constants.READ_STORAGE_PERMISSION_CODE
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class MyProfileActivity : BaseActivity() {

    private var mSelectedImageFileUri: Uri? = null
    private lateinit var mUserDetails : User
    private var mProfileImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        setupActionBar()

        FirestoreClass().loadUserData(this)

        val userImage: ImageView = findViewById(R.id.user_profile_image)
        userImage.setOnClickListener {

            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this)
            } else {
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        val updateButton: Button = findViewById(R.id.btn_update)
        updateButton.setOnClickListener {
            if (mSelectedImageFileUri != null) {
                uploadUserImage()
            } else {
                showProgressDialog(resources.getString(R.string.please_wait))

                updateUserProfileData()
            }
        }

    }

//    Taking permission for accessing image
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_STORAGE_PERMISSION_CODE) {
            if(grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this)
            }
        } else {
            Toast.makeText(this,
                "You denied permission for storage. You can also allow it from the settings.",
                Toast.LENGTH_LONG)
                .show()
        }
    }

//    Taking image from the local storage
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val userImage: ImageView = findViewById(R.id.user_profile_image)

        if (resultCode == Activity.RESULT_OK &&
            requestCode == PICK_IMAGE_REQUEST_CODE &&
            data!!.data != null ) {
            mSelectedImageFileUri = data.data

            try {
                Glide
                    .with(this)
                    .load(mSelectedImageFileUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(userImage)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

//    Setting up action bar
    private fun setupActionBar() {
        val toolBar : Toolbar = findViewById(R.id.toolbar_my_profile_activity)
        setSupportActionBar(toolBar)
        val actionBar = supportActionBar

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.my_profile)
        }

        toolBar.setNavigationOnClickListener { onBackPressed() }
    }

//    Setting data to show in UI
    fun setUserDataInUI(user: User) {

        val userImage: ImageView = findViewById(R.id.user_profile_image)
        val name: EditText = findViewById(R.id.et_name)
        val email: EditText = findViewById(R.id.et_email)
        val mobile: EditText = findViewById(R.id.et_mobile)
        mUserDetails = user

        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(userImage)

        name.setText(user.name)
        email.setText(user.email)

        if (user.mobile != 0L) {
            mobile.setText(user.mobile.toString())
        }
    }

//    Uploading the data in the database
    private fun updateUserProfileData() {
        val userHashMap = HashMap<String, Any>()
        val name: EditText = findViewById(R.id.et_name)
        val mobile: EditText = findViewById(R.id.et_mobile)

        if (mProfileImageURL.isNotEmpty() && mProfileImageURL != mUserDetails.image) {
            userHashMap[Constants.IMAGE] = mProfileImageURL
        }

        if (name.text.toString() != mUserDetails.name) {
            userHashMap[Constants.NAME] = name.text.toString()
        }

        if (mobile.text.toString() != mUserDetails.mobile.toString()) {
            userHashMap[Constants.MOBILE] = mobile.text.toString().toLong()
        }

        FirestoreClass().updatingUserProfileData(this, userHashMap)

    }

//    Uploading image on the firebase storage
    private fun uploadUserImage() {
        showProgressDialog(resources.getString(R.string.please_wait))

        if (mSelectedImageFileUri != null) {
            val sRef: StorageReference =
                FirebaseStorage.getInstance()
                    .reference
                    .child("USER_IMAGE" + System.currentTimeMillis() + "." +
                            Constants.getFileExtension(this, mSelectedImageFileUri))

            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                taskSnapshot ->
                Log.i(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )

                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri ->
                    Log.i("Downloadable Image URL", uri.toString())
                    mProfileImageURL = uri.toString()
                    updateUserProfileData()
                }
            }.addOnFailureListener {
                exception ->
                Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()

                hideProgressDialog()
            }
        }
    }



//    When profile update is success
    fun profileUpdateSuccess() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

}