package com.example.tif_gr31.models;

public class Recomendacion {
    private int id;
    private String icono;
    private String titulo;
    private String texto;

    public Recomendacion(int id, String icono, String titulo, String texto) {
        this.id = id;
        this.icono = icono;
        this.titulo = titulo;
        this.texto = texto;
    }

    public int getId() { return id; }
    public String getIcono() { return icono; }
    public String getTitulo() { return titulo; }
    public String getTexto() { return texto; }
}
