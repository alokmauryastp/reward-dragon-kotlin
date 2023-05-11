package aara.technologies.rewarddragon.firebase;

import static aara.technologies.rewarddragon.firebase.NotificationUtils.isAppIsInBackground;
import static aara.technologies.rewarddragon.utils.SharedPrefManager.FIREBASE_TOKEN;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import aara.technologies.rewarddragon.Dashboard;
import aara.technologies.rewarddragon.R;
import aara.technologies.rewarddragon.activities.MyCampaigns;
import aara.technologies.rewarddragon.activities.MyGameTime;
import aara.technologies.rewarddragon.activities.MyJoshForToday;
import aara.technologies.rewarddragon.activities.MyLatestChallenge;
import aara.technologies.rewarddragon.activities.MyWellBeing;
import aara.technologies.rewarddragon.activities.SplashActivity;
import aara.technologies.rewarddragon.manager.MyResources;
import aara.technologies.rewarddragon.utils.Constant;
import aara.technologies.rewarddragon.utils.SharedPrefManager;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private NotificationUtils notificationUtils;
    private boolean mSilent;


    public static String getToken(Context context) {
        Log.i(TAG, "getToken: " + new SharedPrefManager(context).getString(FIREBASE_TOKEN));
        return new SharedPrefManager(context).getString(FIREBASE_TOKEN);
        //context.getSharedPreferences("_", MODE_PRIVATE).getString("fb", "empty");
    }

    @Override
    public void handleIntent(Intent intent) {
        super.handleIntent(intent);
        Log.i(TAG, "handleIntent: ");

        if (isAppIsInBackground(getApplicationContext())) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                for (String key : bundle.keySet()) {
                    if (key.equals("click_action")) {
                        Object value = bundle.get(key);
                        Dashboard.clickAction = (String) value;
                    }
                }
            }
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From:  " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
           // handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        PendingIntent pendingIntent = null;
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), json);

            } catch (Exception e) {
                //Log.e(TAG, "Exception: " + e.getMessage());
                sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), null);

            }
        } else {
            Log.i(TAG, "onMessageReceived: no payload");
            sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), null);

        }

        //   Log.i(TAG, "onMessageReceived: "+intent.getAction());


    }

    public @NonNull
    MyFirebaseMessagingService setNotificationSilent() {
        mSilent = true;
        return this;
    }

    void sendNotification(String title, String body, PendingIntent pendingIntent) {
        if (pendingIntent == null) {

            Intent intent = new Intent(getBaseContext(), SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
        }
        String channelId = "Default";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true).setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle());

        if (!NotificationUtils.isAppIsInBackground(this)) {
            builder.setSound(null)
                    .setNotificationSilent();
        }
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Default channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            if (!NotificationUtils.isAppIsInBackground(this)) {
                Log.i(TAG, "sendNotification: app in background");
                channel.setSound(null, null);
            }
            manager.createNotificationChannel(channel);
        }
        manager.notify(0, builder.build());


    }

    private void handleNotification(String message) {
        if (!isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Constant.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        } else {
            // If the app is in background, firebase itself handles the notification
        }
    }

    private void handleDataMessage(String title, String body, JSONObject json) {

        Log.e(TAG, "push json: " + json.toString());
        Intent intent = new Intent();
        PendingIntent pendingIntent = null;
        try {
            Log.i(TAG, "handleDataMessage: try " + "running");
            String status = json.getString("status");
            String id = json.getString("id");
            String click_action = json.getString("click_action");

            Log.i(TAG, "isAppIsInBackground: " + isAppIsInBackground(getBaseContext()));
            if (!isAppIsInBackground(getBaseContext())) {
                switch (click_action) {
               /* case "my_leaderboard":

                    break;*/

                    case "my_josh":
                        intent = new Intent(getBaseContext(), MyJoshForToday.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
                        break;

                    case "my_wellbeing":
                        intent = new Intent(getBaseContext(), MyWellBeing.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

                        break;

                    case "game_for_today":
                        intent = new Intent(getBaseContext(), MyGameTime.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

                        break;

                    case "my_challenges":
                    case "new_challenge":
                    case "my_challenge":
                        // MyLatestChallenge
                        intent = new Intent(getBaseContext(), MyLatestChallenge.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

                        break;

                    case "my_resources":
                        Log.i(TAG, "handleDataMessage: running");
                        intent = new Intent(getBaseContext(), MyResources.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

                        break;
                    case "new_campaign":
                        intent = new Intent(getBaseContext(), MyCampaigns.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

                        break;

                    default:
                        intent = new Intent(getBaseContext(), SplashActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
                        break;
                }
            } else {
                intent = new Intent(getBaseContext(), SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
            }


        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        sendNotification(title, body, pendingIntent);

    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("newToken", s);
        new SharedPrefManager(this).setString(FIREBASE_TOKEN, s);
    }

}