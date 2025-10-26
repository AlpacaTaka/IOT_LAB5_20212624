package com.example.lab5_20212624;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class CourseActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getStringExtra("action");
        int courseId = intent.getIntExtra("course_id", -1);
        if (action == null || courseId == -1) return;

        switch (action) {
            case "complete":

                Toast.makeText(context, "Curso marcado como completado", Toast.LENGTH_SHORT).show();
                break;
            case "postpone":

                Toast.makeText(context, "Recordatorio pospuesto", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}

