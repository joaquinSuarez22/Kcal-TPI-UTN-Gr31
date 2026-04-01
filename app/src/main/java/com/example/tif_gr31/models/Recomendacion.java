package com.example.tif_gr31.models;

/**
 * Modelo de datos que representa una recomendación o consejo nutricional.
 * Cada recomendación tiene un contexto visual (icono), un título y un cuerpo de texto.
 */
public class Recomendacion {
    private int id;
    private String icono;
    private String titulo;
    private String texto;

    /**
     * Constructor para inicializar una recomendación.
     * 
     * @param id     Identificador de la recomendación (usado para la lógica de selección).
     * @param icono  Emoji o recurso gráfico que acompaña al consejo.
     * @param titulo Encabezado breve del consejo.
     * @param texto  Explicación detallada y pasos a seguir.
     */
    public Recomendacion(int id, String icono, String titulo, String texto) {
        this.id = id;
        this.icono = icono;
        this.titulo = titulo;
        this.texto = texto;
    }

    // --- GETTERS ---

    public int getId() { return id; }
    public String getIcono() { return icono; }
    public String getTitulo() { return titulo; }
    public String getTexto() { return texto; }
}
