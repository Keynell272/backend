package server;

import service.Service;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Maneja la comunicación con un cliente específico
 * Cada ClientHandler corre en su propio thread
 */
public class ClientHandler implements Runnable {
    
    private Socket socket;
    private Server server;
    private Service service;
    private BufferedReader entrada;
    private PrintWriter salida;
    private String usuarioId;
    private boolean activo;
    
    public ClientHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
        this.service = new Service();
        this.activo = true;
    }
    
    @Override
    public void run() {
        try {
            // Configurar streams de entrada/salida
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida = new PrintWriter(socket.getOutputStream(), true);
            
            System.out.println("ClientHandler iniciado para: " + socket.getInetAddress());
            
            // Ciclo principal de lectura de mensajes
            String mensaje;
            while (activo && (mensaje = entrada.readLine()) != null) {
                
                System.out.println("Mensaje recibido: " + mensaje);
                
                // Procesar el mensaje y obtener respuesta
                String respuesta = procesarMensaje(mensaje);
                
                // Enviar respuesta al cliente
                if (respuesta != null) {
                    salida.println(respuesta);
                    System.out.println("Respuesta enviada: " + respuesta);
                }
            }
            
        } catch (IOException e) {
            if (activo) {
                System.err.println("Error en ClientHandler: " + e.getMessage());
            }
        } finally {
            cerrar();
        }
    }
    
    /**
     * Procesa un mensaje recibido del cliente
     */
    private String procesarMensaje(String mensaje) {
        try {
            // El mensaje viene en formato JSON (lo veremos en Protocol)
            // Por ahora, delegar al Service
            String respuesta = service.procesarSolicitud(mensaje, this);
            return respuesta;
            
        } catch (Exception e) {
            System.err.println("Error al procesar mensaje: " + e.getMessage());
            e.printStackTrace();
            return "{\"status\":\"ERROR\",\"message\":\"" + e.getMessage() + "\"}";
        }
    }
    
    /**
     * Envía una notificación asíncrona al cliente
     */
    public void enviarNotificacion(String notificacion) {
        if (salida != null && activo) {
            salida.println(notificacion);
            System.out.println("Notificación enviada a " + usuarioId + ": " + notificacion);
        }
    }
    
    /**
     * Cierra la conexión con el cliente
     */
    public void cerrar() {
        activo = false;
        
        // Si había un usuario logueado, hacer logout
        if (usuarioId != null) {
            try {
                service.registrarLogout(usuarioId);
                
                // Notificar a otros clientes
                String notificacion = server.getNotificationManager()
                    .crearNotificacionLogout(usuarioId);
                server.notificarATodosExcepto(notificacion, this);
                
            } catch (Exception e) {
                System.err.println("Error al hacer logout: " + e.getMessage());
            }
        }
        
        // Cerrar streams
        try {
            if (entrada != null) entrada.close();
            if (salida != null) salida.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("Error al cerrar conexión: " + e.getMessage());
        }
        
        // Remover de la lista de clientes del servidor
        server.removerCliente(this);
        
        System.out.println("Conexión cerrada para usuario: " + usuarioId);
    }
    
    /**
     * Getters y Setters
     */
    public String getUsuarioId() {
        return usuarioId;
    }
    
    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }
    
    public Server getServer() {
        return server;
    }
    
    public Socket getSocket() {
        return socket;
    }
}