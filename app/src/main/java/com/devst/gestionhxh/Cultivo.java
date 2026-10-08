package com.devst.gestionhxh;

// Clase que guarda la información de cada cultivo del huerto.
// Los textos están en res/values/strings.xml, aquí solo guardamos su id (R.string...)
public class Cultivo {

    String clave;        // nombre interno para guardarlo en SharedPreferences
    int nombre;
    int emoji;
    int descripcion;

    // Rangos ideales (se usan para comparar con los sensores)
    double phMin;
    double phMax;
    int ecMin;      // conductividad en µS/cm
    int ecMax;
    int tempMin;    // temperatura en °C
    int tempMax;

    // Información extra para mostrar en el detalle
    int sistema;
    int horasLuz;
    int germinacion;
    int cosecha;
    int distancia;
    int nutrientes;
    int plagas;
    int consejo;

    public Cultivo(String clave, int nombre, int emoji, int descripcion,
                   double phMin, double phMax, int ecMin, int ecMax, int tempMin, int tempMax,
                   int sistema, int horasLuz, int germinacion, int cosecha,
                   int distancia, int nutrientes, int plagas, int consejo) {
        this.clave = clave;
        this.nombre = nombre;
        this.emoji = emoji;
        this.descripcion = descripcion;
        this.phMin = phMin;
        this.phMax = phMax;
        this.ecMin = ecMin;
        this.ecMax = ecMax;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.sistema = sistema;
        this.horasLuz = horasLuz;
        this.germinacion = germinacion;
        this.cosecha = cosecha;
        this.distancia = distancia;
        this.nutrientes = nutrientes;
        this.plagas = plagas;
        this.consejo = consejo;
    }

    // Lista con todos los cultivos de la app
    public static Cultivo[] lista = {
            new Cultivo("lechuga", R.string.lechuga_nombre, R.string.lechuga_emoji, R.string.lechuga_descripcion,
                    5.5, 6.5, 800, 1200, 15, 22,
                    R.string.lechuga_sistema, R.string.lechuga_luz, R.string.lechuga_germinacion,
                    R.string.lechuga_cosecha, R.string.lechuga_distancia, R.string.lechuga_nutrientes,
                    R.string.lechuga_plagas, R.string.lechuga_consejo),

            new Cultivo("albahaca", R.string.albahaca_nombre, R.string.albahaca_emoji, R.string.albahaca_descripcion,
                    5.5, 6.5, 1000, 1600, 20, 27,
                    R.string.albahaca_sistema, R.string.albahaca_luz, R.string.albahaca_germinacion,
                    R.string.albahaca_cosecha, R.string.albahaca_distancia, R.string.albahaca_nutrientes,
                    R.string.albahaca_plagas, R.string.albahaca_consejo),

            new Cultivo("tomate", R.string.tomate_nombre, R.string.tomate_emoji, R.string.tomate_descripcion,
                    5.8, 6.8, 2000, 3500, 18, 26,
                    R.string.tomate_sistema, R.string.tomate_luz, R.string.tomate_germinacion,
                    R.string.tomate_cosecha, R.string.tomate_distancia, R.string.tomate_nutrientes,
                    R.string.tomate_plagas, R.string.tomate_consejo),

            new Cultivo("frutilla", R.string.frutilla_nombre, R.string.frutilla_emoji, R.string.frutilla_descripcion,
                    5.5, 6.2, 1000, 1500, 16, 24,
                    R.string.frutilla_sistema, R.string.frutilla_luz, R.string.frutilla_germinacion,
                    R.string.frutilla_cosecha, R.string.frutilla_distancia, R.string.frutilla_nutrientes,
                    R.string.frutilla_plagas, R.string.frutilla_consejo),

            new Cultivo("espinaca", R.string.espinaca_nombre, R.string.espinaca_emoji, R.string.espinaca_descripcion,
                    6.0, 7.0, 1800, 2300, 10, 20,
                    R.string.espinaca_sistema, R.string.espinaca_luz, R.string.espinaca_germinacion,
                    R.string.espinaca_cosecha, R.string.espinaca_distancia, R.string.espinaca_nutrientes,
                    R.string.espinaca_plagas, R.string.espinaca_consejo),

            new Cultivo("cilantro", R.string.cilantro_nombre, R.string.cilantro_emoji, R.string.cilantro_descripcion,
                    6.5, 6.7, 1200, 1800, 15, 25,
                    R.string.cilantro_sistema, R.string.cilantro_luz, R.string.cilantro_germinacion,
                    R.string.cilantro_cosecha, R.string.cilantro_distancia, R.string.cilantro_nutrientes,
                    R.string.cilantro_plagas, R.string.cilantro_consejo),
    };

    // Busca un cultivo por su clave. Si no lo encuentra devuelve null
    public static Cultivo buscar(String clave) {
        for (int i = 0; i < lista.length; i++) {
            if (lista[i].clave.equals(clave)) {
                return lista[i];
            }
        }
        return null;
    }
}
