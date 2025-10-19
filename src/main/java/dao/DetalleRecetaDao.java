package dao;

import config.Database;
import model.DetalleReceta;
import model.Medicamento;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetalleRecetaDao {
    
    private MedicamentoDao medicamentoDao = new MedicamentoDao();
    
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
    
    public List<DetalleReceta> buscarPorReceta(String recetaId) throws SQLException {
        List<DetalleInfo> infos = new ArrayList<>();
        String sql = "SELECT * FROM detalle_recetas WHERE receta_id = ?";
        
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, recetaId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                DetalleInfo info = new DetalleInfo();
                info.id = rs.getInt("id");
                info.medicamentoCodigo = rs.getString("medicamento_codigo");
                info.cantidad = rs.getInt("cantidad");
                info.indicaciones = rs.getString("indicaciones");
                info.duracionDias = rs.getInt("duracion_dias");
                infos.add(info);
            }
        }
        
        List<DetalleReceta> detalles = new ArrayList<>();
        for (DetalleInfo info : infos) {
            Medicamento medicamento = medicamentoDao.buscarPorCodigo(info.medicamentoCodigo);
            if (medicamento != null) {
                DetalleReceta detalle = new DetalleReceta(info.id, medicamento, info.cantidad, 
                                                         info.indicaciones, info.duracionDias);
                detalles.add(detalle);
            }
        }
        
        return detalles;
    }
    
    private static class DetalleInfo {
        int id;
        String medicamentoCodigo;
        int cantidad;
        String indicaciones;
        int duracionDias;
    }
}