package com.example.tif_gr31.models;

public class Comida {

    private int id;
    private String nombre;
    private int calorias;
    private double proteinas;

    public Comida() {
    }

    public Comida(int id, String nombre, int calorias, double proteinas) {
        this.id = id;
        this.nombre = nombre;
        this.calorias = calorias;
        this.proteinas = proteinas;
    }

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