package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Servidor principal que escucha conexiones de clientes
 * Maneja múltiples clientes concurrentemente usando threads
 */
public class Server {
    
    private static final int PORT = 5000;
    private ServerSocket serverSocket;
    private boolean running;
    private List<ClientHandler> clientes;
    private NotificationManager notificationManager;
    
    public Server() {
        clientes = new ArrayList<>();
        notificationManager = new NotificationManager();
    }
    
    /**
     * Inicia el servidor
     */
    public void iniciar() {
        try {
            serverSocket = new ServerSocket(PORT);
            running = true;
            
            System.out.println("=================================");
            System.out.println("Servidor iniciado en puerto " + PORT);
            System.out.println("Esperando conexiones...");
            System.out.println("=================================");
            
            // Ciclo infinito para aceptar clientes
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    
                    System.out.println("Nueva conexión desde: " + clientSocket.getInetAddress().getHostAddress());
                    
                    // Crear un handler para este cliente
                    ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                    
                    // Agregar a la lista de clientes
                    synchronized (clientes) {
                        clientes.add(clientHandler);
                    }
                    
                    // Iniciar el thread del cliente
                    Thread clientThread = new Thread(clientHandler);
                    clientThread.start();
                    
                    System.out.println("Total clientes conectados: " + clientes.size());
                    
                } catch (IOException e) {
                    if (running) {
                        System.err.println("Error al aceptar cliente: " + e.getMessage());
                    }
                }
            }
            
        } catch (IOException e) {
            System.err.println("Error al iniciar servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Detiene el servidor
     */
    public void detener() {
        running = false;
        
        // Cerrar todas las conexiones de clientes
        synchronized (clientes) {
            for (ClientHandler cliente : clientes) {
                cliente.cerrar();
            }
            clientes.clear();
        }
        
        // Cerrar el server socket
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error al cerrar servidor: " + e.getMessage());
        }
        
        System.out.println("Servidor detenido");
    }
    
    /**
     * Elimina un cliente de la lista
     */
    public void removerCliente(ClientHandler cliente) {
        synchronized (clientes) {
            clientes.remove(cliente);
            System.out.println("Cliente desconectado. Total clientes: " + clientes.size());
        }
    }
    
    /**
     * Obtiene el NotificationManager
     */
    public NotificationManager getNotificationManager() {
        return notificationManager;
    }
    
    /**
     * Envía una notificación a todos los clientes conectados
     */
    public void notificarATodos(String notificacion) {
        synchronized (clientes) {
            for (ClientHandler cliente : clientes) {
                cliente.enviarNotificacion(notificacion);
            }
        }
    }
    
    /**
     * Envía una notificación a todos excepto al remitente
     */
    public void notificarATodosExcepto(String notificacion, ClientHandler remitente) {
        synchronized (clientes) {
            for (ClientHandler cliente : clientes) {
                if (cliente != remitente) {
                    cliente.enviarNotificacion(notificacion);
                }
            }
        }
    }
    
    /**
     * Envía una notificación a un cliente específico
     */
    public void notificarACliente(String usuarioId, String notificacion) {
        synchronized (clientes) {
            for (ClientHandler cliente : clientes) {
                if (cliente.getUsuarioId() != null && cliente.getUsuarioId().equals(usuarioId)) {
                    cliente.enviarNotificacion(notificacion);
                    break;
                }
            }
        }
    }
    
    /**
     * Obtiene la lista de clientes conectados
     */
    public List<ClientHandler> getClientes() {
        synchronized (clientes) {
            return new ArrayList<>(clientes);
        }
    }
}