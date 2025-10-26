package com.example.lab5_20212624;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AlarmScheduler {

    private static final String PREFS_NAME = "GestorEstudioPrefs";
    private static final String KEY_CURSOS_LIST = "cursos_list";
    private static final int MOTIVATIONAL_ID = 424242;

    public static void scheduleCourseReminder(Context context, Curso curso) {
        if (curso == null || !curso.isNotificacionActiva()) return;

        long triggerMillis = curso.getProximaNotificacionTimestamp();
        long intervalMillis = 0;

        if (curso.getFrecuenciaDias() > 0) {
            intervalMillis = TimeUnit.DAYS.toMillis(curso.getFrecuenciaDias());
        } else if (curso.getFrecuenciaHoras() > 0) {
            intervalMillis = TimeUnit.HOURS.toMillis(curso.getFrecuenciaHoras());
        } else {
            // No frequency -> no alarm
            return;
        }

        long now = System.currentTimeMillis();
        if (triggerMillis <= now) {
            long diff = now - triggerMillis;
            long steps = diff / intervalMillis + 1;
            triggerMillis = triggerMillis + steps * intervalMillis;
        }

        int id = Math.abs(curso.getNombre().hashCode());

        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra(NotificationReceiver.EXTRA_TYPE, "course");
        intent.putExtra(NotificationReceiver.EXTRA_TITLE, curso.getNombre());
        intent.putExtra(NotificationReceiver.EXTRA_TEXT, curso.getAccionSugerida());
        intent.putExtra(NotificationReceiver.EXTRA_CHANNEL, curso.getCanalNotificacion());
        intent.putExtra(NotificationReceiver.EXTRA_ID, id);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0));

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent);

            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, triggerMillis, intervalMillis, pendingIntent);
        } else {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerMillis, intervalMillis, pendingIntent);
        }
    }

    public static void scheduleAllCourseReminders(Context context) {
        Gson gson = new Gson();
        android.content.SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String cursosJson = prefs.getString(KEY_CURSOS_LIST, "");
        if (cursosJson == null || cursosJson.isEmpty()) return;

        Type listType = new TypeToken<List<Curso>>(){}.getType();
        List<Curso> listaCursos = gson.fromJson(cursosJson, listType);
        if (listaCursos == null) listaCursos = new ArrayList<>();

        for (Curso c : listaCursos) {
            scheduleCourseReminder(context, c);
        }
    }


    public static void scheduleMotivationalReminder(Context context, String message, int minutes) {
        if (message == null || message.trim().isEmpty() || minutes <= 0) return;

        // *** CONVERSIÓN: De minutos a milisegundos ***
        long intervalMillis = TimeUnit.MINUTES.toMillis(minutes);
        long triggerMillis = System.currentTimeMillis() + intervalMillis;

        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra(NotificationReceiver.EXTRA_TYPE, "motivational");
        intent.putExtra(NotificationReceiver.EXTRA_TITLE, "Mensaje motivacional");
        intent.putExtra(NotificationReceiver.EXTRA_TEXT, message);
        intent.putExtra(NotificationReceiver.EXTRA_CHANNEL, MainActivity.CHANNEL_MOTIVACIONAL);
        intent.putExtra(NotificationReceiver.EXTRA_ID, MOTIVATIONAL_ID);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, MOTIVATIONAL_ID, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0));

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, triggerMillis, intervalMillis, pendingIntent);
        } else {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerMillis, intervalMillis, pendingIntent);
        }
    }

    public static void scheduleMotivationalFromPrefs(Context context) {
        android.content.SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String message = prefs.getString("motivational_message", null);

        int minutes = prefs.getInt("motivational_minutes", 0);
        if (message != null && !message.isEmpty() && minutes > 0) {
            scheduleMotivationalReminder(context, message, minutes);
        }
    }

    public static void cancelCourseReminder(Context context, Curso curso) {
        int id = Math.abs(curso.getNombre().hashCode());
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0));
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    public static void cancelMotivationalReminder(Context context) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, MOTIVATIONAL_ID, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0));
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}