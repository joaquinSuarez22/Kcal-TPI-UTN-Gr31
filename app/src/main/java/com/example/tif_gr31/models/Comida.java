package com.example.tif_gr31.models;

/**
 * Modelo de datos simple que representa un ítem de comida.
 * Nota: Aunque en Firestore se usa un Map dinámico para mayor flexibilidad con ingredientes,
 * este POJO puede ser utilizado para transferencias de datos locales o compatibilidad.
 */
public class Comida {

    private int id;
    private String nombre;
    private int calorias;
    private double proteinas;

    /**
     * Constructor vacío requerido para la deserialización de Firebase.
     */
    public Comida() {
    }

    /**
     * Constructor completo para inicializar una instancia de comida.
     * 
     * @param id        Identificador único (opcional).
     * @param nombre    Nombre o categoría de la comida.
     * @param calorias  Valor energético total.
     * @param proteinas Gramos de proteína totales.
     */
    public Comida(int id, String nombre, int calorias, double proteinas) {
        this.id = id;
        this.nombre = nombre;
        this.calorias = calorias;
        this.proteinas = proteinas;
    }

    // --- GETTERS Y SETTERS ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCalorias() {
        return calorias;
    }

    public void setCalorias(int calorias) {
        this.calorias = calorias;
    }

    public double getProteinas() {
        return proteinas;
    }

    public void setProteinas(double proteinas) {
        this.proteinas = proteinas;
    }
}
