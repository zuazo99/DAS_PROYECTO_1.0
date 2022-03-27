package com.example.proyecto_das.activities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import com.example.proyecto_das.R;

public class NotificationHandler extends ContextWrapper {

    private NotificationManager manager;

    public static final String CHANNEL_HIGH_ID = "1";
    private final String CHANNEL_HIGH_NAME = "HIGH CHANNEL"; //privado no queremos que el nombre salga fuera de la clase --> encapsular
    public static final String CHANNEL_LOW_ID = "2";
    private final String CHANNEL_LOW_NAME = "LOW CHANNEL";

    public NotificationHandler(Context base) {
        super(base);
        createChannels();
    }

    public NotificationManager getManager(){
        if (manager==null){
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    private void createChannels() {

        // metodo para crear los canales
        if (Build.VERSION.SDK_INT >= 26){
            // Compatible con los canales en las notificaciones
            // Creando el high channel
            NotificationChannel highchannel = new NotificationChannel(CHANNEL_HIGH_ID, CHANNEL_HIGH_NAME, NotificationManager.IMPORTANCE_HIGH);

            NotificationChannel lowchannel = new NotificationChannel(CHANNEL_LOW_ID, CHANNEL_LOW_NAME, NotificationManager.IMPORTANCE_LOW);
            /// ... EXTRA CONFIG... ///

            highchannel.enableLights(true);
            highchannel.setLightColor(Color.YELLOW);
            highchannel.setShowBadge(true);
            highchannel.enableVibration(true);

            // Para poner un sonido
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            highchannel.setSound(defaultSoundUri, null);

            //Metodos para los canales
            getManager().createNotificationChannel(highchannel);
            getManager().createNotificationChannel(lowchannel);
        }

    }

    public Notification.Builder createNotification(String title, String message, boolean isHighImportance){
        if (Build.VERSION.SDK_INT >= 26){
            if (isHighImportance){
                return this.createNotificationWithChannel(title, message, CHANNEL_HIGH_ID);
            }
            return this.createNotificationWithChannel(title, message, CHANNEL_LOW_ID);
        }
        return this.createNotificationWithoutChannel(title, message);
    }

    // Estos dos metodos son dependiendo de la version de android que utilizemos
    private Notification.Builder createNotificationWithChannel(String title, String message, String channelId){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            //Versiones >= Oreo
            return new Notification.Builder(getApplicationContext(), channelId)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(android.R.drawable.stat_notify_chat)
                    .setColor(getColor(R.color.orangePrimary))
                    .setAutoCancel(true);
        }
        return null;
    }

    private Notification.Builder createNotificationWithoutChannel(String title, String message){
        return new Notification.Builder(getApplicationContext())
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(android.R.drawable.stat_notify_chat)
                .setAutoCancel(true);
    }
 }
