package dao;

import config.Database;
import model.UsuarioActivo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioActivoDao {
    
    /**
     * Registra un usuario como activo (login)
     */
    public boolean registrarLogin(UsuarioActivo usuarioActivo) throws SQLException {
        String sql = "INSERT INTO usuarios_activos (usuario_id, nombre, rol, fecha_login, ip_address) " +
                     "VALUES (?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE fecha_login = ?, ip_address = ?";
        
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuarioActivo.getUsuarioId());
            stmt.setString(2, usuarioActivo.getNombre());
            stmt.setString(3, usuarioActivo.getRol());
            stmt.setTimestamp(4, new Timestamp(usuarioActivo.getFechaLogin().getTime()));
            stmt.setString(5, usuarioActivo.getIpAddress());
            stmt.setTimestamp(6, new Timestamp(usuarioActivo.getFechaLogin().getTime()));
            stmt.setString(7, usuarioActivo.getIpAddress());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Registra el logout de un usuario
     */
    public boolean registrarLogout(String usuarioId) throws SQLException {
        String sql = "DELETE FROM usuarios_activos WHERE usuario_id = ?";
        
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuarioId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Lista todos los usuarios activos
     */
    public List<UsuarioActivo> listarActivos() throws SQLException {
        List<UsuarioActivo> usuariosActivos = new ArrayList<>();
        String sql = "SELECT * FROM usuarios_activos ORDER BY nombre";
        
        try (Connection conn = Database.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String usuarioId = rs.getString("usuario_id");
                String nombre = rs.getString("nombre");
                String rol = rs.getString("rol");
                java.util.Date fechaLogin = new java.util.Date(rs.getTimestamp("fecha_login").getTime());
                String ipAddress = rs.getString("ip_address");
                
                UsuarioActivo usuarioActivo = new UsuarioActivo(usuarioId, nombre, rol, fechaLogin, ipAddress);
                usuariosActivos.add(usuarioActivo);
            }
        }
        return usuariosActivos;
    }
    
    /**
     * Verifica si un usuario está activo
     */
    public boolean estaActivo(String usuarioId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuarios_activos WHERE usuario_id = ?";
        
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
    
    /**
     * Limpia usuarios inactivos (opcional, para mantenimiento)
     * Elimina usuarios que llevan más de X horas sin actividad
     */
    public int limpiarInactivos(int horasInactividad) throws SQLException {
        String sql = "DELETE FROM usuarios_activos WHERE fecha_login < DATE_SUB(NOW(), INTERVAL ? HOUR)";
        
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, horasInactividad);
            return stmt.executeUpdate();
        }
    }
}