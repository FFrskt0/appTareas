package com.senati.appgestion;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerTareas;
    private FloatingActionButton btnNuevaTarea;
    private Spinner spinnerFiltro;

    private DatabaseHelper databaseHelper;

    private ArrayList<Tarea> listaTareas;
    private TareaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerTareas = findViewById(R.id.recyclerTareas);
        btnNuevaTarea = findViewById(R.id.btnNuevaTarea);
        spinnerFiltro = findViewById(R.id.spinnerFiltro);

        databaseHelper = new DatabaseHelper(this);

        recyclerTareas.setLayoutManager(
                new LinearLayoutManager(this)
        );

        configurarFiltro();

        btnNuevaTarea.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    FormularioTareaActivity.class
            );

            startActivity(intent);
        });
    }

    private void configurarFiltro() {

        String[] filtros = {
                "Todas",
                "Pendiente",
                "En progreso",
                "Completada"
        };

        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        filtros
                );

        spinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerFiltro.setAdapter(spinnerAdapter);

        spinnerFiltro.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        cargarTareas();
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent) {
                    }
                }
        );
    }

    private void cargarTareas() {

        String filtro = spinnerFiltro.getSelectedItem().toString();

        if (filtro.equals("Todas")) {

            listaTareas = databaseHelper.obtenerTareas();

        } else {

            listaTareas =
                    databaseHelper.obtenerTareasPorEstado(filtro);
        }

        adapter = new TareaAdapter(this, listaTareas);

        recyclerTareas.setAdapter(adapter);
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (databaseHelper != null) {
            cargarTareas();
        }
    }
}