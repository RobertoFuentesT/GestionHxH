package com.devst.gestionhxh;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// INTENT EXPLÍCITO 2: pantalla de ajustes con Toolbar y botón Atrás
public class ConfigActivity extends AppCompatActivity {

    EditText etNombre, etDireccion, etTelefono, etCorreo, etUrl;
    Button btnGuardar, btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_config);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Toolbar con la flecha de "Atrás"
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etNombre = findViewById(R.id.etNombre);
        etDireccion = findViewById(R.id.etDireccion);
        etTelefono = findViewById(R.id.etTelefono);
        etCorreo = findViewById(R.id.etCorreo);
        etUrl = findViewById(R.id.etUrl);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnVolver = findViewById(R.id.btnVolver);

        // Mostramos lo que ya estaba guardado
        SharedPreferences prefs = getSharedPreferences("gestionhxh", MODE_PRIVATE);
        etNombre.setText(prefs.getString("nombre", ""));
        etDireccion.setText(prefs.getString("direccion", ""));
        etTelefono.setText(prefs.getString("telefono", ""));
        etCorreo.setText(prefs.getString("correo", ""));
        etUrl.setText(prefs.getString("url", getString(R.string.url_guia_defecto)));

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardar();
            }
        });

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    // Flecha de la Toolbar
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void guardar() {
        String nombre = etNombre.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String url = etUrl.getText().toString().trim();

        // ----- Validaciones -----
        if (nombre.length() < 2) {
            etNombre.setError(getString(R.string.error_nombre));
            etNombre.requestFocus();
            return;
        }
        if (direccion.length() < 5) {
            etDireccion.setError(getString(R.string.error_direccion));
            etDireccion.requestFocus();
            return;
        }
        if (telefono.length() < 8) {
            etTelefono.setError(getString(R.string.error_telefono));
            etTelefono.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            etCorreo.setError(getString(R.string.error_correo));
            etCorreo.requestFocus();
            return;
        }
        if (!url.startsWith("https://")) {
            etUrl.setError(getString(R.string.error_url));
            etUrl.requestFocus();
            return;
        }

        // Si todo está bien, guardamos los datos
        SharedPreferences prefs = getSharedPreferences("gestionhxh", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("nombre", nombre);
        editor.putString("direccion", direccion);
        editor.putString("telefono", telefono);
        editor.putString("correo", correo);
        editor.putString("url", url);
        editor.apply();

        Toast.makeText(this, R.string.aviso_datos_guardados, Toast.LENGTH_SHORT).show();
        finish();
    }
}