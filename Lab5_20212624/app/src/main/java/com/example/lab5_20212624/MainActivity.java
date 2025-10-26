package com.example.lab5_20212624;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "GestorEstudioPrefs";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_MOTIVATIONAL_MESSAGE = "motivational_message";
    private static final String KEY_PROFILE_IMAGE = "profile_image.jpg";

    private static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_PICK_IMAGE = 2;
    private static final int REQUEST_NOTIFICATION_PERMISSION = 3;

    // Canales de notificación
    public static final String CHANNEL_TEORICOS = "channel_teoricos";
    public static final String CHANNEL_LABORATORIOS = "channel_laboratorios";
    public static final String CHANNEL_ELECTIVOS = "channel_electivos";
    public static final String CHANNEL_OTROS = "channel_otros";
    public static final String CHANNEL_MOTIVACIONAL = "channel_motivacional";

    private TextView tvSaludo;
    private TextView tvMensajeMotivacional;
    private ImageView ivProfileImage;
    private Button btnVerCursos;
    private Button btnConfiguraciones;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initSharedPreferences();
        createNotificationChannels();
        requestNotificationPermission();
        loadUserData();
        setupClickListeners();
    }

    private void initViews() {
        tvSaludo = findViewById(R.id.tv_saludo);
        tvMensajeMotivacional = findViewById(R.id.tv_mensaje_motivacional);
        ivProfileImage = findViewById(R.id.iv_profile_image);
        btnVerCursos = findViewById(R.id.btn_ver_cursos);
        btnConfiguraciones = findViewById(R.id.btn_configuraciones);
    }

    private void initSharedPreferences() {
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (manager == null) return;

            NotificationChannel teoricos = new NotificationChannel(
                    CHANNEL_TEORICOS,
                    "Teóricos",
                    NotificationManager.IMPORTANCE_HIGH
            );
            teoricos.setDescription("Notificaciones de cursos teóricos");
            teoricos.enableVibration(true);
            teoricos.setVibrationPattern(new long[]{0, 300, 200, 300});

            NotificationChannel laboratorios = new NotificationChannel(
                    CHANNEL_LABORATORIOS,
                    "Laboratorios",
                    NotificationManager.IMPORTANCE_HIGH
            );
            laboratorios.setDescription("Notificaciones de laboratorios");
            laboratorios.enableVibration(true);
            laboratorios.setVibrationPattern(new long[]{0, 100, 100, 100, 100, 100});

            NotificationChannel electivos = new NotificationChannel(
                    CHANNEL_ELECTIVOS,
                    "Electivos",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            electivos.setDescription("Notificaciones de cursos electivos");
            electivos.enableVibration(true);
            electivos.setVibrationPattern(new long[]{0, 250, 250, 250});

            NotificationChannel otros = new NotificationChannel(
                    CHANNEL_OTROS,
                    "Otros",
                    NotificationManager.IMPORTANCE_LOW
            );
            otros.setDescription("Notificaciones de otros cursos");
            otros.enableVibration(false);

            NotificationChannel motivacional = new NotificationChannel(
                    CHANNEL_MOTIVACIONAL,
                    "Motivacional",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            motivacional.setDescription("Notificaciones motivacionales");
            motivacional.enableVibration(true);
            motivacional.setVibrationPattern(new long[]{0, 500, 200, 200});

            manager.createNotificationChannel(teoricos);
            manager.createNotificationChannel(laboratorios);
            manager.createNotificationChannel(electivos);
            manager.createNotificationChannel(otros);
            manager.createNotificationChannel(motivacional);
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_NOTIFICATION_PERMISSION);
            }
        }
    }

    private void loadUserData() {

        String userName = sharedPreferences.getString(KEY_USER_NAME, "");
        if (userName.isEmpty()) {
            // Primera vez - establecer nombre por defecto
            userName = "Estudiante";
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_USER_NAME, userName);
            editor.apply();
        }


        tvSaludo.setText("¡Hola, " + userName + "!");


        String motivationalMessage = sharedPreferences.getString(KEY_MOTIVATIONAL_MESSAGE,
                "Hoy es un gran día para aprender");
        tvMensajeMotivacional.setText(motivationalMessage);


        loadProfileImage();
    }

    private void loadProfileImage() {
        try {
            File file = new File(getFilesDir(), KEY_PROFILE_IMAGE);
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                Bitmap bitmap = BitmapFactory.decodeStream(fis);
                ivProfileImage.setImageBitmap(bitmap);
                fis.close();
            } else {
                // Imagen por defecto
                ivProfileImage.setImageResource(R.drawable.ic_default_profile);
            }
        } catch (IOException e) {
            e.printStackTrace();
            ivProfileImage.setImageResource(R.drawable.ic_default_profile);
        }
    }

    private void setupClickListeners() {
        // Click en imagen para cambiarla
        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionAndPickImage();
            }
        });

        // Botón Ver mis cursos
        btnVerCursos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CursosActivity.class);
                startActivity(intent);
            }
        });

        // Botón Configuraciones
        btnConfiguraciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ConfiguracionesActivity.class);
                startActivity(intent);
            }
        });
    }

    private void checkPermissionAndPickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ usa READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
            } else {
                pickImageFromGallery();
            }
        } else {
            // Versiones anteriores usan READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
            } else {
                pickImageFromGallery();
            }
        }
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSION_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGallery();
                } else {
                    Toast.makeText(this, "Permiso denegado para acceder a la galería",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_NOTIFICATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permisos de notificación concedidos",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Las notificaciones están deshabilitadas",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    //se pregunto a claude como implementar carga de imagenes ya que no me acordaba como lo habia hecho en el proyectp
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            try {
                // Cargar la imagen seleccionada
                InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                // Redimensionar la imagen para ahorrar espacio
                Bitmap resizedBitmap = resizeBitmap(bitmap, 300, 300);

                // Guardar en Internal Storage
                saveImageToInternalStorage(resizedBitmap);

                // Mostrar la imagen en el ImageView
                ivProfileImage.setImageBitmap(resizedBitmap);

                inputStream.close();
                Toast.makeText(this, "Imagen guardada exitosamente", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Bitmap resizeBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = ((float) maxWidth) / width;
        float scaleHeight = ((float) maxHeight) / height;
        float scale = Math.min(scaleWidth, scaleHeight);

        int newWidth = Math.round(width * scale);
        int newHeight = Math.round(height * scale);

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

    private void saveImageToInternalStorage(Bitmap bitmap) {
        try {
            FileOutputStream fos = openFileOutput(KEY_PROFILE_IMAGE, MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadUserData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        AlarmScheduler.scheduleAllCourseReminders(this);
        AlarmScheduler.scheduleMotivationalFromPrefs(this);
    }
}