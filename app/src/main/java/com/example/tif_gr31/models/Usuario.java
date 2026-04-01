package com.example.tif_gr31.models;

/**
 * Modelo de datos que representa a un usuario en el sistema.
 * Contiene información básica de autenticación y vinculación con Firestore.
 */
public class Usuario {

    private int id; // ID incremental (si se usa base de datos local)
    private String nombre;
    private String email;
    private String password;

    /**
     * Constructor vacío requerido por Firebase Firestore para mapear documentos a objetos.
     */
    public Usuario() {
    }

    /**
     * Constructor completo para inicializar un usuario.
     * 
     * @param id       Identificador único.
     * @param nombre   Nombre para mostrar.
     * @param email    Correo electrónico único.
     * @param password Contraseña (manejada de forma segura por Firebase Auth).
     */
    public Usuario(int id, String nombre, String email, String password) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
