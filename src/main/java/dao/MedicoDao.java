package dao;

import config.Database;
import model.Medico;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicoDao {
    
    /**
     * Lista todos los médicos
     */
    public List<Medico> listarTodos() throws SQLException {
        List<Medico> medicos = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE rol = 'MED'";
        
        try (Connection conn = Database.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String id = rs.getString("id");
                String clave = rs.getString("clave");
                String nombre = rs.getString("nombre");
                String especialidad = rs.getString("especialidad");
                
                medicos.add(new Medico(id, clave, nombre, especialidad));
            }
        }
        return medicos;
    }
    
    /**
     * Busca un médico por ID
     */
    public Medico buscarPorId(String id) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE id = ? AND rol = 'MED'";
        
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String clave = rs.getString("clave");
                String nombre = rs.getString("nombre");
                String especialidad = rs.getString("especialidad");
                
                return new Medico(id, clave, nombre, especialidad);
            }
        }
        return null;
    }
}