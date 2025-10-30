package dao;

import config.Database;
import model.Medicamento;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicamentoDao {
    
    /**
     * Inserta un nuevo medicamento
     */
    public boolean insertar(Medicamento medicamento) throws SQLException {
        String sql = "INSERT INTO medicamentos (codigo, nombre, presentacion) VALUES (?, ?, ?)";
        
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, medicamento.getCodigo());
            stmt.setString(2, medicamento.getNombre());
            stmt.setString(3, medicamento.getPresentacion());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    
    /**
     * Busca un medicamento ACTIVO por código
     */
    public Medicamento buscarPorCodigo(String codigo) throws SQLException {
        String sql = "SELECT * FROM medicamentos WHERE codigo = ? AND estado = 'activo'";
        
        try (Connection conn = Database.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, codigo);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String nombre = rs.getString("nombre");
                String presentacion = rs.getString("presentacion");
                
                return new Medicamento(codigo, nombre, presentacion);
            }
        }
        return null;
    }
    
    /**
     * Actualiza un medicamento
     */
    public boolean actualizar(Medicamento medicamento) throws SQLException {
        String sql = "UPDATE medicamentos SET nombre = ?, presentacion = ? WHERE codigo = ?";
        
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, medicamento.getNombre());
            stmt.setString(2, medicamento.getPresentacion());
            stmt.setString(3, medicamento.getCodigo());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Desactiva un medicamento (no lo elimina)
     */
    public boolean eliminar(String codigo) throws SQLException {
        String sql = "UPDATE medicamentos SET estado = 'inactivo' WHERE codigo = ?";
        
        try (Connection conn = Database.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, codigo);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Lista todos los medicamentos activos
     */
    public List<Medicamento> listarTodos() throws SQLException {
        List<Medicamento> medicamentos = new ArrayList<>();
        String sql = "SELECT * FROM medicamentos WHERE estado = 'activo'";
        
        try (Connection conn = Database.getInstance().getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                String presentacion = rs.getString("presentacion");
                
                medicamentos.add(new Medicamento(codigo, nombre, presentacion));
            }
        }
        return medicamentos;
    }
}