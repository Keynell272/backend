package dao;

import config.Database;
import model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecetaDao {
    
    private DetalleRecetaDao detalleDao = new DetalleRecetaDao();
    private PacienteDao pacienteDao = new PacienteDao();
    
    public boolean insertar(Receta receta) throws SQLException {
        String sql = "INSERT INTO recetas (id, fecha_confeccion, fecha_retiro, estado, paciente_id, medico_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        Connection conn = Database.getInstance().getConnection();
        
        try {
            conn.setAutoCommit(false);
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, receta.getId());
                stmt.setTimestamp(2, new Timestamp(receta.getFechaConfeccion().getTime()));
                stmt.setTimestamp(3, new Timestamp(receta.getFechaRetiro().getTime()));
                stmt.setString(4, receta.getEstado());
                stmt.setString(5, receta.getPaciente().getId());
                stmt.setString(6, receta.getMedicoId());
                
                stmt.executeUpdate();
            }
            
            for (DetalleReceta detalle : receta.getDetalles()) {
                detalleDao.insertar(receta.getId(), detalle, conn);
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }
    
    public Receta buscarPorId(String id) throws SQLException {
        String sql = "SELECT * FROM recetas WHERE id = ?";
        
        String recetaId = null;
        java.util.Date fechaConfeccion = null;
        java.util.Date fechaRetiro = null;
        String pacienteId = null;
        String medicoId = null;
        String estado = null;
        java.util.Date fechaProceso = null;
        java.util.Date fechaLista = null;
        java.util.Date fechaEntrega = null;
        
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                recetaId = rs.getString("id");
                fechaConfeccion = new java.util.Date(rs.getTimestamp("fecha_confeccion").getTime());
                fechaRetiro = new java.util.Date(rs.getTimestamp("fecha_retiro").getTime());
                pacienteId = rs.getString("paciente_id");
                medicoId = rs.getString("medico_id");
                estado = rs.getString("estado");
                
                Timestamp tsProceso = rs.getTimestamp("fecha_proceso");
                if (tsProceso != null) {
                    fechaProceso = new java.util.Date(tsProceso.getTime());
                }
                
                Timestamp tsLista = rs.getTimestamp("fecha_lista");
                if (tsLista != null) {
                    fechaLista = new java.util.Date(tsLista.getTime());
                }
                
                Timestamp tsEntrega = rs.getTimestamp("fecha_entrega");
                if (tsEntrega != null) {
                    fechaEntrega = new java.util.Date(tsEntrega.getTime());
                }
            } else {
                return null;
            }
        }
        
        Paciente paciente = pacienteDao.buscarPorId(pacienteId);
        if (paciente == null) {
            return null;
        }
        
        Receta receta = new Receta(recetaId, fechaConfeccion, fechaRetiro, paciente);
        receta.setEstado(estado);
        receta.setMedicoId(medicoId);
        
        if (fechaProceso != null) {
            receta.setFechaProceso(fechaProceso);
        }
        if (fechaLista != null) {
            receta.setFechaLista(fechaLista);
        }
        if (fechaEntrega != null) {
            receta.setFechaEntrega(fechaEntrega);
        }
        
        List<DetalleReceta> detalles = detalleDao.buscarPorReceta(recetaId);
        receta.setDetalles(detalles);
        
        return receta;
    }
    
    public boolean actualizarEstado(String id, String nuevoEstado) throws SQLException {
        String sql = "UPDATE recetas SET estado = ?, ";
        
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
                sql += "estado = estado ";
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
    
    public List<Receta> listarTodas() throws SQLException {
        List<String> ids = new ArrayList<>();
        String sql = "SELECT id FROM recetas ORDER BY fecha_confeccion DESC";
        
        try (Connection conn = Database.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                ids.add(rs.getString("id"));
            }
        }
        
        List<Receta> recetas = new ArrayList<>();
        for (String id : ids) {
            Receta receta = buscarPorId(id);
            if (receta != null) {
                recetas.add(receta);
            }
        }
        
        return recetas;
    }
    
    public List<Receta> listarPorEstado(String estado) throws SQLException {
        List<String> ids = new ArrayList<>();
        String sql = "SELECT id FROM recetas WHERE estado = ? ORDER BY fecha_confeccion DESC";
        
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, estado);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                ids.add(rs.getString("id"));
            }
        }
        
        List<Receta> recetas = new ArrayList<>();
        for (String id : ids) {
            Receta receta = buscarPorId(id);
            if (receta != null) {
                recetas.add(receta);
            }
        }
        
        return recetas;
    }
}