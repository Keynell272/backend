package model;

public class Administrador extends Usuario {
    public Administrador(String id, String clave, String nombre) {
        super(id, clave, nombre);
    }
    
    public String getRol() {
        return "ADM";
    }
}