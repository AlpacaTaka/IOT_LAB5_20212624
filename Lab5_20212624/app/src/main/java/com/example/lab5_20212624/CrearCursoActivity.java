package com.example.lab5_20212624;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Calendar;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CrearCursoActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "GestorEstudioPrefs";
    private static final String KEY_CURSOS_LIST = "cursos_list";

    private EditText etNombreCurso;
    private AutoCompleteTextView actCategoriaCurso;
    private NumberPicker npFrecuenciaHoras;
    private NumberPicker npFrecuenciaDias;
    private TextView tvFechaSeleccionada;
    private TextView tvHoraSeleccionada;
    private EditText etAccionSugerida;
    private Button btnSeleccionarFecha;
    private Button btnSeleccionarHora;
    private Button btnGuardarCurso;
    private Button btnCancelar;

    private SharedPreferences sharedPreferences;
    private Gson gson;
    private String fechaSeleccionada = "";
    private String horaSeleccionada = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_curso);


        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Crear Curso");
            }
        }

        initViews();
        initData();
        setupSpinner();
        setupNumberPickers();
        setupClickListeners();
    }

    private void initViews() {
        etNombreCurso = findViewById(R.id.et_nombre_curso);
        actCategoriaCurso = findViewById(R.id.act_categoria_curso);
        npFrecuenciaHoras = findViewById(R.id.np_frecuencia_horas);
        npFrecuenciaDias = findViewById(R.id.np_frecuencia_dias);
        tvFechaSeleccionada = findViewById(R.id.tv_fecha_seleccionada);
        tvHoraSeleccionada = findViewById(R.id.tv_hora_seleccionada);
        etAccionSugerida = findViewById(R.id.et_accion_sugerida);
        btnSeleccionarFecha = findViewById(R.id.btn_seleccionar_fecha);
        btnSeleccionarHora = findViewById(R.id.btn_seleccionar_hora);
        btnGuardarCurso = findViewById(R.id.btn_guardar_curso);
        btnCancelar = findViewById(R.id.btn_cancelar);
    }

    private void initData() {
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    private void setupSpinner() {

        String[] categorias = {"Teórico", "Laboratorio", "Electivo", "Otros"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, categorias);
        actCategoriaCurso.setAdapter(adapter);
        actCategoriaCurso.setKeyListener(null);
    }

    private void setupNumberPickers() {

        npFrecuenciaHoras.setMinValue(0);
        npFrecuenciaHoras.setMaxValue(24);
        npFrecuenciaHoras.setValue(0);
        npFrecuenciaHoras.setWrapSelectorWheel(false);


        npFrecuenciaDias.setMinValue(0);
        npFrecuenciaDias.setMaxValue(30);
        npFrecuenciaDias.setValue(1);
        npFrecuenciaDias.setWrapSelectorWheel(false);
    }

    private void setupClickListeners() {

        btnSeleccionarFecha.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        fechaSeleccionada = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year);
                        tvFechaSeleccionada.setText(fechaSeleccionada);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });


        btnSeleccionarHora.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    this,
                    (view, hourOfDay, minute) -> {
                        horaSeleccionada = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                        tvHoraSeleccionada.setText(horaSeleccionada);
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
            );
            timePickerDialog.show();
        });


        btnGuardarCurso.setOnClickListener(v -> guardarCurso());


        btnCancelar.setOnClickListener(v -> finish());
    }

    private void guardarCurso() {

        String nombre = etNombreCurso.getText().toString().trim();
        if (nombre.isEmpty()) {
            etNombreCurso.setError("El nombre del curso es obligatorio");
            etNombreCurso.requestFocus();
            return;
        }

        String categoriaSeleccionada = actCategoriaCurso.getText().toString().trim();
        if (categoriaSeleccionada.isEmpty()) {
            actCategoriaCurso.setError("Selecciona una categoría");
            actCategoriaCurso.requestFocus();
            return;
        }

        if (fechaSeleccionada.isEmpty()) {
            Toast.makeText(this, "Selecciona una fecha para la próxima sesión", Toast.LENGTH_SHORT).show();
            return;
        }

        if (horaSeleccionada.isEmpty()) {
            Toast.makeText(this, "Selecciona una hora para la próxima sesión", Toast.LENGTH_SHORT).show();
            return;
        }


        String categoria = categoriaSeleccionada;
        int frecuenciaHoras = npFrecuenciaHoras.getValue();
        int frecuenciaDias = npFrecuenciaDias.getValue();
        String accionSugerida = etAccionSugerida.getText().toString().trim();

        Curso nuevoCurso = new Curso(
                nombre,
                categoria,
                frecuenciaHoras,
                frecuenciaDias,
                fechaSeleccionada,
                horaSeleccionada,
                accionSugerida
        );


        String combined = fechaSeleccionada + " " + horaSeleccionada; // dd/MM/yyyy HH:mm
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        try {
            Date d = sdf.parse(combined);
            if (d != null) {
                nuevoCurso.setProximaNotificacionTimestamp(d.getTime());
            } else {
                nuevoCurso.setProximaNotificacionTimestamp(System.currentTimeMillis());
            }
        } catch (ParseException e) {
            e.printStackTrace();
            nuevoCurso.setProximaNotificacionTimestamp(System.currentTimeMillis());
        }


        List<Curso> listaCursos = cargarCursosExistentes();

        listaCursos.add(nuevoCurso);

        guardarListaCursos(listaCursos);


        AlarmScheduler.scheduleCourseReminder(this, nuevoCurso);

        Toast.makeText(this, "Curso guardado exitosamente", Toast.LENGTH_SHORT).show();
        finish();
    }

    private List<Curso> cargarCursosExistentes() {
        String cursosJson = sharedPreferences.getString(KEY_CURSOS_LIST, "");
        if (!cursosJson.isEmpty()) {
            try {
                Type listType = new TypeToken<List<Curso>>(){}.getType();
                List<Curso> lista = gson.fromJson(cursosJson, listType);
                return lista != null ? lista : new ArrayList<>();
            } catch (Exception e) {

                e.printStackTrace();
                return new ArrayList<>();
            }
        }
        return new ArrayList<>();
    }

    private void guardarListaCursos(List<Curso> listaCursos) {
        try {
            String cursosJson = gson.toJson(listaCursos);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_CURSOS_LIST, cursosJson);
            editor.apply();
        } catch (Exception e) {

            e.printStackTrace();
            Toast.makeText(this, "Error al guardar el curso", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    private void limpiarCampos() {
        etNombreCurso.setText("");
        actCategoriaCurso.setText("");
        etAccionSugerida.setText("");
        npFrecuenciaHoras.setValue(0);
        npFrecuenciaDias.setValue(1);
        fechaSeleccionada = "";
        horaSeleccionada = "";
        tvFechaSeleccionada.setText("Fecha no seleccionada");
        tvHoraSeleccionada.setText("Hora no seleccionada");
    }


    @Override
    public void onBackPressed() {
        // Aquí podrías agregar un diálogo de confirmación si hay cambios sin guardar
        super.onBackPressed();
    }
}