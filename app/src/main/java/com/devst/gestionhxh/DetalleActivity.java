package com.devst.gestionhxh;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// INTENT EXPLÍCITO 1: recibe los datos del cultivo enviados con putExtra
public class DetalleActivity extends AppCompatActivity {

    Button btnVolver;
    TextView tvEmoji, tvNombre, tvDescripcion, tvCondiciones, tvCultivar, tvCuidados, tvComparacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalle);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnVolver = findViewById(R.id.btnVolver);
        tvEmoji = findViewById(R.id.tvEmoji);
        tvNombre = findViewById(R.id.tvNombre);
        tvDescripcion = findViewById(R.id.tvDescripcion);
        tvCondiciones = findViewById(R.id.tvCondiciones);
        tvCultivar = findViewById(R.id.tvCultivar);
        tvCuidados = findViewById(R.id.tvCuidados);
        tvComparacion = findViewById(R.id.tvComparacion);

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // cierra esta pantalla y vuelve a la anterior
            }
        });

        // Recibimos los datos que mandó MainActivity
        Intent intent = getIntent();
        String nombre = intent.getStringExtra("nombre");

        // Validación: si no llegó el nombre no hay nada que mostrar
        if (nombre == null) {
            Toast.makeText(this, R.string.detalle_sin_datos, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        double phMin = intent.getDoubleExtra("phMin", 0);
        double phMax = intent.getDoubleExtra("phMax", 0);
        int ecMin = intent.getIntExtra("ecMin", 0);
        int ecMax = intent.getIntExtra("ecMax", 0);
        int tempMin = intent.getIntExtra("tempMin", 0);
        int tempMax = intent.getIntExtra("tempMax", 0);

        tvEmoji.setText(intent.getStringExtra("emoji"));
        tvNombre.setText(nombre);
        tvDescripcion.setText(intent.getStringExtra("descripcion"));

        tvCondiciones.setText(getString(R.string.detalle_texto_condiciones,
                String.valueOf(phMin), String.valueOf(phMax),
                ecMin, ecMax, tempMin, tempMax,
                intent.getStringExtra("horasLuz")));

        tvCultivar.setText(getString(R.string.detalle_texto_cultivar,
                intent.getStringExtra("sistema"),
                intent.getStringExtra("germinacion"),
                intent.getStringExtra("distancia"),
                intent.getStringExtra("cosecha")));

        tvCuidados.setText(getString(R.string.detalle_texto_cuidados,
                intent.getStringExtra("nutrientes"),
                intent.getStringExtra("plagas"),
                intent.getStringExtra("consejo")));

        // Comparamos con la última lectura de sensores (si existe)
        boolean hayLectura = intent.getBooleanExtra("hayLectura", false);
        if (hayLectura) {
            double ph = intent.getDoubleExtra("lecturaPh", 0);
            int ec = intent.getIntExtra("lecturaEc", 0);
            double temp = intent.getDoubleExtra("lecturaTemp", 0);

            String texto = "";
            if (ph < phMin) {
                texto += getString(R.string.comp_ph_bajo, String.valueOf(ph)) + "\n";
            } else if (ph > phMax) {
                texto += getString(R.string.comp_ph_alto, String.valueOf(ph)) + "\n";
            } else {
                texto += getString(R.string.comp_ph_ok, String.valueOf(ph)) + "\n";
            }

            if (ec < ecMin) {
                texto += getString(R.string.comp_ec_bajo, ec) + "\n";
            } else if (ec > ecMax) {
                texto += getString(R.string.comp_ec_alto, ec) + "\n";
            } else {
                texto += getString(R.string.comp_ec_ok, ec) + "\n";
            }

            if (temp < tempMin) {
                texto += getString(R.string.comp_temp_bajo, String.valueOf(temp));
            } else if (temp > tempMax) {
                texto += getString(R.string.comp_temp_alto, String.valueOf(temp));
            } else {
                texto += getString(R.string.comp_temp_ok, String.valueOf(temp));
            }
            tvComparacion.setText(texto);
        } else {
            tvComparacion.setText(R.string.detalle_sin_lectura);
        }
    }
}