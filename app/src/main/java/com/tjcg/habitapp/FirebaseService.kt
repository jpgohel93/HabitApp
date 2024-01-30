package com.tjcg.habitapp
import android.util.Log
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("Firebase Message", message.toString())
  //      Toast.makeText(applicationContext, message.toString(), Toast.LENGTH_SHORT).show()
    }
}