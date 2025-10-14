package server;

import org.json.JSONObject;

/**
 * Gestiona la creación de notificaciones asíncronas
 * para enviar a los clientes
 */
public class NotificationManager {
    
    /**
     * Crea una notificación de login
     */
    public String crearNotificacionLogin(String usuarioId, String nombre, String rol) {
        JSONObject json = new JSONObject();
        json.put("type", "NOTIFICATION");
        json.put("action", "USER_LOGIN");
        
        JSONObject data = new JSONObject();
        data.put("usuarioId", usuarioId);
        data.put("nombre", nombre);
        data.put("rol", rol);
        
        json.put("data", data);
        
        return json.toString();
    }
    
    /**
     * Crea una notificación de logout
     */
    public String crearNotificacionLogout(String usuarioId) {
        JSONObject json = new JSONObject();
        json.put("type", "NOTIFICATION");
        json.put("action", "USER_LOGOUT");
        
        JSONObject data = new JSONObject();
        data.put("usuarioId", usuarioId);
        
        json.put("data", data);
        
        return json.toString();
    }
    
    /**
     * Crea una notificación de nuevo mensaje
     */
    public String crearNotificacionMensaje(String remitenteId, String remitenteNombre, 
                                          String destinatarioId, String texto) {
        JSONObject json = new JSONObject();
        json.put("type", "NOTIFICATION");
        json.put("action", "NEW_MESSAGE");
        
        JSONObject data = new JSONObject();
        data.put("remitenteId", remitenteId);
        data.put("remitenteNombre", remitenteNombre);
        data.put("destinatarioId", destinatarioId);
        data.put("texto", texto);
        
        json.put("data", data);
        
        return json.toString();
    }
    
    /**
     * Crea una notificación genérica
     */
    public String crearNotificacion(String action, JSONObject data) {
        JSONObject json = new JSONObject();
        json.put("type", "NOTIFICATION");
        json.put("action", action);
        json.put("data", data);
        
        return json.toString();
    }
}
