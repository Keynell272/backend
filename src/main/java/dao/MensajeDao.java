package dao;

import config.Database;
import model.Mensaje;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MensajeDao {
    
    /**
     * Inserta un nuevo mensaje
     */
    public boolean insertar(Mensaje mensaje) throws SQLException {
        String sql = "INSERT INTO mensajes (remitente_id, remitente_nombre, destinatario_id, destinatario_nombre, texto, fecha_envio, leido) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, mensaje.getRemitenteId());
            stmt.setString(2, mensaje.getRemitenteNombre());
            stmt.setString(3, mensaje.getDestinatarioId());
            stmt.setString(4, mensaje.getDestinatarioNombre());
            stmt.setString(5, mensaje.getTexto());
            stmt.setTimestamp(6, new Timestamp(mensaje.getFechaEnvio().getTime()));
            stmt.setBoolean(7, mensaje.isLeido());
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                // Obtener el ID generado
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    mensaje.setId(rs.getInt(1));
                }
                return true;
            }
            return false;
        }
    }
    
    /**
     * Busca todos los mensajes recibidos por un usuario (no leídos)
     */
    public List<Mensaje> buscarMensajesNoLeidos(String usuarioId) throws SQLException {
        List<Mensaje> mensajes = new ArrayList<>();
        String sql = "SELECT * FROM mensajes WHERE destinatario_id = ? AND leido = FALSE ORDER BY fecha_envio DESC";
        
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Mensaje mensaje = crearMensajeDesdeResultSet(rs);
                mensajes.add(mensaje);
            }
        }
        return mensajes;
    }
    
    /**
     * Busca todos los mensajes de un usuario (enviados y recibidos)
     */
    public List<Mensaje> buscarPorUsuario(String usuarioId) throws SQLException {
        List<Mensaje> mensajes = new ArrayList<>();
        String sql = "SELECT * FROM mensajes WHERE remitente_id = ? OR destinatario_id = ? ORDER BY fecha_envio DESC";
        
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuarioId);
            stmt.setString(2, usuarioId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Mensaje mensaje = crearMensajeDesdeResultSet(rs);
                mensajes.add(mensaje);
            }
        }
        return mensajes;
    }
    
    /**
     * Marca un mensaje como leído
     */
    public boolean marcarComoLeido(int mensajeId) throws SQLException {
        String sql = "UPDATE mensajes SET leido = TRUE WHERE id = ?";
        
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, mensajeId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Cuenta mensajes no leídos de un usuario
     */
    public int contarNoLeidos(String usuarioId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM mensajes WHERE destinatario_id = ? AND leido = FALSE";
        
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
    
    /**
     * Método auxiliar para crear un Mensaje desde un ResultSet
     */
    private Mensaje crearMensajeDesdeResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String remitenteId = rs.getString("remitente_id");
        String remitenteNombre = rs.getString("remitente_nombre");
        String destinatarioId = rs.getString("destinatario_id");
        String destinatarioNombre = rs.getString("destinatario_nombre");
        String texto = rs.getString("texto");
        java.util.Date fechaEnvio = new java.util.Date(rs.getTimestamp("fecha_envio").getTime());
        boolean leido = rs.getBoolean("leido");
        
        return new Mensaje(id, remitenteId, remitenteNombre, destinatarioId, 
                          destinatarioNombre, texto, fechaEnvio, leido);
    }
}