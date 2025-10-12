package model;

public abstract class Usuario {
    protected String id;
    protected String clave;
    protected String nombre;

    public Usuario(String id, String clave, String nombre) {
        this.id = id;
        this.clave = clave;
        this.nombre = nombre;
    }

    public String getId() { return id; }
    public String getClave() { return clave; }
    public String getNombre() { return nombre; }

    public void setClave(String nuevaClave) {
        this.clave = nuevaClave;
    }
    public abstract String getRol();
}