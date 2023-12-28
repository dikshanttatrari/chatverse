package com.techverse.chatverse;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.techverse.chatverse.model.UserModel;
import com.techverse.chatverse.utils.FirebaseUtil;


import org.json.JSONException;
import org.json.JSONObject;

public class FCMNotificationService extends FirebaseMessagingService {
}
