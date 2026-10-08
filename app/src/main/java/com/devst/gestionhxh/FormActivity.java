package com.devst.gestionhxh;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

// INTENT EXPLÍCITO 3 (parte 1): formulario que envía datos y espera una respuesta
public class FormActivity extends AppCompatActivity {

    Spinner spCultivo;
    EditText etCantidad, etFecha, etPh, etNotas;
    Button btnRevisar, btnVolver;

    // Recibe la respuesta de ConfirmActivity
    ActivityResultLauncher<Intent> confirmarLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult resultado) {
                    if (resultado.getResultCode() == RESULT_OK && resultado.getData() != null) {
                        // El usuario confirmó: guardamos la siembra
                        String codigo = resultado.getData().getStringExtra("codigo");
                        guardarSiembra(codigo);
                        Toast.makeText(FormActivity.this,
                                getString(R.string.aviso_siembra_guardada, codigo), Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        // El usuario quiere corregir algo
                        Toast.makeText(FormActivity.this, R.string.aviso_corregir, Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_form);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spCultivo = findViewById(R.id.spCultivo);
        etCantidad = findViewById(R.id.etCantidad);
        etFecha = findViewById(R.id.etFecha);
        etPh = findViewById(R.id.etPh);
        etNotas = findViewById(R.id.etNotas);
        btnRevisar = findViewById(R.id.btnRevisar);
        btnVolver = findViewById(R.id.btnVolver);

        // Llenamos el Spinner con los nombres de los cultivos
        String[] opciones = new String[Cultivo.lista.length + 1];
        opciones[0] = getString(R.string.spinner_selecciona);
        for (int i = 0; i < Cultivo.lista.length; i++) {
            opciones[i + 1] = getString(Cultivo.lista[i].emoji) + " " + getString(Cultivo.lista[i].nombre);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, opciones);
        spCultivo.setAdapter(adapter);

        etFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                elegirFecha();
            }
        });

        btnRevisar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                revisar();
            }
        });

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    // Abre un calendario para elegir la fecha
    private void elegirFecha() {
        Calendar hoy = Calendar.getInstance();
        int anio = hoy.get(Calendar.YEAR);
        int mes = hoy.get(Calendar.MONTH);
        int dia = hoy.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog calendario = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int anioElegido, int mesElegido, int diaElegido) {
                // Los meses parten en 0, por eso sumamos 1
                etFecha.setText(getString(R.string.formato_fecha, diaElegido, mesElegido + 1, anioElegido));
                etFecha.setError(null);
            }
        }, anio, mes, dia);

        // Validación: no se puede elegir una fecha futura
        calendario.getDatePicker().setMaxDate(System.currentTimeMillis());
        calendario.show();
    }

    private void revisar() {
        String cantidadTexto = etCantidad.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();
        String phTexto = etPh.getText().toString().trim().replace(",", ".");
        String notas = etNotas.getText().toString().trim();

        // ----- Validaciones -----
        if (spCultivo.getSelectedItemPosition() == 0) {
            Toast.makeText(this, R.string.error_cultivo, Toast.LENGTH_SHORT).show();
            return;
        }
        if (cantidadTexto.isEmpty()) {
            etCantidad.setError(getString(R.string.error_cantidad_vacia));
            etCantidad.requestFocus();
            return;
        }
        int cantidad = Integer.parseInt(cantidadTexto);
        if (cantidad < 1 || cantidad > 200) {
            etCantidad.setError(getString(R.string.error_cantidad_rango));
            etCantidad.requestFocus();
            return;
        }
        if (fecha.isEmpty()) {
            etFecha.setError(getString(R.string.error_fecha));
            Toast.makeText(this, R.string.error_fecha, Toast.LENGTH_SHORT).show();
            return;
        }
        if (phTexto.isEmpty()) {
            etPh.setError(getString(R.string.error_ph_vacio));
            etPh.requestFocus();
            return;
        }
        double ph;
        try {
            ph = Double.parseDouble(phTexto);
        } catch (NumberFormatException e) {
            etPh.setError(getString(R.string.error_ph_numero));
            etPh.requestFocus();
            return;
        }
        if (ph < 0 || ph > 14) {
            etPh.setError(getString(R.string.error_ph_rango));
            etPh.requestFocus();
            return;
        }

        // La posición 0 es "Selecciona un cultivo", por eso restamos 1
        Cultivo cultivo = Cultivo.lista[spCultivo.getSelectedItemPosition() - 1];

        // Enviamos los datos a ConfirmActivity y esperamos su respuesta
        Intent confirmar = new Intent(FormActivity.this, ConfirmActivity.class);
        confirmar.putExtra("cultivo", cultivo.clave);
        confirmar.putExtra("cantidad", cantidad);
        confirmar.putExtra("fecha", fecha);
        confirmar.putExtra("ph", ph);
        confirmar.putExtra("notas", notas);
        confirmarLauncher.launch(confirmar);
    }

    private void guardarSiembra(String codigo) {
        Cultivo cultivo = Cultivo.lista[spCultivo.getSelectedItemPosition() - 1];

        SharedPreferences prefs = getSharedPreferences("gestionhxh", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("reg_cultivo", cultivo.clave);
        editor.putInt("reg_cantidad", Integer.parseInt(etCantidad.getText().toString().trim()));
        editor.putString("reg_fecha", etFecha.getText().toString());
        editor.putString("reg_codigo", codigo);
        editor.apply();
    }
}