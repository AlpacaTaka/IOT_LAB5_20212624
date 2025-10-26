package com.example.lab5_20212624;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CursosActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "GestorEstudioPrefs";
    private static final String KEY_CURSOS_LIST = "cursos_list";

    private RecyclerView recyclerViewCursos;
    private CursosAdapter cursosAdapter;
    private FloatingActionButton fabAgregarCurso;
    private View tvNoCursos;

    private List<Curso> listaCursos;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cursos);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Mis Cursos");
        }


        initViews();
        initData();
        setupRecyclerView();
        setupClickListeners();


        loadCursos();
    }

    private void initViews() {
        recyclerViewCursos = findViewById(R.id.recycler_view_cursos);
        fabAgregarCurso = findViewById(R.id.fab_agregar_curso);
        tvNoCursos = findViewById(R.id.tv_no_cursos);
    }

    private void initData() {
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        listaCursos = new ArrayList<>();
    }

    private void setupRecyclerView() {
        cursosAdapter = new CursosAdapter(this, listaCursos, new CursosAdapter.OnCursoClickListener() {
            @Override
            public void onCursoClick(Curso curso) {

                Toast.makeText(CursosActivity.this, "Curso: " + curso.getNombre(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCursoDelete(Curso curso, int position) {
                eliminarCurso(curso, position);
            }
        });

        recyclerViewCursos.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCursos.setAdapter(cursosAdapter);
    }

    private void setupClickListeners() {
        fabAgregarCurso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CursosActivity.this, CrearCursoActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadCursos() {
        String cursosJson = sharedPreferences.getString(KEY_CURSOS_LIST, "");

        if (!cursosJson.isEmpty()) {
            Type listType = new TypeToken<List<Curso>>(){}.getType();
            listaCursos = gson.fromJson(cursosJson, listType);

            if (listaCursos == null) {
                listaCursos = new ArrayList<>();
            }
        } else {
            listaCursos = new ArrayList<>();
        }


        updateUI();
    }

    private void updateUI() {
        if (listaCursos.isEmpty()) {
            recyclerViewCursos.setVisibility(View.GONE);
            tvNoCursos.setVisibility(View.VISIBLE);
        } else {
            recyclerViewCursos.setVisibility(View.VISIBLE);
            tvNoCursos.setVisibility(View.GONE);
            cursosAdapter.updateList(listaCursos);
        }
    }

    private void eliminarCurso(Curso curso, int position) {

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Eliminar Curso")
                .setMessage("¿Estás seguro de que deseas eliminar el curso '" + curso.getNombre() + "'?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    // Eliminar de la lista
                    listaCursos.remove(position);


                    saveCursos();


                    cursosAdapter.notifyItemRemoved(position);
                    updateUI();

                    Toast.makeText(this, "Curso eliminado", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void saveCursos() {
        String cursosJson = gson.toJson(listaCursos);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_CURSOS_LIST, cursosJson);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadCursos();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}