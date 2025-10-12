package model;

import java.util.Date;

/**
 * Representa un mensaje entre usuarios del sistema
 */
public class Mensaje {
    private int id;
    private String remitenteId;
    private String remitenteNombre;
    private String destinatarioId;
    private String destinatarioNombre;
    private String texto;
    private Date fechaEnvio;
    private boolean leido;

    // Constructor sin id (para crear nuevos mensajes)
    public Mensaje(String remitenteId, String remitenteNombre, String destinatarioId, 
                   String destinatarioNombre, String texto) {
        this.remitenteId = remitenteId;
        this.remitenteNombre = remitenteNombre;
        this.destinatarioId = destinatarioId;
        this.destinatarioNombre = destinatarioNombre;
        this.texto = texto;
        this.fechaEnvio = new Date();
        this.leido = false;
    }

    // Constructor completo (para cargar desde BD)
    public Mensaje(int id, String remitenteId, String remitenteNombre, String destinatarioId, 
                   String destinatarioNombre, String texto, Date fechaEnvio, boolean leido) {
        this.id = id;
        this.remitenteId = remitenteId;
        this.remitenteNombre = remitenteNombre;
        this.destinatarioId = destinatarioId;
        this.destinatarioNombre = destinatarioNombre;
        this.texto = texto;
        this.fechaEnvio = fechaEnvio;
        this.leido = leido;
    }

    // Getters
    public int getId() {
        return id;
    }
    public String getRemitenteId() {
        return remitenteId;
    }
    public String getRemitenteNombre() {
        return remitenteNombre;
    }
    public String getDestinatarioId() {
        return destinatarioId;
    }
    public String getDestinatarioNombre() {
        return destinatarioNombre;
    }
    public String getTexto() {
        return texto;
    }
    public Date getFechaEnvio() {
        return fechaEnvio;
    }
    public boolean isLeido() {
        return leido;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }
    public void setRemitenteId(String remitenteId) {
        this.remitenteId = remitenteId;
    }
    public void setRemitenteNombre(String remitenteNombre) {
        this.remitenteNombre = remitenteNombre;
    }
    public void setDestinatarioId(String destinatarioId) {
        this.destinatarioId = destinatarioId;
    }
    public void setDestinatarioNombre(String destinatarioNombre) {
        this.destinatarioNombre = destinatarioNombre;
    }
    public void setTexto(String texto) {
        this.texto = texto;
    }
    public void setFechaEnvio(Date fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }
    public void setLeido(boolean leido) {
        this.leido = leido;
    }
    
    public void marcarComoLeido() {
        this.leido = true;
    }
}