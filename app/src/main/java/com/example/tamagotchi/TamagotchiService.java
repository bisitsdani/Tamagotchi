package com.example.tamagotchi;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;

public class TamagotchiService extends IntentService {
    private boolean notified = false;

    private SharedPreferences prefs;
    private int hungerProg;
    private int gameProg;

    NotificationManager mNotifyManager;

    // Notification channel ID.
    private static final String PRIMARY_CHANNEL_ID =
            "primary_notification_channel";



    public TamagotchiService() {
        super("TamagotchiService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        prefs = getApplicationContext().getSharedPreferences("Tamagotchi", MODE_PRIVATE);
        loadAll();
        sendTamagotchiBroadcast();
    }
    public void loadAll() {
        hungerProg = prefs.getInt("HUNGER", 50);
        gameProg = prefs.getInt("GAME", 50);
    }
    public void saveAll(){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("HUNGER", hungerProg);
        editor.putInt("GAME", gameProg);
        editor.commit();
    }
    public void ehezes(){
        hungerProg = (prefs.getInt("HUNGER", 50) - 5);
        gameProg = (prefs.getInt("GAME", 50) - 4);
        saveAll();
        loadAll();
    }
    public void broadcastSent(){
        if(hungerProg <= 30 || gameProg <= 30){
            if(!notified) {
                notifyUser();
            }
        }
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                ehezes();
                sendTamagotchiBroadcast();
            }
        }, 5000);

    }
    public void notificationTimer(){
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                notified = false;
            }
        }, 15000);
    }
    public void createNotificationChannel() {
        mNotifyManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel
                    (PRIMARY_CHANNEL_ID,
                            "Tamagotchi notification",
                            NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notifications from Tamagotchi");

            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }

    public void notifyUser(){
        createNotificationChannel();

        PendingIntent contentPendingIntent = PendingIntent.getActivity
                (this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder
                (this, PRIMARY_CHANNEL_ID)
                .setContentTitle("Tamagotchi")
                .setContentText("Foglalkozz a szörnyecskéddel!")
                .setContentIntent(contentPendingIntent)
                .setSmallIcon(R.mipmap.launchericon_round)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true);

        mNotifyManager.notify(0, builder.build());
        notified = true;
        notificationTimer();
    }

    private void sendTamagotchiBroadcast() {
        try {
            Intent broadCastIntent = new Intent();
            broadCastIntent.setAction(MainActivity.BROADCAST_ACTION);

            sendBroadcast(broadCastIntent);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        broadcastSent();
    }
}
