package main;

import config.Database;
import server.Server;

/**
 * Clase principal para iniciar el servidor Backend
 */
public class ServerMain {
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║  SISTEMA PRESCRIPCIÓN Y DESPACHO RECETAS  ║");
        System.out.println("║            BACKEND SERVER                  ║");
        System.out.println("╚════════════════════════════════════════════╝");
        System.out.println();
        
        // Probar conexión a base de datos
        System.out.println("Probando conexión a base de datos...");
        Database db = Database.getInstance();
        
        if (db.testConnection()) {
            System.out.println("✓ Conexión a base de datos exitosa");
            System.out.println();
        } else {
            System.err.println("✗ Error: No se pudo conectar a la base de datos");
            System.err.println("Verifique:");
            System.err.println("  - MySQL está corriendo");
            System.err.println("  - La base de datos 'hospital_db' existe");
            System.err.println("  - Las credenciales son correctas");
            System.exit(1);
        }
        
        // Iniciar el servidor
        Server server = new Server();
        
        // Agregar shutdown hook para cerrar limpiamente
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nCerrando servidor...");
            server.detener();
            db.closeConnection();
            System.out.println("Servidor cerrado correctamente");
        }));
        
        // Iniciar servidor (bloquea el thread principal)
        server.iniciar();
    }
}