package com.senati.appgestion;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerTareas;

    private FloatingActionButton btnNuevaTarea;

    private Spinner spinnerFiltro;
    private Spinner spinnerOrden;

    private EditText etBuscar;

    private DatabaseHelper databaseHelper;

    private ArrayList<Tarea> listaTareas;

    private TareaAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        // ==========================
        // REFERENCIAS
        // ==========================

        recyclerTareas =
                findViewById(R.id.recyclerTareas);

        btnNuevaTarea =
                findViewById(R.id.btnNuevaTarea);

        spinnerFiltro =
                findViewById(R.id.spinnerFiltro);

        spinnerOrden =
                findViewById(R.id.spinnerOrden);

        etBuscar =
                findViewById(R.id.etBuscar);


        // ==========================
        // BASE DE DATOS
        // ==========================

        databaseHelper =
                new DatabaseHelper(this);


        // ==========================
        // RECYCLERVIEW
        // ==========================

        recyclerTareas.setLayoutManager(
                new LinearLayoutManager(this)
        );


        // ==========================
        // CONFIGURACIONES
        // ==========================

        configurarOrden();

        configurarFiltro();

        configurarBusqueda();


        // ==========================
        // NUEVA TAREA
        // ==========================

        btnNuevaTarea.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MainActivity.this,
                            FormularioTareaActivity.class
                    );

            startActivity(intent);
        });
    }


    // ==================================================
    // FILTRO POR ESTADO
    // ==================================================

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


        spinnerFiltro.setAdapter(
                spinnerAdapter
        );


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


    // ==================================================
    // ORDENAMIENTO
    // ==================================================

    private void configurarOrden() {

        String[] ordenes = {

                "Fecha de vencimiento",

                "Fecha de creación",

                "Título A-Z",

                "Título Z-A"
        };


        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        ordenes
                );


        spinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );


        spinnerOrden.setAdapter(
                spinnerAdapter
        );


        spinnerOrden.setOnItemSelectedListener(
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


    // ==================================================
    // BÚSQUEDA
    // ==================================================

    private void configurarBusqueda() {

        etBuscar.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after) {
                    }


                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count) {

                        cargarTareas();
                    }


                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                }
        );
    }


    // ==================================================
    // CARGAR TAREAS
    // ==================================================

    private void cargarTareas() {

        if (spinnerFiltro.getSelectedItem() == null) {
            return;
        }


        String filtro =
                spinnerFiltro
                        .getSelectedItem()
                        .toString();


        String textoBusqueda =
                etBuscar
                        .getText()
                        .toString()
                        .trim()
                        .toLowerCase();


        // ==========================
        // OBTENER DE SQLITE
        // ==========================

        if (filtro.equals("Todas")) {

            listaTareas =
                    databaseHelper.obtenerTareas();

        } else {

            listaTareas =
                    databaseHelper
                            .obtenerTareasPorEstado(
                                    filtro
                            );
        }


        // ==========================
        // BÚSQUEDA
        // ==========================

        if (!textoBusqueda.isEmpty()) {

            ArrayList<Tarea>
                    tareasFiltradas =
                    new ArrayList<>();


            for (Tarea tarea : listaTareas) {

                String titulo =
                        tarea.getTitulo() == null
                                ? ""
                                : tarea.getTitulo()
                                .toLowerCase();


                String descripcion =
                        tarea.getDescripcion() == null
                                ? ""
                                : tarea.getDescripcion()
                                .toLowerCase();


                if (titulo.contains(
                        textoBusqueda)
                        ||
                        descripcion.contains(
                                textoBusqueda)) {

                    tareasFiltradas.add(
                            tarea
                    );
                }
            }


            listaTareas =
                    tareasFiltradas;
        }


        // ==========================
        // ORDENAR
        // ==========================

        ordenarTareas();


        // ==========================
        // MOSTRAR
        // ==========================

        adapter =
                new TareaAdapter(
                        this,
                        listaTareas
                );


        recyclerTareas.setAdapter(
                adapter
        );
    }


    // ==================================================
    // ORDENAR TAREAS
    // ==================================================

    private void ordenarTareas() {

        if (listaTareas == null
                || listaTareas.size() <= 1) {

            return;
        }

        // Evita errores si el Spinner todavía no tiene
        // un elemento seleccionado
        if (spinnerOrden == null
                || spinnerOrden.getSelectedItem() == null) {

            return;
        }

        String orden =
                spinnerOrden
                        .getSelectedItem()
                        .toString();


        // ==========================
        // TÍTULO A-Z
        // ==========================

        if (orden.equals("Título A-Z")) {

            Collections.sort(
                    listaTareas,
                    new Comparator<Tarea>() {

                        @Override
                        public int compare(
                                Tarea t1,
                                Tarea t2) {

                            return t1
                                    .getTitulo()
                                    .compareToIgnoreCase(
                                            t2.getTitulo()
                                    );
                        }
                    }
            );
        }


        // ==========================
        // TÍTULO Z-A
        // ==========================

        else if (
                orden.equals("Título Z-A")) {

            Collections.sort(
                    listaTareas,
                    new Comparator<Tarea>() {

                        @Override
                        public int compare(
                                Tarea t1,
                                Tarea t2) {

                            return t2
                                    .getTitulo()
                                    .compareToIgnoreCase(
                                            t1.getTitulo()
                                    );
                        }
                    }
            );
        }


        // ==========================
        // FECHA VENCIMIENTO
        // ==========================

        else if (
                orden.equals(
                        "Fecha de vencimiento")) {

            Collections.sort(
                    listaTareas,
                    new Comparator<Tarea>() {

                        @Override
                        public int compare(
                                Tarea t1,
                                Tarea t2) {

                            Date fecha1 =
                                    convertirFecha(
                                            t1.getFechaVencimiento()
                                    );

                            Date fecha2 =
                                    convertirFecha(
                                            t2.getFechaVencimiento()
                                    );

                            return compararFechas(
                                    fecha1,
                                    fecha2
                            );
                        }
                    }
            );
        }


        // ==========================
        // FECHA CREACIÓN
        // ==========================

        else if (
                orden.equals(
                        "Fecha de creación")) {

            Collections.sort(
                    listaTareas,
                    new Comparator<Tarea>() {

                        @Override
                        public int compare(
                                Tarea t1,
                                Tarea t2) {

                            Date fecha1 =
                                    convertirFecha(
                                            t1.getFechaCreacion()
                                    );

                            Date fecha2 =
                                    convertirFecha(
                                            t2.getFechaCreacion()
                                    );

                            return compararFechas(
                                    fecha1,
                                    fecha2
                            );
                        }
                    }
            );
        }
    }


    // ==================================================
    // CONVERTIR FECHA
    // ==================================================

    private Date convertirFecha(
            String fecha) {

        if (fecha == null
                || fecha.isEmpty()) {

            return null;
        }


        String[] formatos = {

                "dd/MM/yyyy",

                "dd-MM-yyyy",

                "yyyy-MM-dd"
        };


        for (String formato : formatos) {

            try {

                SimpleDateFormat sdf =
                        new SimpleDateFormat(
                                formato,
                                Locale.getDefault()
                        );


                sdf.setLenient(false);


                return sdf.parse(fecha);

            } catch (ParseException e) {

                // Intentar siguiente formato
            }
        }


        return null;
    }


    // ==================================================
    // COMPARAR FECHAS
    // ==================================================

    private int compararFechas(
            Date fecha1,
            Date fecha2) {

        if (fecha1 == null
                && fecha2 == null) {

            return 0;
        }


        if (fecha1 == null) {

            return 1;
        }


        if (fecha2 == null) {

            return -1;
        }


        return fecha1.compareTo(
                fecha2
        );
    }


    // ==================================================
    // RECARGAR AL VOLVER
    // ==================================================

    @Override
    protected void onResume() {
        super.onResume();
    }
}