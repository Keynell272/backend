package dao;

import config.Database;
import model.Paciente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PacienteDao {
    
    /**
     * Inserta un nuevo paciente
     */
    public boolean insertar(Paciente paciente) throws SQLException {
        String sql = "INSERT INTO pacientes (id, nombre, fecha_nacimiento, telefono) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, paciente.getId());
            stmt.setString(2, paciente.getNombre());
            stmt.setDate(3, new java.sql.Date(paciente.getFechaNacimiento().getTime()));
            stmt.setString(4, paciente.getTelefono());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Busca un paciente ACTIVO por ID
     */
    public Paciente buscarPorId(String id) throws SQLException {
        String sql = "SELECT * FROM pacientes WHERE id = ? AND estado = 'activo'";
        
        try (Connection conn = Database.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String nombre = rs.getString("nombre");
                java.util.Date fechaNacimiento = new java.util.Date(rs.getDate("fecha_nacimiento").getTime());
                String telefono = rs.getString("telefono");
                
                return new Paciente(id, nombre, fechaNacimiento, telefono);
            }
        }
        return null;
    }
    
    /**
     * Actualiza un paciente
     */
    public boolean actualizar(Paciente paciente) throws SQLException {
        String sql = "UPDATE pacientes SET nombre = ?, fecha_nacimiento = ?, telefono = ? WHERE id = ?";
        
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, paciente.getNombre());
            stmt.setDate(2, new java.sql.Date(paciente.getFechaNacimiento().getTime()));
            stmt.setString(3, paciente.getTelefono());
            stmt.setString(4, paciente.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Lista todos los pacientes ACTIVOS
     */
    public List<Paciente> listarTodos() throws SQLException {
        List<Paciente> pacientes = new ArrayList<>();
        String sql = "SELECT * FROM pacientes WHERE estado = 'activo'";
        
        try (Connection conn = Database.getInstance().getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String id = rs.getString("id");
                String nombre = rs.getString("nombre");
                java.util.Date fechaNacimiento = new java.util.Date(rs.getDate("fecha_nacimiento").getTime());
                String telefono = rs.getString("telefono");
                
                pacientes.add(new Paciente(id, nombre, fechaNacimiento, telefono));
            }
        }
        return pacientes;
    }

    /**
     * Desactiva un paciente (no lo elimina fisicamente)
     */
    public boolean eliminar(String id) throws SQLException {
        String sql = "UPDATE pacientes SET estado = 'inactivo' WHERE id = ?";
        
        try (Connection conn = Database.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Busca un paciente (ACTIVO o INACTIVO) por ID
     * Usado para consultar histórico de recetas
     */
    public Paciente buscarPorIdSinFiltro(String id) throws SQLException {
        String sql = "SELECT * FROM pacientes WHERE id = ?";
        
        try (Connection conn = Database.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String nombre = rs.getString("nombre");
                java.util.Date fechaNacimiento = new java.util.Date(rs.getDate("fecha_nacimiento").getTime());
                String telefono = rs.getString("telefono");
                
                return new Paciente(id, nombre, fechaNacimiento, telefono);
            }
        }
        return null;
    }
}