package model;
public class DetalleReceta {
    private int id; // Nuevo campo para BD
    private Medicamento medicamento;
    private int cantidad;
    private String indicaciones;
    private int duracionDias;

    // Constructor sin id (para crear nuevos detalles)
    public DetalleReceta(Medicamento medicamento, int cantidad, String indicaciones, int duracionDias) {
        this.medicamento = medicamento;
        this.cantidad = cantidad;
        this.indicaciones = indicaciones;
        this.duracionDias = duracionDias;
    }
    
    // Constructor con id (para cargar desde BD)
    public DetalleReceta(int id, Medicamento medicamento, int cantidad, String indicaciones, int duracionDias) {
        this.id = id;
        this.medicamento = medicamento;
        this.cantidad = cantidad;
        this.indicaciones = indicaciones;
        this.duracionDias = duracionDias;
    }

    // Getters
    public int getId() {
        return id;
    }
    public Medicamento getMedicamento() {
        return medicamento;
    }
    public int getCantidad() {
        return cantidad;
    }
    public String getIndicaciones() {
        return indicaciones;
    }
    public int getDuracionDias() {
        return duracionDias;
    }
    
    // Setters
    public void setId(int id) {
        this.id = id;
    }
    public void setMedicamento(Medicamento medicamento) {
        this.medicamento = medicamento;
    }
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    public void setIndicaciones(String indicaciones) {
        this.indicaciones = indicaciones;
    }
    public void setDuracionDias(int duracionDias) {
        this.duracionDias = duracionDias;
    }
}