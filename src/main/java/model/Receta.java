package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Receta {
    private String id; 
    private Date fechaConfeccion;
    private Date fechaRetiro;
    private Date fechaProceso;
    private Date fechaLista;
    private Date fechaEntrega;
    private String estado; // "confeccionada", "proceso", "lista", "entregada"
    private List<DetalleReceta> detalles;
    private Paciente paciente;
    private String medicoId; // Nuevo campo para relacionar con médico

    public Receta(String id, Date fechaConfeccion, Date fechaRetiro, Paciente paciente) {
        this.id = id;
        this.fechaConfeccion = fechaConfeccion;
        this.fechaRetiro = fechaRetiro;
        this.paciente = paciente;
        this.detalles = new ArrayList<>();
        this.estado = "confeccionada"; // Estado inicial
    }

    public void agregarDetalle(DetalleReceta detalle) {
        detalles.add(detalle);
    }

    public void cambiarEstado(String nuevoEstado) {
        this.estado = nuevoEstado;
    }

    // Getters
    public String getId() {
        return id;
    }
    public Date getFechaConfeccion() {
        return fechaConfeccion;
    }
    public Date getFechaRetiro() {
        return fechaRetiro;
    }
    public String getEstado() {
        return estado;
    }
    public List<DetalleReceta> getDetalles() {
        return detalles;
    }
    public Paciente getPaciente() {
        return paciente;
    }
    public Date getFechaProceso() {
        return fechaProceso;
    }
    public Date getFechaLista() {
        return fechaLista;
    }
    public Date getFechaEntrega() {
        return fechaEntrega;
    }
    public String getMedicoId() {
        return medicoId;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }   
    public void setFechaConfeccion(Date fecha) {
        this.fechaConfeccion = fecha;
    }
    public void setFechaRetiro(Date fechaRetiro) {
        this.fechaRetiro = fechaRetiro;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
    public void setDetalles(List<DetalleReceta> detalles) {
        this.detalles = detalles;
    }
    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }
    public void setFechaProceso(Date fechaProceso) {
        this.fechaProceso = fechaProceso;
    }
    public void setFechaLista(Date fechaLista) {
        this.fechaLista = fechaLista;
    }
    public void setFechaEntrega(Date fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }
    public void setMedicoId(String medicoId) {
        this.medicoId = medicoId;
    }
}