package model;

import java.util.Date;

/**
 * Representa un usuario que está actualmente conectado al sistema
 */
public class UsuarioActivo {
    private String usuarioId;
    private String nombre;
    private String rol;
    private Date fechaLogin;
    private String ipAddress;

    public UsuarioActivo(String usuarioId, String nombre, String rol, Date fechaLogin, String ipAddress) {
        this.usuarioId = usuarioId;
        this.nombre = nombre;
        this.rol = rol;
        this.fechaLogin = fechaLogin;
        this.ipAddress = ipAddress;
    }

    // Getters
    public String getUsuarioId() {
        return usuarioId;
    }
    public String getNombre() {
        return nombre;
    }
    public String getRol() {
        return rol;
    }
    public Date getFechaLogin() {
        return fechaLogin;
    }
    public String getIpAddress() {
        return ipAddress;
    }

    // Setters
    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void setRol(String rol) {
        this.rol = rol;
    }
    public void setFechaLogin(Date fechaLogin) {
        this.fechaLogin = fechaLogin;
    }
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}