package dao;

import config.Database;
import model.DetalleReceta;
import model.Medicamento;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetalleRecetaDao {
    
    private MedicamentoDao medicamentoDao = new MedicamentoDao();
    
    /**
     * Inserta un detalle de receta (usado internamente por RecetaDao)
     */
    public boolean insertar(String recetaId, DetalleReceta detalle, Connection conn) throws SQLException {
        String sql = "INSERT INTO detalle_recetas (receta_id, medicamento_codigo, cantidad, indicaciones, duracion_dias) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, recetaId);
            stmt.setString(2, detalle.getMedicamento().getCodigo());
            stmt.setInt(3, detalle.getCantidad());
            stmt.setString(4, detalle.getIndicaciones());
            stmt.setInt(5, detalle.getDuracionDias());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Busca todos los detalles de una receta
     */
    public List<DetalleReceta> buscarPorReceta(String recetaId) throws SQLException {
        List<DetalleReceta> detalles = new ArrayList<>();
        String sql = "SELECT * FROM detalle_recetas WHERE receta_id = ?";
        
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, recetaId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String medicamentoCodigo = rs.getString("medicamento_codigo");
                int cantidad = rs.getInt("cantidad");
                String indicaciones = rs.getString("indicaciones");
                int duracionDias = rs.getInt("duracion_dias");
                
                Medicamento medicamento = medicamentoDao.buscarPorCodigo(medicamentoCodigo);
                
                if (medicamento != null) {
                    DetalleReceta detalle = new DetalleReceta(id, medicamento, cantidad, indicaciones, duracionDias);
                    detalles.add(detalle);
                }
            }
        }
        return detalles;
    }
}