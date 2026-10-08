package com.devst.gestionhxh;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

// INTENT EXPLÍCITO 3 (parte 2): muestra los datos y devuelve una respuesta a FormActivity
public class ConfirmActivity extends AppCompatActivity {

    TextView tvResumen, tvAdvertencia;
    ProgressBar pbGuardando;
    Button btnConfirmar, btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_confirm);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvResumen = findViewById(R.id.tvResumen);
        tvAdvertencia = findViewById(R.id.tvAdvertencia);
        pbGuardando = findViewById(R.id.pbGuardando);
        btnConfirmar = findViewById(R.id.btnConfirmar);
        btnVolver = findViewById(R.id.btnVolver);

        // Recibimos los datos del formulario
        Intent intent = getIntent();
        Cultivo cultivo = Cultivo.buscar(intent.getStringExtra("cultivo"));
        int cantidad = intent.getIntExtra("cantidad", 0);
        String fecha = intent.getStringExtra("fecha");
        double ph = intent.getDoubleExtra("ph", 0);
        String notas = intent.getStringExtra("notas");

        // Validación: si no llegó el cultivo volvemos al formulario
        if (cultivo == null) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        if (notas == null || notas.isEmpty()) {
            notas = getString(R.string.confirm_sin_notas);
        }

        tvResumen.setText(getString(R.string.confirm_resumen,
                getString(cultivo.emoji), getString(cultivo.nombre),
                cantidad, fecha, String.valueOf(ph), notas));

        // Advertencia si el pH no es el ideal para ese cultivo
        if (ph < cultivo.phMin || ph > cultivo.phMax) {
            tvAdvertencia.setText(getString(R.string.confirm_advertencia_ph,
                    getString(cultivo.nombre), String.valueOf(cultivo.phMin), String.valueOf(cultivo.phMax)));
            tvAdvertencia.setVisibility(View.VISIBLE);
        }

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmar();
            }
        });

        // Volver sin guardar: devolvemos RESULT_CANCELED
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    private void confirmar() {
        // Mostramos que está cargando y bloqueamos los botones
        pbGuardando.setVisibility(View.VISIBLE);
        btnConfirmar.setEnabled(false);
        btnVolver.setEnabled(false);

        // Thread que simula que se están guardando los datos
        Thread hilo = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Creamos un código para la siembra, por ejemplo HU-1234
                final String codigo = getString(R.string.formato_codigo, 1000 + new Random().nextInt(9000));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Devolvemos RESULT_OK junto con el código
                        Intent respuesta = new Intent();
                        respuesta.putExtra("codigo", codigo);
                        setResult(RESULT_OK, respuesta);
                        finish();
                    }
                });
            }
        });
        hilo.start();
    }
}