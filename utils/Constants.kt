package com.example.bitrex.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.core.app.ActivityCompat.startActivityForResult

object Constants {

    const val USERS: String = "users"

    const val BOARDS: String = "boards"

    const val NAME: String = "name"
    const val MOBILE: String = "mobile"
    const val IMAGE: String = "image"
    const val ASSIGNED_TO: String = "assignedTo"
    const val READ_STORAGE_PERMISSION_CODE = 1
    const val PICK_IMAGE_REQUEST_CODE = 2
    const val DOCUMENT_ID: String = "documentId"
    const val TASK_LIST: String = "taskList"
    const val BOARD_DETAIL: String = "boardDetail"
    const val ID: String = "id"
    const val EMAIL: String = "email"
    const val BOARD_MEMBERS_LIST: String = "boarMembersList"
    const val SELECT: String = "select"
    const val UN_SELECT: String = "unselect"
    const val TASK_LIST_ITEM_POSITION: String = "taskListItemPosition"
    const val CARD_LIST_ITEM_POSITION: String = "cardListItemPosition"
    const val BITREX_PREFERENCES: String = "bitrexPreferences"
    const val FCM_TOKEN: String = "fcmToken"
    const val FCM_TOKEN_UPDATED: String = "fcmTokenUpdated"
    const val FCM_AUTHORIZATION: String = "authorization"
    const val FCM_KEY: String = "key"
    const val FCM_KEY_TITLE: String = "title"
    const val FCM_KEY_MESSAGE: String = "message"
    const val FCM_KEY_DATA: String = "data"
    const val FCM_KEY_TO: String = "to"
    const val FCM_BASE_URL: String = "https://fcm.googleapis.com/fcm/send"
    const val FCM_SERVER_KEY: String = "AAAAxzgyB0A:APA91bGcva00p5Sv5qNx487j2r-zdQu_ZoebFRpeSlqPrM10JmW9l-lpPYqVge_f2yBZNTpi0zHuxNsIeDWuqVx3Yyz5btvM3XJsX_8nIKqVAEJ_BZ61hgmyvPXx4EVgRDTus-oMMo0s"

//    Showing image chooser
    fun showImageChooser(activity: Activity) {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

//    Finding the image type
    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}