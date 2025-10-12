package dao;

import config.Database;
import model.Farmaceuta;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FarmaceutaDao {
    
    /**
     * Lista todos los farmaceutas
     */
    public List<Farmaceuta> listarTodos() throws SQLException {
        List<Farmaceuta> farmaceutas = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE rol = 'FAR'";
        
        try (Connection conn = Database.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String id = rs.getString("id");
                String clave = rs.getString("clave");
                String nombre = rs.getString("nombre");
                
                farmaceutas.add(new Farmaceuta(id, clave, nombre));
            }
        }
        return farmaceutas;
    }
    
    /**
     * Busca un farmaceuta por ID
     */
    public Farmaceuta buscarPorId(String id) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE id = ? AND rol = 'FAR'";
        
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String clave = rs.getString("clave");
                String nombre = rs.getString("nombre");
                
                return new Farmaceuta(id, clave, nombre);
            }
        }
        return null;
    }
}
