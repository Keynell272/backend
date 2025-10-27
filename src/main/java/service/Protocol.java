package service;

/**
 * Define el protocolo de comunicación entre Frontend y Backend
 * Contiene las constantes de acciones y formato de mensajes
 * 
 * IMPORTANTE: Esta clase debe ser IDÉNTICA en Frontend y Backend
 */
public class Protocol {

    // ==================== TIPOS DE MENSAJE ====================
    public static final String TYPE_REQUEST = "REQUEST";
    public static final String TYPE_RESPONSE = "RESPONSE";
    public static final String TYPE_NOTIFICATION = "NOTIFICATION";
    
    // ==================== ESTADOS DE RESPUESTA ====================
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_ERROR = "ERROR";
    
    // ==================== ACCIONES - AUTENTICACIÓN ====================
    public static final String ACTION_LOGIN = "LOGIN";
    public static final String ACTION_LOGOUT = "LOGOUT";
    public static final String ACTION_CAMBIAR_CLAVE = "CAMBIAR_CLAVE";
    
    // ==================== ACCIONES - PRESCRIPCIÓN ====================
    public static final String ACTION_CREAR_RECETA = "CREAR_RECETA";
    public static final String ACTION_BUSCAR_RECETA = "BUSCAR_RECETA";
    public static final String ACTION_LISTAR_RECETAS = "LISTAR_RECETAS";
    
    // ==================== ACCIONES - DESPACHO ====================
    public static final String ACTION_INICIAR_DESPACHO = "INICIAR_DESPACHO";
    public static final String ACTION_MARCAR_LISTA = "MARCAR_LISTA";
    public static final String ACTION_ENTREGAR_RECETA = "ENTREGAR_RECETA";
    public static final String ACTION_LISTAR_RECETAS_ESTADO = "LISTAR_RECETAS_ESTADO";
    
    // ==================== ACCIONES - LISTAS ====================
    public static final String ACTION_LISTAR_MEDICOS = "LISTAR_MEDICOS";
    public static final String ACTION_LISTAR_FARMACEUTAS = "LISTAR_FARMACEUTAS";
    public static final String ACTION_LISTAR_PACIENTES = "LISTAR_PACIENTES";
    public static final String ACTION_LISTAR_MEDICAMENTOS = "LISTAR_MEDICAMENTOS";
    
    // ==================== ACCIONES - CATÁLOGO ====================
    public static final String ACTION_AGREGAR_MEDICAMENTO = "AGREGAR_MEDICAMENTO";
    public static final String ACTION_ACTUALIZAR_MEDICAMENTO = "ACTUALIZAR_MEDICAMENTO";
    public static final String ACTION_BUSCAR_MEDICAMENTO = "BUSCAR_MEDICAMENTO";
    
    // ==================== ACCIONES - PACIENTES ====================
    public static final String ACTION_AGREGAR_PACIENTE = "AGREGAR_PACIENTE";
    public static final String ACTION_ACTUALIZAR_PACIENTE = "ACTUALIZAR_PACIENTE";
    public static final String ACTION_BUSCAR_PACIENTE = "BUSCAR_PACIENTE";
    
    // ==================== ACCIONES - DASHBOARD ====================
    public static final String ACTION_OBTENER_ESTADISTICAS = "OBTENER_ESTADISTICAS";
    public static final String ACTION_OBTENER_HISTORICO = "OBTENER_HISTORICO";
    
    // ==================== ACCIONES - USUARIOS ====================
    public static final String ACTION_LISTAR_USUARIOS = "LISTAR_USUARIOS";
    public static final String ACTION_AGREGAR_USUARIO = "AGREGAR_USUARIO";
    public static final String ACTION_LISTAR_USUARIOS_ACTIVOS = "LISTAR_USUARIOS_ACTIVOS";
    
    // ==================== ACCIONES - MENSAJERÍA ====================
    public static final String ACTION_ENVIAR_MENSAJE = "ENVIAR_MENSAJE";
    public static final String ACTION_RECIBIR_MENSAJES = "RECIBIR_MENSAJES";
    public static final String ACTION_MARCAR_MENSAJE_LEIDO = "MARCAR_MENSAJE_LEIDO";
    public static final String ACTION_CONTAR_MENSAJES_NO_LEIDOS = "CONTAR_MENSAJES_NO_LEIDOS";
    
    // ==================== ACCIONES - ELIMINAR ====================
    public static final String ACTION_ELIMINAR_USUARIO = "eliminarUsuario";
    public static final String ACTION_ELIMINAR_PACIENTE = "eliminarPaciente";
    public static final String ACTION_ELIMINAR_MEDICAMENTO = "ELIMINAR_MEDICAMENTO";

    // ==================== NOTIFICACIONES ASÍNCRONAS ====================
    public static final String NOTIFICATION_USER_LOGIN = "USER_LOGIN";
    public static final String NOTIFICATION_USER_LOGOUT = "USER_LOGOUT";
    public static final String NOTIFICATION_NEW_MESSAGE = "NEW_MESSAGE";
    
    // ==================== CAMPOS DE DATOS ====================
    public static final String FIELD_USUARIO_ID = "usuarioId";
    public static final String FIELD_CLAVE = "clave";
    public static final String FIELD_NOMBRE = "nombre";
    public static final String FIELD_ROL = "rol";
    public static final String FIELD_ESPECIALIDAD = "especialidad";
    
    public static final String FIELD_PACIENTE_ID = "pacienteId";
    public static final String FIELD_FECHA_NACIMIENTO = "fechaNacimiento";
    public static final String FIELD_TELEFONO = "telefono";
    
    public static final String FIELD_MEDICAMENTO_CODIGO = "medicamentoCodigo";
    public static final String FIELD_PRESENTACION = "presentacion";
    
    public static final String FIELD_RECETA_ID = "recetaId";
    public static final String FIELD_FECHA_CONFECCION = "fechaConfeccion";
    public static final String FIELD_FECHA_RETIRO = "fechaRetiro";
    public static final String FIELD_ESTADO = "estado";
    public static final String FIELD_MEDICO_ID = "medicoId";
    public static final String FIELD_DETALLES = "detalles";
    
    public static final String FIELD_CANTIDAD = "cantidad";
    public static final String FIELD_INDICACIONES = "indicaciones";
    public static final String FIELD_DURACION_DIAS = "duracionDias";
    
    public static final String FIELD_REMITENTE_ID = "remitenteId";
    public static final String FIELD_REMITENTE_NOMBRE = "remitenteNombre";
    public static final String FIELD_DESTINATARIO_ID = "destinatarioId";
    public static final String FIELD_DESTINATARIO_NOMBRE = "destinatarioNombre";
    public static final String FIELD_TEXTO = "texto";
    public static final String FIELD_MENSAJE_ID = "mensajeId";
    
    // ==================== ESTADOS DE RECETA ====================
    public static final String ESTADO_CONFECCIONADA = "confeccionada";
    public static final String ESTADO_PROCESO = "proceso";
    public static final String ESTADO_LISTA = "lista";
    public static final String ESTADO_ENTREGADA = "entregada";
    
    // ==================== FORMATO DE MENSAJE ====================
    /*
     * Formato JSON de REQUEST:
     * {
     *   "type": "REQUEST",
     *   "action": "ACTION_NAME",
     *   "data": {
     *     // datos específicos según la acción
     *   }
     * }
     * 
     * Formato JSON de RESPONSE:
     * {
     *   "type": "RESPONSE",
     *   "status": "SUCCESS" o "ERROR",
     *   "message": "mensaje descriptivo",
     *   "data": {
     *     // datos de respuesta
     *   }
     * }
     * 
     * Formato JSON de NOTIFICATION:
     * {
     *   "type": "NOTIFICATION",
     *   "action": "NOTIFICATION_NAME",
     *   "data": {
     *     // datos de la notificación
     *   }
     * }
     */
    
    // ==================== MÉTODOS AUXILIARES ====================
    
    /**
     * Crea un mensaje de request básico
     */
    public static String crearRequest(String action) {
        return String.format("{\"type\":\"%s\",\"action\":\"%s\",\"data\":{}}", 
                           TYPE_REQUEST, action);
    }
    
    /**
     * Crea un mensaje de respuesta exitosa
     */
    public static String crearResponse(boolean success, String message) {
        String status = success ? STATUS_SUCCESS : STATUS_ERROR;
        return String.format("{\"type\":\"%s\",\"status\":\"%s\",\"message\":\"%s\",\"data\":{}}", 
                           TYPE_RESPONSE, status, message);
    }
    
    /**
     * Crea un mensaje de respuesta con datos
     */
    public static String crearResponseConDatos(boolean success, String message, String dataJson) {
        String status = success ? STATUS_SUCCESS : STATUS_ERROR;
        return String.format("{\"type\":\"%s\",\"status\":\"%s\",\"message\":\"%s\",\"data\":%s}", 
                           TYPE_RESPONSE, status, message, dataJson);
    }
    
    /**
     * Constructor privado para evitar instanciación
     */
    private Protocol() {
        // Clase de constantes, no se debe instanciar
    }
}
