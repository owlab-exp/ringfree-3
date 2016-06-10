package com.owlab.callblocker.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.owlab.callblocker.CONS;
import com.owlab.callblocker.MainActivity;
import com.owlab.callblocker.R;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class CallBlockerIntentService extends IntentService {
    public static final String TAG = CallBlockerIntentService.class.getSimpleName();

    private static final String ACTION_BLOCKING_ON = "com.owlab.callblocker.service.action.BLOCKING_ON";
    private static final String ACTION_BLOCKING_OFF = "com.owlab.callblocker.service.action.BLOCKING_OFF";

    private static final String ACTION_STATUSBAR_NOTIFICATION_ON  = "com.owlab.callblocker.service.action.STATUSBAR_NOTIFICATION_ON";
    private static final String ACTION_STATUSBAR_NOTIFICATION_OFF = "com.owlab.callblocker.service.action.STATUSBAR_NOTIFICATION_OFF";

    private static final String ACTION_SUPPRESS_RINGING_ON  = "com.owlab.callblocker.service.action.SUPPRESS_RINGING_ON";
    private static final String ACTION_SUPPRESS_RINGING_OFF = "com.owlab.callblocker.service.action.SUPPRESS_RINGING_OFF";

    private static final String ACTION_SUPPRESS_CALL_NOTIFICATION_ON  = "com.owlab.callblocker.service.action.SUPPRESS_CALL_NOTIFICATION_ON";
    private static final String ACTION_SUPPRESS_CALL_NOTIFICATION_OFF = "com.owlab.callblocker.service.action.SUPPRESS_CALL_NOTIFICATION_OFF";

    private static final String ACTION_DISMISS_CALL_ON  = "com.owlab.callblocker.service.action.DISMISS_CALL_ON";
    private static final String ACTION_DISMISS_CALL_OFF = "com.owlab.callblocker.service.action.DISMISS_CALL_OFF";

    private static final String ACTION_SUPPRESS_HEADS_UP_NOTIFICATION_ON  = "com.owlab.callblocker.service.action.SUPPRESS_HEADS_UP_NOTIFICATION_ON";
    private static final String ACTION_SUPPRESS_HEADS_UP_NOTIFICATION_OFF = "com.owlab.callblocker.service.action.SUPPRESS_HEADS_UP_NOTIFICATION_OFF";

    //private static boolean isStarted = false;
    //private static boolean statusbarNotificationOn = false;

    public CallBlockerIntentService() {
        super(TAG);
    }

    public static void startActionBlockingOn(Context context) {
        Intent intent = new Intent(context, CallBlockerIntentService.class);
        intent.setAction(ACTION_BLOCKING_ON);
        context.startService(intent);
    }

    public static void startActionBlockingOff(Context context) {
        Intent intent = new Intent(context, CallBlockerIntentService.class);
        intent.setAction(ACTION_BLOCKING_OFF);
        context.startService(intent);
    }

    public static void startActionStatusbarNotificationOn(Context context) {
        Intent intent = new Intent(context, CallBlockerIntentService.class);
        intent.setAction(ACTION_STATUSBAR_NOTIFICATION_ON);
        context.startService(intent);
    }

    public static void startActionStatusbarNotificationOff(Context context) {
        Intent intent = new Intent(context, CallBlockerIntentService.class);
        intent.setAction(ACTION_STATUSBAR_NOTIFICATION_OFF);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, ">>>>> handling intent action: " + intent.getAction().toString());

        if (intent == null) return;

        if (intent.getAction().equals(ACTION_BLOCKING_ON)) {
            handleActionBlockingOn();
        } else if (intent.getAction().equals(ACTION_STATUSBAR_NOTIFICATION_ON)) {
            handleActionStatusbarNotificationOn();
        } else if (intent.getAction().equals(ACTION_STATUSBAR_NOTIFICATION_OFF)) {
            handleActionStatusbarNotificationOff();
        } else if (intent.getAction().equals(ACTION_SUPPRESS_RINGING_ON)) {
            handleActionQuietRingerOn();
        } else if (intent.getAction().equals(ACTION_SUPPRESS_RINGING_OFF)) {
            handleActionQuietRingerOff();
        } else if (intent.getAction().equals(ACTION_SUPPRESS_CALL_NOTIFICATION_ON)) {
            handleActionSuppressCallNotificationOn();
        } else if (intent.getAction().equals(ACTION_SUPPRESS_CALL_NOTIFICATION_OFF)) {
            handleActionSuppressCallNotificationOff();
        } else if (intent.getAction().equals(ACTION_DISMISS_CALL_ON)) {
            handleActionDismissCallOn();
        } else if (intent.getAction().equals(ACTION_DISMISS_CALL_OFF)) {
            handleActionDismissCallOff();
        } else if (intent.getAction().equals(ACTION_SUPPRESS_HEADS_UP_NOTIFICATION_ON)) {
            handleActionSuppressHeadsUpNotificationOn();
        } else if (intent.getAction().equals(ACTION_SUPPRESS_HEADS_UP_NOTIFICATION_OFF)) {
            handleActionSuppressHeadsUpNotificationOff();
        } else if (intent.getAction().equals(ACTION_BLOCKING_OFF)) {
            handleActionBlockingOff();
        }
    }

    private void handleActionBlockingOn() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        // show blocking notification
        handleActionStatusbarNotificationOn();

        // register broadcast receiver,
    }

    private void handleActionBlockingOff() {
        handleActionStatusbarNotificationOff();

    }

    private void handleActionStatusbarNotificationOn() {
        //Log.d(TAG, ">>>>> handleActionStatusbarNotificationOn called");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        //If the notification is disabled then return
        if (!sharedPreferences.getBoolean(getString(R.string.settings_key_show_notification_icon), false)) {
            Log.d(TAG, ">>>>> show notification icon disabled");
            return;
        }

        //If the notification is already on then return
        if(sharedPreferences.getBoolean(getString(R.string.status_key_show_notification_icon), false)) {
            return;
        }

        //Otherwise show notification icon
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_call_blocker_48)
                .setContentTitle("CallBlocker")
                .setOngoing(true)
                .setContentIntent(PendingIntent.getActivity(getApplication(), 0, new Intent(getApplication(), MainActivity.class), 0));
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(CONS.STATUSBAR_NOTIFICATION_ID, notificationBuilder.build());

        //Write status
        sharedPreferences.edit().putBoolean(getString(R.string.status_key_show_notification_icon), true).commit();
    }

    private void handleActionStatusbarNotificationOff() {
        //If the notification is not turned on, return
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if(!sharedPreferences.getBoolean(getString(R.string.status_key_show_notification_icon), false)) {
            return;
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(CONS.STATUSBAR_NOTIFICATION_ID);

        //write status
        sharedPreferences.edit().putBoolean(getString(R.string.status_key_show_notification_icon), false).commit();
    }

    private void handleActionQuietRingerOn() {}
    private void handleActionQuietRingerOff() {}
    private void handleActionSuppressCallNotificationOn() {}
    private void handleActionSuppressCallNotificationOff() {}
    private void handleActionDismissCallOn() {}
    private void handleActionDismissCallOff() {}
    private void handleActionSuppressHeadsUpNotificationOn() {}
    private void handleActionSuppressHeadsUpNotificationOff() {}

}