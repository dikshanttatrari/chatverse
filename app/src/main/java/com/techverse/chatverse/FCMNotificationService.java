package com.techverse.chatverse;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.techverse.chatverse.model.UserModel;
import com.techverse.chatverse.utils.FirebaseUtil;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

import org.json.JSONException;
import org.json.JSONObject;

public class FCMNotificationService extends FirebaseMessagingService {
}
