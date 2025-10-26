package com.example.lab5_20212624;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

public class MotivationalNotificationReceiver extends BroadcastReceiver {

    private static final String PREFS_NAME = "GestorEstudioPrefs";
    private static final String KEY_MOTIVATIONAL_MESSAGE = "motivational_message";
    private static final int MOTIVATIONAL_NOTIFICATION_ID = 1001;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Obtener mensaje motivacional desde SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String message = prefs.getString(KEY_MOTIVATIONAL_MESSAGE, "¡Sigue adelante con tus estudios!");

        // Mostrar notificación
        showMotivationalNotification(context, message);

        // Reprogramar la siguiente notificación
        // *** CAMBIO: Ahora leemos minutos en lugar de horas ***
        int minutosRepeticion = prefs.getInt("motivational_minutes", 30); // Default: 30 minutos
        scheduleMotivationalNotifications(context, minutosRepeticion);
    }

    private void showMotivationalNotification(Context context, String message) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Intent para abrir la app al tocar la notificación
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Construir la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "MOTIVACIONAL")
                .setSmallIcon(R.drawable.ic_notification_motivational)
                .setContentTitle("💪 Mensaje Motivacional")
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVibrate(new long[]{0, 300, 200, 300});

        // Mostrar la notificación
        notificationManager.notify(MOTIVATIONAL_NOTIFICATION_ID, builder.build());
    }

    // *** CAMBIO: Ahora recibe minutos en lugar de horas ***
    public static void scheduleMotivationalNotifications(Context context, int minutosRepeticion) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, MotivationalNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                MOTIVATIONAL_NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Cancelar alarmas anteriores
        alarmManager.cancel(pendingIntent);

        // *** CONVERSIÓN: Calcular el tiempo para la próxima notificación en minutos ***
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, minutosRepeticion); // Cambio de HOUR_OF_DAY a MINUTE

        // Programar la nueva alarma
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );
        } else {
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );
        }
    }

    public static void cancelMotivationalNotifications(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, MotivationalNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                MOTIVATIONAL_NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.cancel(pendingIntent);
    }
}