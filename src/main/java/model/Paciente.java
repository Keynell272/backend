package model;

import java.util.Date;

public class Paciente {
    private String id;
    private String nombre;
    private Date fechaNacimiento;
    private String telefono;

    public Paciente(String id, String nombre, Date fechaNacimiento, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
        this.telefono = telefono;
    }

    // Getters 
    public String getId() {
        return id;
    }
    public String getNombre() {
        return nombre;
    }
    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }
    public String getTelefono() {
        return telefono;
    }
    
    // Setters
    public void setId(String id) {
        this.id = id;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}