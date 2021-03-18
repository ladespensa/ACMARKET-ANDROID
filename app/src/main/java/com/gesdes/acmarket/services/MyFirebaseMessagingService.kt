package com.gesdes.acmarket.services;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory
import android.media.RingtoneManager;
import android.util.Log

import androidx.core.app.NotificationCompat;
import com.gesdes.acmarket.R

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


class MyFirebaseMessagingService: FirebaseMessagingService() {

    var CHANNEL_ID: String = "chanel_id"
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        CHANNEL_ID = getString(R.string.CHANNEL_ID)
        val bm = BitmapFactory.decodeResource(resources, R.drawable.ladespensa)

        //Log.d("SCHNOTI","title: "+message.notification!!.title);
        //Log.d("SCHNOTI","menssage: "+message.notification!!.body);


        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(message.notification!!.title)
                .setContentText(message.notification!!.body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(NotificationCompat.BigTextStyle())
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(bm)
                .setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(0, notificationBuilder.build())
    }

    private val LOGTAG = "android-fcm"

    fun onTokenRefresh() {
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.d(LOGTAG, "Token actualizado: " + refreshedToken!!)
    }

}