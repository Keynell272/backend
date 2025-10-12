package dao;

import config.Database;
import model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecetaDao {
    
    private DetalleRecetaDao detalleDao = new DetalleRecetaDao();
    private PacienteDao pacienteDao = new PacienteDao();
    
    /**
     * Inserta una nueva receta con sus detalles
     */
    public boolean insertar(Receta receta) throws SQLException {
        String sql = "INSERT INTO recetas (id, fecha_confeccion, fecha_retiro, estado, paciente_id, medico_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        Connection conn = Database.getInstance().getConnection();
        
        try {
            conn.setAutoCommit(false); // Iniciar transacción
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, receta.getId());
                stmt.setTimestamp(2, new Timestamp(receta.getFechaConfeccion().getTime()));
                stmt.setTimestamp(3, new Timestamp(receta.getFechaRetiro().getTime()));
                stmt.setString(4, receta.getEstado());
                stmt.setString(5, receta.getPaciente().getId());
                stmt.setString(6, receta.getMedicoId());
                
                stmt.executeUpdate();
            }
            
            // Insertar los detalles
            for (DetalleReceta detalle : receta.getDetalles()) {
                detalleDao.insertar(receta.getId(), detalle, conn);
            }
            
            conn.commit(); // Confirmar transacción
            return true;
            
        } catch (SQLException e) {
            conn.rollback(); // Revertir en caso de error
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }
    
    /**
     * Busca una receta por ID
     */
    public Receta buscarPorId(String id) throws SQLException {
        String sql = "SELECT * FROM recetas WHERE id = ?";
        
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                java.util.Date fechaConfeccion = new java.util.Date(rs.getTimestamp("fecha_confeccion").getTime());
                java.util.Date fechaRetiro = new java.util.Date(rs.getTimestamp("fecha_retiro").getTime());
                String pacienteId = rs.getString("paciente_id");
                String medicoId = rs.getString("medico_id");
                String estado = rs.getString("estado");
                
                Paciente paciente = pacienteDao.buscarPorId(pacienteId);
                
                Receta receta = new Receta(id, fechaConfeccion, fechaRetiro, paciente);
                receta.setEstado(estado);
                receta.setMedicoId(medicoId);
                
                // Cargar fechas de proceso si existen
                Timestamp fechaProceso = rs.getTimestamp("fecha_proceso");
                if (fechaProceso != null) {
                    receta.setFechaProceso(new java.util.Date(fechaProceso.getTime()));
                }
                
                Timestamp fechaLista = rs.getTimestamp("fecha_lista");
                if (fechaLista != null) {
                    receta.setFechaLista(new java.util.Date(fechaLista.getTime()));
                }
                
                Timestamp fechaEntrega = rs.getTimestamp("fecha_entrega");
                if (fechaEntrega != null) {
                    receta.setFechaEntrega(new java.util.Date(fechaEntrega.getTime()));
                }
                
                // Cargar los detalles
                List<DetalleReceta> detalles = detalleDao.buscarPorReceta(id);
                receta.setDetalles(detalles);
                
                return receta;
            }
        }
        return null;
    }
    
    /**
     * Actualiza el estado de una receta
     */
    public boolean actualizarEstado(String id, String nuevoEstado) throws SQLException {
        String sql = "UPDATE recetas SET estado = ?, ";
        
        // Agregar el campo de fecha correspondiente
        switch (nuevoEstado) {
            case "proceso":
                sql += "fecha_proceso = ? ";
                break;
            case "lista":
                sql += "fecha_lista = ? ";
                break;
            case "entregada":
                sql += "fecha_entrega = ? ";
                break;
            default:
                sql += "estado = estado "; // No actualizar fecha
        }
        
        sql += "WHERE id = ?";
        
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nuevoEstado);
            
            if (!nuevoEstado.equals("confeccionada")) {
                stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                stmt.setString(3, id);
            } else {
                stmt.setString(2, id);
            }
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Lista todas las recetas
     */
    public List<Receta> listarTodas() throws SQLException {
        List<Receta> recetas = new ArrayList<>();
        String sql = "SELECT id FROM recetas ORDER BY fecha_confeccion DESC";
        
        try (Connection conn = Database.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String id = rs.getString("id");
                Receta receta = buscarPorId(id);
                if (receta != null) {
                    recetas.add(receta);
                }
            }
        }
        return recetas;
    }
    
    /**
     * Lista recetas por estado
     */
    public List<Receta> listarPorEstado(String estado) throws SQLException {
        List<Receta> recetas = new ArrayList<>();
        String sql = "SELECT id FROM recetas WHERE estado = ? ORDER BY fecha_confeccion DESC";
        
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, estado);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String id = rs.getString("id");
                Receta receta = buscarPorId(id);
                if (receta != null) {
                    recetas.add(receta);
                }
            }
        }
        return recetas;
    }
}