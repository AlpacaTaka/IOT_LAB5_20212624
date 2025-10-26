package com.example.lab5_20212624;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class ConfiguracionesActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "GestorEstudioPrefs";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_MOTIVATIONAL_MESSAGE = "motivational_message";
    private static final String KEY_MOTIVATIONAL_HOURS = "motivational_hours";

    private TextInputEditText etUserName;
    private TextInputEditText etMotivationalMessage;
    private SeekBar sbMotivationalHours;
    private TextView tvHoursLabel;
    private Button btnSave;
    private Button btnBack;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuraciones);

        initViews();
        initSharedPreferences();
        loadCurrentSettings();
        setupClickListeners();
        setupSeekBarListener();
    }

    private void initViews() {
        etUserName = findViewById(R.id.et_user_name);
        etMotivationalMessage = findViewById(R.id.et_motivational_message);
        sbMotivationalHours = findViewById(R.id.sb_motivational_hours);
        tvHoursLabel = findViewById(R.id.tv_hours_label);
        btnSave = findViewById(R.id.btn_save);
        btnBack = findViewById(R.id.btn_back);
    }

    private void initSharedPreferences() {
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    private void loadCurrentSettings() {
        String currentName = sharedPreferences.getString(KEY_USER_NAME, "Estudiante");
        etUserName.setText(currentName);

        String currentMessage = sharedPreferences.getString(KEY_MOTIVATIONAL_MESSAGE,
                "Hoy es un gran día para aprender");
        etMotivationalMessage.setText(currentMessage);


        int currentHours = sharedPreferences.getInt(KEY_MOTIVATIONAL_HOURS, 4);
        sbMotivationalHours.setProgress(currentHours - 1); // Ajustar para SeekBar (0-based)
        updateHoursLabel(currentHours);
    }

    private void setupSeekBarListener() {
        sbMotivationalHours.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                int hours = progress + 1;
                updateHoursLabel(hours);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void updateHoursLabel(int hours) {
        String label = "Cada " + hours + (hours == 1 ? " hora" : " horas");
        tvHoursLabel.setText(label);
    }

    private void setupClickListeners() {
        // Botón Guardar
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });

        // Botón Volver
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void saveSettings() {

        String nuevoNombre = etUserName.getText().toString().trim();
        String nuevoMensaje = etMotivationalMessage.getText().toString().trim();
        int nuevasHoras = sbMotivationalHours.getProgress() + 1; // Convertir de 0-based a 1-based

        // Validaciones
        if (nuevoNombre.isEmpty()) {
            etUserName.setError("El nombre no puede estar vacío");
            etUserName.requestFocus();
            return;
        }

        if (nuevoMensaje.isEmpty()) {
            etMotivationalMessage.setError("El mensaje motivacional no puede estar vacío");
            etMotivationalMessage.requestFocus();
            return;
        }


        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_NAME, nuevoNombre);
        editor.putString(KEY_MOTIVATIONAL_MESSAGE, nuevoMensaje);
        editor.putInt(KEY_MOTIVATIONAL_HOURS, nuevasHoras);

        boolean success = editor.commit();

        if (success) {
            Toast.makeText(this, "Configuraciones guardadas exitosamente", Toast.LENGTH_SHORT).show();


            finish();
        } else {
            Toast.makeText(this, "Error al guardar configuraciones", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}