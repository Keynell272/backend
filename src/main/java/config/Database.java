package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase Singleton para manejar la conexión a la base de datos MySQL
 * Proporciona una única instancia de conexión reutilizable
 */
public class Database {
    
    // Instancia única (Singleton)
    private static Database instance;
    
    // Parámetros de conexión
    private static final String URL = "jdbc:mysql://localhost:3306/hospital_db";
    private static final String USER = "user";
    private static final String PASSWORD = "V7p!qT9#xL2@eR4"; // Cambiar según tu configuración
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    
    // Conexión
    private Connection connection;
    
    /**
     * Constructor privado para implementar Singleton
     */
    private Database() {
        try {
            // Cargar el driver de MySQL
            Class.forName(DRIVER);
            System.out.println("Driver MySQL cargado correctamente");
        } catch (ClassNotFoundException e) {
            System.err.println("Error al cargar el driver de MySQL: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Obtiene la instancia única de Database (Singleton)
     * @return instancia de Database
     */
    public static Database getInstance() {
        if (instance == null) {
            synchronized (Database.class) {
                if (instance == null) {
                    instance = new Database();
                }
            }
        }
        return instance;
    }
    
    /**
     * Obtiene una conexión a la base de datos
     * Si la conexión está cerrada o es nula, crea una nueva
     * @return Connection objeto de conexión a MySQL
     * @throws SQLException si hay error en la conexión
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Conexión a base de datos establecida");
            } catch (SQLException e) {
                System.err.println("Error al conectar con la base de datos: " + e.getMessage());
                throw e;
            }
        }
        return connection;
    }
    
    /**
     * Cierra la conexión a la base de datos
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Conexión a base de datos cerrada");
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
    
    /**
     * Verifica si la conexión está activa
     * @return true si la conexión está activa, false en caso contrario
     */
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
    
    /**
     * Método para probar la conexión
     * @return true si la conexión es exitosa
     */
    public boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Test de conexión fallido: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Configura los parámetros de conexión (útil para configuración dinámica)
     * @param url URL de la base de datos
     * @param user usuario de la base de datos
     * @param password contraseña de la base de datos
     */
    public void configure(String url, String user, String password) {
        // Cerrar conexión existente si hay una
        closeConnection();
        
        // Nota: Para implementar esto correctamente, necesitarías hacer
        // que URL, USER y PASSWORD no sean final y manejarlos de otra forma
        System.out.println("Para configuración dinámica, modifica las constantes en el código");
    }
}