package com.devst.gestionhxh;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    TextView tvSaludo, tvUltimoRegistro, tvSensores;
    Button btnLinterna, btnLeerSensores;
    ProgressBar pbSensores;
    ImageView ivFoto;

    // Linterna
    CameraManager cameraManager;
    String idCamara;
    boolean linternaEncendida = false;

    // Última lectura de los sensores
    boolean hayLectura = false;
    double ultimoPh;
    int ultimoEc;
    double ultimaTemp;
    boolean nivelAguaOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvSaludo = findViewById(R.id.tvSaludo);
        tvUltimoRegistro = findViewById(R.id.tvUltimoRegistro);
        tvSensores = findViewById(R.id.tvSensores);
        btnLinterna = findViewById(R.id.btnLinterna);
        btnLeerSensores = findViewById(R.id.btnLeerSensores);
        pbSensores = findViewById(R.id.pbSensores);
        ivFoto = findViewById(R.id.ivFoto);

        // Preparamos la linterna (usa el flash de la cámara trasera)
        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            idCamara = cameraManager.getCameraIdList()[0];
        } catch (Exception e) {
            idCamara = null;
        }

        // ---------- FUNCIONES ----------
        btnLinterna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cambiarLinterna();
            }
        });

        btnLeerSensores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leerSensores();
            }
        });

        // ---------- CULTIVOS: intent explícito a DetalleActivity ----------
        findViewById(R.id.btnLechuga).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirDetalle(Cultivo.lista[0]);
            }
        });
        findViewById(R.id.btnAlbahaca).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirDetalle(Cultivo.lista[1]);
            }
        });
        findViewById(R.id.btnTomate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirDetalle(Cultivo.lista[2]);
            }
        });
        findViewById(R.id.btnFrutilla).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirDetalle(Cultivo.lista[3]);
            }
        });
        findViewById(R.id.btnEspinaca).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirDetalle(Cultivo.lista[4]);
            }
        });
        findViewById(R.id.btnCilantro).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirDetalle(Cultivo.lista[5]);
            }
        });

        // ---------- OTRAS PANTALLAS: intents explícitos ----------
        findViewById(R.id.btnRegistrar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent formulario = new Intent(MainActivity.this, FormActivity.class);
                startActivity(formulario);
            }
        });

        findViewById(R.id.btnConfig).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent config = new Intent(MainActivity.this, ConfigActivity.class);
                startActivity(config);
            }
        });
    }

    // Se ejecuta cada vez que volvemos a esta pantalla
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("gestionhxh", MODE_PRIVATE);

        String nombre = prefs.getString("nombre", "");
        if (nombre.isEmpty()) {
            tvSaludo.setText(R.string.saludo_sin_nombre);
        } else {
            tvSaludo.setText(getString(R.string.saludo_con_nombre, nombre));
        }

        Cultivo cultivo = Cultivo.buscar(prefs.getString("reg_cultivo", ""));
        if (cultivo != null) {
            // getQuantityString elige "planta" o "plantas" según la cantidad
            int cantidad = prefs.getInt("reg_cantidad", 0);
            String plantas = getResources().getQuantityString(R.plurals.cantidad_plantas, cantidad, cantidad);
            tvUltimoRegistro.setText(getString(R.string.ultima_siembra,
                    getString(cultivo.nombre),
                    plantas,
                    prefs.getString("reg_fecha", ""),
                    prefs.getString("reg_codigo", "")));
        }
    }

    // Si salimos de la app apagamos la linterna
    @Override
    protected void onPause() {
        super.onPause();
        if (linternaEncendida) {
            cambiarLinterna();
        }
    }

    // =====================================================
    // LINTERNA
    // =====================================================
    private void cambiarLinterna() {
        // Validamos que el teléfono tenga flash
        if (idCamara == null || !getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            Toast.makeText(this, R.string.aviso_sin_flash, Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            cameraManager.setTorchMode(idCamara, !linternaEncendida);
            linternaEncendida = !linternaEncendida;
            if (linternaEncendida) {
                btnLinterna.setText(R.string.btn_linterna_apagar);
            } else {
                btnLinterna.setText(R.string.btn_linterna_encender);
            }
        } catch (Exception e) {
            // Por ejemplo en el emulador, donde la cámara no tiene flash
            Toast.makeText(this, R.string.aviso_error_linterna, Toast.LENGTH_SHORT).show();
        }
    }

    // =====================================================
    // SENSORES (usa un Thread para no congelar la pantalla)
    // =====================================================
    private void leerSensores() {
        btnLeerSensores.setEnabled(false);
        pbSensores.setVisibility(View.VISIBLE);
        pbSensores.setProgress(0);
        tvSensores.setText(R.string.sensores_leyendo);

        Thread hilo = new Thread(new Runnable() {
            @Override
            public void run() {
                // Simulamos que leer cada sensor demora un poco
                for (int i = 1; i <= 5; i++) {
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    final int progreso = i * 20;
                    // La pantalla solo se puede modificar desde el hilo principal
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pbSensores.setProgress(progreso);
                        }
                    });
                }

                // Valores al azar como si vinieran de los sensores
                Random random = new Random();
                ultimoPh = (50 + random.nextInt(21)) / 10.0;  // entre 5.0 y 7.0
                ultimoEc = 700 + random.nextInt(1300);        // entre 700 y 2000
                ultimaTemp = 14 + random.nextInt(15);         // entre 14 y 28
                nivelAguaOk = random.nextInt(10) > 0;         // casi siempre hay agua
                hayLectura = true;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mostrarLecturas();
                        pbSensores.setVisibility(View.GONE);
                        btnLeerSensores.setEnabled(true);
                    }
                });
            }
        });
        hilo.start();
    }

    private void mostrarLecturas() {
        // Comparamos con el cultivo de la última siembra (si no hay, con la lechuga)
        SharedPreferences prefs = getSharedPreferences("gestionhxh", MODE_PRIVATE);
        Cultivo c = Cultivo.buscar(prefs.getString("reg_cultivo", ""));
        if (c == null) {
            c = Cultivo.lista[0];
        }

        String texto = "";
        if (ultimoPh >= c.phMin && ultimoPh <= c.phMax) {
            texto += getString(R.string.lectura_ph_ok, String.valueOf(ultimoPh)) + "\n";
        } else {
            texto += getString(R.string.lectura_ph_mal, String.valueOf(ultimoPh),
                    String.valueOf(c.phMin), String.valueOf(c.phMax)) + "\n";
        }
        if (ultimoEc >= c.ecMin && ultimoEc <= c.ecMax) {
            texto += getString(R.string.lectura_ec_ok, ultimoEc) + "\n";
        } else {
            texto += getString(R.string.lectura_ec_mal, ultimoEc, c.ecMin, c.ecMax) + "\n";
        }
        if (ultimaTemp >= c.tempMin && ultimaTemp <= c.tempMax) {
            texto += getString(R.string.lectura_temp_ok, String.valueOf(ultimaTemp)) + "\n";
        } else {
            texto += getString(R.string.lectura_temp_mal, String.valueOf(ultimaTemp), c.tempMin, c.tempMax) + "\n";
        }
        if (nivelAguaOk) {
            texto += getString(R.string.lectura_agua_ok) + "\n";
        } else {
            texto += getString(R.string.lectura_agua_mal) + "\n";
        }
        texto += "\n" + getString(R.string.lectura_comparado, getString(c.emoji), getString(c.nombre));
        tvSensores.setText(texto);
    }

    // =====================================================
    // INTENT EXPLÍCITO 1: enviar datos a DetalleActivity
    // =====================================================
    private void abrirDetalle(Cultivo c) {
        Intent detalle = new Intent(MainActivity.this, DetalleActivity.class);
        detalle.putExtra("nombre", getString(c.nombre));
        detalle.putExtra("emoji", getString(c.emoji));
        detalle.putExtra("descripcion", getString(c.descripcion));
        detalle.putExtra("phMin", c.phMin);
        detalle.putExtra("phMax", c.phMax);
        detalle.putExtra("ecMin", c.ecMin);
        detalle.putExtra("ecMax", c.ecMax);
        detalle.putExtra("tempMin", c.tempMin);
        detalle.putExtra("tempMax", c.tempMax);
        detalle.putExtra("sistema", getString(c.sistema));
        detalle.putExtra("horasLuz", getString(c.horasLuz));
        detalle.putExtra("germinacion", getString(c.germinacion));
        detalle.putExtra("cosecha", getString(c.cosecha));
        detalle.putExtra("distancia", getString(c.distancia));
        detalle.putExtra("nutrientes", getString(c.nutrientes));
        detalle.putExtra("plagas", getString(c.plagas));
        detalle.putExtra("consejo", getString(c.consejo));

        // Si ya leímos los sensores, también los mandamos para compararlos
        detalle.putExtra("hayLectura", hayLectura);
        detalle.putExtra("lecturaPh", ultimoPh);
        detalle.putExtra("lecturaEc", ultimoEc);
        detalle.putExtra("lecturaTemp", ultimaTemp);
        startActivity(detalle);
    }
}