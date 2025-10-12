package model;

public class Farmaceuta extends Usuario {
    public Farmaceuta(String id, String clave, String nombre) {
        super(id, clave, nombre);
    }
    
    public String getRol() {
        return "FAR";
    }
}