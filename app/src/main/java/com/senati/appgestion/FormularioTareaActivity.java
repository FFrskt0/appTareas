package com.senati.appgestion;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class FormularioTareaActivity extends AppCompatActivity {

    private EditText edtTitulo;
    private EditText edtDescripcion;
    private EditText edtFechaVencimiento;
    private EditText edtUsuario;

    private Spinner spinnerEstado;

    private Button btnGuardar;
    private Button btnFecha;

    private DatabaseHelper databaseHelper;

    private int idTarea = -1;
    private String fechaCreacion = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_formulario_tarea);

        edtTitulo = findViewById(R.id.edtTitulo);
        edtDescripcion = findViewById(R.id.edtDescripcion);
        edtFechaVencimiento = findViewById(R.id.edtFechaVencimiento);
        edtUsuario = findViewById(R.id.edtUsuario);

        spinnerEstado = findViewById(R.id.spinnerEstado);

        btnGuardar = findViewById(R.id.btnGuardar);
        btnFecha = findViewById(R.id.btnFecha);

        databaseHelper = new DatabaseHelper(this);

        configurarSpinner();

        btnFecha.setOnClickListener(v -> seleccionarFecha());

        if (getIntent().hasExtra("id")) {

            cargarDatosParaEditar();

        } else {

            fechaCreacion = obtenerFechaActual();
        }

        btnGuardar.setOnClickListener(v -> guardarTarea());
    }

    private void configurarSpinner() {

        String[] estados = {
                "Pendiente",
                "En progreso",
                "Completada"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        estados
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerEstado.setAdapter(adapter);
    }

    private void seleccionarFecha() {

        Calendar calendario = Calendar.getInstance();

        DatePickerDialog datePickerDialog =
                new DatePickerDialog(
                        this,
                        (view, year, month, dayOfMonth) -> {

                            String fecha =
                                    String.format(
                                            Locale.getDefault(),
                                            "%02d/%02d/%04d",
                                            dayOfMonth,
                                            month + 1,
                                            year
                                    );

                            edtFechaVencimiento.setText(fecha);

                        },
                        calendario.get(Calendar.YEAR),
                        calendario.get(Calendar.MONTH),
                        calendario.get(Calendar.DAY_OF_MONTH)
                );

        datePickerDialog.show();
    }

    private void guardarTarea() {

        String titulo =
                edtTitulo.getText().toString().trim();

        String descripcion =
                edtDescripcion.getText().toString().trim();

        String fechaVencimiento =
                edtFechaVencimiento.getText().toString().trim();

        String usuario =
                edtUsuario.getText().toString().trim();

        String estado =
                spinnerEstado.getSelectedItem().toString();

        if (titulo.isEmpty()) {

            edtTitulo.setError("Ingrese el título");
            edtTitulo.requestFocus();

            return;
        }

        if (fechaVencimiento.isEmpty()) {

            edtFechaVencimiento.setError(
                    "Seleccione una fecha"
            );

            return;
        }

        if (idTarea == -1) {

            Tarea tarea = new Tarea(
                    titulo,
                    descripcion,
                    estado,
                    fechaVencimiento,
                    fechaCreacion,
                    usuario
            );

            long resultado =
                    databaseHelper.insertarTarea(tarea);

            if (resultado != -1) {

                Toast.makeText(
                        this,
                        "Tarea creada correctamente",
                        Toast.LENGTH_SHORT
                ).show();

                finish();

            } else {

                Toast.makeText(
                        this,
                        "Error al crear la tarea",
                        Toast.LENGTH_SHORT
                ).show();
            }

        } else {

            Tarea tarea = new Tarea(
                    idTarea,
                    titulo,
                    descripcion,
                    estado,
                    fechaVencimiento,
                    fechaCreacion,
                    usuario
            );

            boolean resultado =
                    databaseHelper.actualizarTarea(tarea);

            if (resultado) {

                Toast.makeText(
                        this,
                        "Tarea actualizada correctamente",
                        Toast.LENGTH_SHORT
                ).show();

                finish();

            } else {

                Toast.makeText(
                        this,
                        "Error al actualizar",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    private void cargarDatosParaEditar() {

        idTarea = getIntent().getIntExtra(
                "id",
                -1
        );

        edtTitulo.setText(
                getIntent().getStringExtra("titulo")
        );

        edtDescripcion.setText(
                getIntent().getStringExtra("descripcion")
        );

        edtFechaVencimiento.setText(
                getIntent().getStringExtra("fecha_vencimiento")
        );

        edtUsuario.setText(
                getIntent().getStringExtra("usuario")
        );

        fechaCreacion =
                getIntent().getStringExtra("fecha_creacion");

        String estado =
                getIntent().getStringExtra("estado");

        if (estado != null) {

            if (estado.equals("Pendiente")) {

                spinnerEstado.setSelection(0);

            } else if (estado.equals("En progreso")) {

                spinnerEstado.setSelection(1);

            } else if (estado.equals("Completada")) {

                spinnerEstado.setSelection(2);
            }
        }

        btnGuardar.setText("ACTUALIZAR TAREA");
    }

    private String obtenerFechaActual() {

        SimpleDateFormat formato =
                new SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                );

        return formato.format(
                Calendar.getInstance().getTime()
        );
    }
}