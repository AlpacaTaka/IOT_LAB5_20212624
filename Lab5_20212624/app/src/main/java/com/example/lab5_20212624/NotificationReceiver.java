package com.example.lab5_20212624;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {

    // Constantes para los extras del Intent
    public static final String EXTRA_TYPE = "type";
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_TEXT = "text";
    public static final String EXTRA_CHANNEL = "channel";
    public static final String EXTRA_ID = "id";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Obtener los datos del intent
        String type = intent.getStringExtra(EXTRA_TYPE);
        String title = intent.getStringExtra(EXTRA_TITLE);
        String text = intent.getStringExtra(EXTRA_TEXT);
        String channel = intent.getStringExtra(EXTRA_CHANNEL);
        int id = intent.getIntExtra(EXTRA_ID, 0);

        // Verificar que tenemos los datos necesarios
        if (title == null || text == null || channel == null) {
            return;
        }

        // Mostrar la notificación
        showNotification(context, title, text, channel, id, type);

        // Si es una notificación de curso, reprogramar la siguiente
        if ("course".equals(type)) {
            // Aquí podrías reprogramar la siguiente notificación si es necesario
            // Por ahora el AlarmScheduler maneja esto automáticamente
        }
    }

    private void showNotification(Context context, String title, String text, String channel, int id, String type) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager == null) return;

        // Intent para abrir la app al tocar la notificación
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Agregar extras según el tipo
        if ("course".equals(type)) {
            intent.putExtra("action", "open_courses");
        } else if ("motivational".equals(type)) {
            intent.putExtra("action", "motivation");
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT |
                        (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );

        // Seleccionar icono según el tipo
        int iconId = getIconForType(type, channel);

        // Construir la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channel)
                .setSmallIcon(iconId)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(getPriorityForChannel(channel));

        // Configurar vibración según el canal
        configureVibrationForChannel(builder, channel);

        // Agregar acciones según el tipo
        if ("course".equals(type)) {
            addCourseActions(context, builder, id);
        }

        // Mostrar la notificación
        notificationManager.notify(id, builder.build());
    }

    private int getIconForType(String type, String channel) {
        if ("motivational".equals(type)) {
            return R.drawable.ic_notification_motivational;
        }

        // Para cursos, seleccionar según el canal
        switch (channel) {
            case "TEORICOS":
                return R.drawable.ic_add;
            case "LABORATORIOS":
                return R.drawable.ic_add;
            case "ELECTIVOS":
                return R.drawable.ic_add;
            case "OTROS":
            default:
                return R.drawable.ic_add;
        }
    }

    private int getPriorityForChannel(String channel) {
        switch (channel) {
            case "TEORICOS":
            case "LABORATORIOS":
                return NotificationCompat.PRIORITY_HIGH;
            case "ELECTIVOS":
                return NotificationCompat.PRIORITY_DEFAULT;
            case "MOTIVACIONAL":
                return NotificationCompat.PRIORITY_DEFAULT;
            case "OTROS":
            default:
                return NotificationCompat.PRIORITY_LOW;
        }
    }

    private void configureVibrationForChannel(NotificationCompat.Builder builder, String channel) {
        switch (channel) {
            case "TEORICOS":
                builder.setVibrate(new long[]{0, 300, 200, 300});
                break;
            case "LABORATORIOS":
                builder.setVibrate(new long[]{0, 100, 100, 100, 100, 100});
                break;
            case "ELECTIVOS":
                builder.setVibrate(new long[]{0, 250, 250, 250});
                break;
            case "MOTIVACIONAL":
                builder.setVibrate(new long[]{0, 500, 200, 200});
                break;
            case "OTROS":
            default:
                // Sin vibración para "Otros"
                break;
        }
    }

    private void addCourseActions(Context context, NotificationCompat.Builder builder, int courseId) {
        // Acción: Marcar como completado
        Intent completeIntent = new Intent(context, CourseActionReceiver.class);
        completeIntent.putExtra("action", "complete");
        completeIntent.putExtra("course_id", courseId);

        PendingIntent completePendingIntent = PendingIntent.getBroadcast(
                context,
                courseId + 1000,
                completeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT |
                        (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );

        builder.addAction(
                R.drawable.ic_check,
                "Completar",
                completePendingIntent
        );

        // Acción: Posponer
        Intent postponeIntent = new Intent(context, CourseActionReceiver.class);
        postponeIntent.putExtra("action", "postpone");
        postponeIntent.putExtra("course_id", courseId);

        PendingIntent postponePendingIntent = PendingIntent.getBroadcast(
                context,
                courseId + 2000,
                postponeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT |
                        (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );

        builder.addAction(
                R.drawable.ic_schedule,
                "Posponer",
                postponePendingIntent
        );
    }
}