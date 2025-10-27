package dao;

import config.Database;
import model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDao {
    
    /**
     * Valida las credenciales de un usuario y retorna el objeto Usuario correspondiente
     */
    public Usuario validarLogin(String id, String clave) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE id = ? AND clave = ?";
        
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            stmt.setString(2, clave);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String rol = rs.getString("rol");
                String nombre = rs.getString("nombre");
                
                // Crear el tipo correcto de usuario según el rol
                switch (rol) {
                    case "ADM":
                        return new Administrador(id, clave, nombre);
                    case "MED":
                        String especialidad = rs.getString("especialidad");
                        return new Medico(id, clave, nombre, especialidad);
                    case "FAR":
                        return new Farmaceuta(id, clave, nombre);
                    default:
                        return null;
                }
            }
        }
        return null;
    }
    
    /**
     * Busca un usuario por su ID
     */
    public Usuario buscarPorId(String id) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String rol = rs.getString("rol");
                String clave = rs.getString("clave");
                String nombre = rs.getString("nombre");
                
                switch (rol) {
                    case "ADM":
                        return new Administrador(id, clave, nombre);
                    case "MED":
                        String especialidad = rs.getString("especialidad");
                        return new Medico(id, clave, nombre, especialidad);
                    case "FAR":
                        return new Farmaceuta(id, clave, nombre);
                }
            }
        }
        return null;
    }
    
    /**
     * Inserta un nuevo usuario
     */
    public boolean insertar(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (id, clave, nombre, rol, especialidad) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuario.getId());
            stmt.setString(2, usuario.getClave());
            stmt.setString(3, usuario.getNombre());
            stmt.setString(4, usuario.getRol());
            
            // Especialidad solo para médicos
            if (usuario instanceof Medico) {
                stmt.setString(5, ((Medico) usuario).getEspecialidad());
            } else {
                stmt.setNull(5, Types.VARCHAR);
            }
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Actualiza la clave de un usuario
     */
    public boolean actualizarClave(String id, String nuevaClave) throws SQLException {
        String sql = "UPDATE usuarios SET clave = ? WHERE id = ?";
        
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nuevaClave);
            stmt.setString(2, id);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Lista todos los usuarios
     */
    public List<Usuario> listarTodos() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";
        
        try (Connection conn = Database.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String id = rs.getString("id");
                String clave = rs.getString("clave");
                String nombre = rs.getString("nombre");
                String rol = rs.getString("rol");
                
                Usuario usuario = null;
                switch (rol) {
                    case "ADM":
                        usuario = new Administrador(id, clave, nombre);
                        break;
                    case "MED":
                        String especialidad = rs.getString("especialidad");
                        usuario = new Medico(id, clave, nombre, especialidad);
                        break;
                    case "FAR":
                        usuario = new Farmaceuta(id, clave, nombre);
                        break;
                }
                if (usuario != null) {
                    usuarios.add(usuario);
                }
            }
        }
        return usuarios;
    }

    public boolean eliminar(String id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        
        try (Connection conn = Database.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            
            return stmt.executeUpdate() > 0;
        }
    }
}