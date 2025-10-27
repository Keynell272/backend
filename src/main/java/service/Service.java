package service;

import dao.*;
import model.*;
import server.ClientHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Servicio principal que contiene la lógica de negocio
 * Procesa las solicitudes del Frontend y usa los DAOs para acceder a datos
 */
public class Service {
    
    // DAOs
    private UsuarioDao usuarioDao;
    private MedicoDao medicoDao;
    private FarmaceutaDao farmaceutaDao;
    private PacienteDao pacienteDao;
    private MedicamentoDao medicamentoDao;
    private RecetaDao recetaDao;
    private MensajeDao mensajeDao;
    private UsuarioActivoDao usuarioActivoDao;
    
    // Formato de fecha
    private SimpleDateFormat dateFormat;
    
    public Service() {
        usuarioDao = new UsuarioDao();
        medicoDao = new MedicoDao();
        farmaceutaDao = new FarmaceutaDao();
        pacienteDao = new PacienteDao();
        medicamentoDao = new MedicamentoDao();
        recetaDao = new RecetaDao();
        mensajeDao = new MensajeDao();
        usuarioActivoDao = new UsuarioActivoDao();
        
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
    
    /**
     * Procesa una solicitud recibida del cliente
     */
    public String procesarSolicitud(String mensajeJson, ClientHandler clientHandler) {
        try {
            JSONObject mensaje = new JSONObject(mensajeJson);
            String tipo = mensaje.getString("type");
            
            if (!tipo.equals(Protocol.TYPE_REQUEST)) {
                return crearRespuestaError("Tipo de mensaje inválido");
            }
            
            String action = mensaje.getString("action");
            JSONObject data = mensaje.optJSONObject("data");
            if (data == null) {
                data = new JSONObject();
            }
            
            // Procesar según la acción
            return procesarAccion(action, data, clientHandler);
            
        } catch (Exception e) {
            e.printStackTrace();
            return crearRespuestaError("Error al procesar solicitud: " + e.getMessage());
        }
    }
    
    /**
     * Procesa una acción específica
     */
    private String procesarAccion(String action, JSONObject data, ClientHandler clientHandler) {
        try {
            switch (action) {
                // AUTENTICACIÓN
                case Protocol.ACTION_LOGIN:
                    return procesarLogin(data, clientHandler);
                case Protocol.ACTION_LOGOUT:
                    return procesarLogout(data, clientHandler);
                case Protocol.ACTION_CAMBIAR_CLAVE:
                    return procesarCambiarClave(data);
                
                // PRESCRIPCIÓN
                case Protocol.ACTION_CREAR_RECETA:
                    return procesarCrearReceta(data);
                case Protocol.ACTION_BUSCAR_RECETA:
                    return procesarBuscarReceta(data);
                case Protocol.ACTION_LISTAR_RECETAS:
                    return procesarListarRecetas();
                
                // DESPACHO
                case Protocol.ACTION_INICIAR_DESPACHO:
                    return procesarIniciarDespacho(data);
                case Protocol.ACTION_MARCAR_LISTA:
                    return procesarMarcarLista(data);
                case Protocol.ACTION_ENTREGAR_RECETA:
                    return procesarEntregarReceta(data);
                case Protocol.ACTION_LISTAR_RECETAS_ESTADO:
                    return procesarListarRecetasEstado(data);
                
                // LISTAS
                case Protocol.ACTION_LISTAR_MEDICOS:
                    return procesarListarMedicos();
                case Protocol.ACTION_LISTAR_FARMACEUTAS:
                    return procesarListarFarmaceutas();
                case Protocol.ACTION_LISTAR_PACIENTES:
                    return procesarListarPacientes();
                case Protocol.ACTION_LISTAR_MEDICAMENTOS:
                    return procesarListarMedicamentos();
                
                // CATÁLOGO
                case Protocol.ACTION_AGREGAR_MEDICAMENTO:
                    return procesarAgregarMedicamento(data);
                case Protocol.ACTION_ACTUALIZAR_MEDICAMENTO:
                    return procesarActualizarMedicamento(data);
                case Protocol.ACTION_ELIMINAR_MEDICAMENTO:
                    return procesarEliminarMedicamento(data);
                case Protocol.ACTION_BUSCAR_MEDICAMENTO:
                    return procesarBuscarMedicamento(data);
                
                // PACIENTES
                case Protocol.ACTION_AGREGAR_PACIENTE:
                    return procesarAgregarPaciente(data);
                case Protocol.ACTION_ACTUALIZAR_PACIENTE:
                    return procesarActualizarPaciente(data);
                case Protocol.ACTION_BUSCAR_PACIENTE:
                    return procesarBuscarPaciente(data);
                case Protocol.ACTION_ELIMINAR_PACIENTE:
                    return procesarEliminarPaciente(data);
                
                // USUARIOS
                case Protocol.ACTION_LISTAR_USUARIOS:
                    return procesarListarUsuarios();
                case Protocol.ACTION_AGREGAR_USUARIO:
                    return procesarAgregarUsuario(data);
                case Protocol.ACTION_LISTAR_USUARIOS_ACTIVOS:
                    return procesarListarUsuariosActivos();
                case Protocol.ACTION_ELIMINAR_USUARIO:
                    return procesarEliminarUsuario(data);
                
                // MENSAJERÍA
                case Protocol.ACTION_ENVIAR_MENSAJE:
                    return procesarEnviarMensaje(data, clientHandler);
                case Protocol.ACTION_RECIBIR_MENSAJES:
                    return procesarRecibirMensajes(data);
                case Protocol.ACTION_MARCAR_MENSAJE_LEIDO:
                    return procesarMarcarMensajeLeido(data);
                case Protocol.ACTION_CONTAR_MENSAJES_NO_LEIDOS:
                    return procesarContarMensajesNoLeidos(data);
                
                default:
                    return crearRespuestaError("Acción no reconocida: " + action);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return crearRespuestaError("Error al procesar acción: " + e.getMessage());
        }
    }
    
    // ==================== AUTENTICACIÓN ====================
    
    private String procesarLogin(JSONObject data, ClientHandler clientHandler) {
        try {
            String usuarioId = data.getString(Protocol.FIELD_USUARIO_ID);
            String clave = data.getString(Protocol.FIELD_CLAVE);
            
            Usuario usuario = usuarioDao.validarLogin(usuarioId, clave);
            
            if (usuario != null) {
                // Registrar usuario como activo
                String ipAddress = clientHandler.getSocket().getInetAddress().getHostAddress();
                UsuarioActivo usuarioActivo = new UsuarioActivo(
                    usuarioId, usuario.getNombre(), usuario.getRol(), new Date(), ipAddress
                );
                usuarioActivoDao.registrarLogin(usuarioActivo);
                
                // Guardar el usuario en el ClientHandler
                clientHandler.setUsuarioId(usuarioId);
                
                // Crear respuesta con datos del usuario
                JSONObject respData = new JSONObject();
                respData.put(Protocol.FIELD_USUARIO_ID, usuario.getId());
                respData.put(Protocol.FIELD_NOMBRE, usuario.getNombre());
                respData.put(Protocol.FIELD_ROL, usuario.getRol());
                
                if (usuario instanceof Medico) {
                    respData.put(Protocol.FIELD_ESPECIALIDAD, ((Medico) usuario).getEspecialidad());
                }
                
                // Notificar a otros clientes del login
                String notificacion = clientHandler.getServer().getNotificationManager()
                    .crearNotificacionLogin(usuarioId, usuario.getNombre(), usuario.getRol());
                clientHandler.getServer().notificarATodosExcepto(notificacion, clientHandler);
                
                return crearRespuestaExito("Login exitoso", respData);
            } else {
                return crearRespuestaError("Usuario o contraseña incorrectos");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return crearRespuestaError("Error en login: " + e.getMessage());
        }
    }
    
    private String procesarLogout(JSONObject data, ClientHandler clientHandler) {
        try {
            String usuarioId = data.getString(Protocol.FIELD_USUARIO_ID);
            registrarLogout(usuarioId);
            
            // Notificar a otros clientes del logout
            String notificacion = clientHandler.getServer().getNotificationManager()
                .crearNotificacionLogout(usuarioId);
            clientHandler.getServer().notificarATodosExcepto(notificacion, clientHandler);
            
            return crearRespuestaExito("Logout exitoso");
        } catch (Exception e) {
            return crearRespuestaError("Error en logout: " + e.getMessage());
        }
    }
    
    public void registrarLogout(String usuarioId) {
        try {
            usuarioActivoDao.registrarLogout(usuarioId);
        } catch (Exception e) {
            System.err.println("Error al registrar logout: " + e.getMessage());
        }
    }
    
    private String procesarCambiarClave(JSONObject data) {
        try {
            String usuarioId = data.getString(Protocol.FIELD_USUARIO_ID);
            String nuevaClave = data.getString(Protocol.FIELD_CLAVE);
            
            boolean exito = usuarioDao.actualizarClave(usuarioId, nuevaClave);
            
            if (exito) {
                return crearRespuestaExito("Contraseña actualizada exitosamente");
            } else {
                return crearRespuestaError("No se pudo actualizar la contraseña");
            }
        } catch (Exception e) {
            return crearRespuestaError("Error al cambiar contraseña: " + e.getMessage());
        }
    }
    
    // ==================== LISTAS ====================
    
    private String procesarListarMedicos() {
        try {
            List<Medico> medicos = medicoDao.listarTodos();
            
            JSONArray medicosArray = new JSONArray();
            for (Medico medico : medicos) {
                JSONObject medicoJson = new JSONObject();
                medicoJson.put(Protocol.FIELD_USUARIO_ID, medico.getId());
                medicoJson.put(Protocol.FIELD_NOMBRE, medico.getNombre());
                medicoJson.put(Protocol.FIELD_ESPECIALIDAD, medico.getEspecialidad());
                medicosArray.put(medicoJson);
            }
            
            JSONObject respData = new JSONObject();
            respData.put("medicos", medicosArray);
            
            return crearRespuestaExito("Lista de médicos obtenida", respData);
        } catch (Exception e) {
            return crearRespuestaError("Error al listar médicos: " + e.getMessage());
        }
    }
    
    private String procesarListarFarmaceutas() {
        try {
            List<Farmaceuta> farmaceutas = farmaceutaDao.listarTodos();
            
            JSONArray farmaceutasArray = new JSONArray();
            for (Farmaceuta farm : farmaceutas) {
                JSONObject farmJson = new JSONObject();
                farmJson.put(Protocol.FIELD_USUARIO_ID, farm.getId());
                farmJson.put(Protocol.FIELD_NOMBRE, farm.getNombre());
                farmaceutasArray.put(farmJson);
            }
            
            JSONObject respData = new JSONObject();
            respData.put("farmaceutas", farmaceutasArray);
            
            return crearRespuestaExito("Lista de farmaceutas obtenida", respData);
        } catch (Exception e) {
            return crearRespuestaError("Error al listar farmaceutas: " + e.getMessage());
        }
    }
    
    private String procesarListarPacientes() {
        try {
            List<Paciente> pacientes = pacienteDao.listarTodos();
            
            JSONArray pacientesArray = new JSONArray();
            for (Paciente pac : pacientes) {
                JSONObject pacJson = pacienteToJson(pac);
                pacientesArray.put(pacJson);
            }
            
            JSONObject respData = new JSONObject();
            respData.put("pacientes", pacientesArray);
            
            return crearRespuestaExito("Lista de pacientes obtenida", respData);
        } catch (Exception e) {
            return crearRespuestaError("Error al listar pacientes: " + e.getMessage());
        }
    }
    
    private String procesarListarMedicamentos() {
        try {
            List<Medicamento> medicamentos = medicamentoDao.listarTodos();
            
            JSONArray medicamentosArray = new JSONArray();
            for (Medicamento med : medicamentos) {
                JSONObject medJson = medicamentoToJson(med);
                medicamentosArray.put(medJson);
            }
            
            JSONObject respData = new JSONObject();
            respData.put("medicamentos", medicamentosArray);
            
            return crearRespuestaExito("Lista de medicamentos obtenida", respData);
        } catch (Exception e) {
            return crearRespuestaError("Error al listar medicamentos: " + e.getMessage());
        }
    }
    
     
    private String procesarAgregarMedicamento(JSONObject data) {
        try {
            String codigo = data.getString(Protocol.FIELD_MEDICAMENTO_CODIGO);
            String nombre = data.getString(Protocol.FIELD_NOMBRE);
            String presentacion = data.getString(Protocol.FIELD_PRESENTACION);
            
            Medicamento medicamento = new Medicamento(codigo, nombre, presentacion);
            boolean exito = medicamentoDao.insertar(medicamento);
            
            if (exito) {
                return crearRespuestaExito("Medicamento agregado exitosamente");
            } else {
                return crearRespuestaError("No se pudo agregar el medicamento");
            }
        } catch (Exception e) {
            return crearRespuestaError("Error al agregar medicamento: " + e.getMessage());
        }
    }
    
    private String procesarActualizarMedicamento(JSONObject data) {
        try {
            String codigo = data.getString(Protocol.FIELD_MEDICAMENTO_CODIGO);
            String nombre = data.getString(Protocol.FIELD_NOMBRE);
            String presentacion = data.getString(Protocol.FIELD_PRESENTACION);
            
            Medicamento medicamento = new Medicamento(codigo, nombre, presentacion);
            boolean exito = medicamentoDao.actualizar(medicamento);
            
            if (exito) {
                return crearRespuestaExito("Medicamento actualizado exitosamente");
            } else {
                return crearRespuestaError("No se pudo actualizar el medicamento");
            }
        } catch (Exception e) {
            return crearRespuestaError("Error al actualizar medicamento: " + e.getMessage());
        }
    }
    
    private String procesarEliminarMedicamento(JSONObject data) {
        try {
            String codigo = data.getString(Protocol.FIELD_MEDICAMENTO_CODIGO);
            
            boolean exito = medicamentoDao.eliminar(codigo);
            
            if (exito) {
                return crearRespuestaExito("Medicamento desactivado exitosamente");
            } else {
                return crearRespuestaError("No se pudo desactivar el medicamento");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return crearRespuestaError("Error al desactivar medicamento: " + e.getMessage());
        }
    }
    
    private String procesarBuscarMedicamento(JSONObject data) {
        try {
            String codigo = data.getString(Protocol.FIELD_MEDICAMENTO_CODIGO);
            
            Medicamento medicamento = medicamentoDao.buscarPorCodigo(codigo);
            
            if (medicamento != null) {
                JSONObject respData = medicamentoToJson(medicamento);
                return crearRespuestaExito("Medicamento encontrado", respData);
            } else {
                return crearRespuestaError("Medicamento no encontrado");
            }
        } catch (Exception e) {
            return crearRespuestaError("Error al buscar medicamento: " + e.getMessage());
        }
    }
    
    // ==================== PACIENTES ====================
    
    private String procesarAgregarPaciente(JSONObject data) {
        try {
            String id = data.getString(Protocol.FIELD_PACIENTE_ID);
            String nombre = data.getString(Protocol.FIELD_NOMBRE);
            String telefono = data.getString(Protocol.FIELD_TELEFONO);
            String fechaNacStr = data.getString(Protocol.FIELD_FECHA_NACIMIENTO);
            
            Date fechaNacimiento = dateFormat.parse(fechaNacStr);
            
            Paciente paciente = new Paciente(id, nombre, fechaNacimiento, telefono);
            boolean exito = pacienteDao.insertar(paciente);
            
            if (exito) {
                return crearRespuestaExito("Paciente agregado exitosamente");
            } else {
                return crearRespuestaError("No se pudo agregar el paciente");
            }
        } catch (Exception e) {
            return crearRespuestaError("Error al agregar paciente: " + e.getMessage());
        }
    }
    
    private String procesarActualizarPaciente(JSONObject data) {
        try {
            String id = data.getString(Protocol.FIELD_PACIENTE_ID);
            String nombre = data.getString(Protocol.FIELD_NOMBRE);
            String telefono = data.getString(Protocol.FIELD_TELEFONO);
            String fechaNacStr = data.getString(Protocol.FIELD_FECHA_NACIMIENTO);
            
            Date fechaNacimiento = dateFormat.parse(fechaNacStr);
            
            Paciente paciente = new Paciente(id, nombre, fechaNacimiento, telefono);
            boolean exito = pacienteDao.actualizar(paciente);
            
            if (exito) {
                return crearRespuestaExito("Paciente actualizado exitosamente");
            } else {
                return crearRespuestaError("No se pudo actualizar el paciente");
            }
        } catch (Exception e) {
            return crearRespuestaError("Error al actualizar paciente: " + e.getMessage());
        }
    }
    
    private String procesarBuscarPaciente(JSONObject data) {
        try {
            String id = data.getString(Protocol.FIELD_PACIENTE_ID);
            
            Paciente paciente = pacienteDao.buscarPorId(id);
            
            if (paciente != null) {
                JSONObject respData = pacienteToJson(paciente);
                return crearRespuestaExito("Paciente encontrado", respData);
            } else {
                return crearRespuestaError("Paciente no encontrado");
            }
        } catch (Exception e) {
            return crearRespuestaError("Error al buscar paciente: " + e.getMessage());
        }
    }
    
    private String procesarEliminarPaciente(JSONObject data) {
        try {
            String id = data.getString(Protocol.FIELD_PACIENTE_ID);
            
            boolean exito = pacienteDao.eliminar(id);
            
            if (exito) {
                return crearRespuestaExito("Paciente eliminado exitosamente");
            } else {
                return crearRespuestaError("No se pudo eliminar el paciente");
            }
        } catch (Exception e) {
            return crearRespuestaError("Error al eliminar paciente: " + e.getMessage());
        }
    }

    // ==================== PRESCRIPCIÓN Y DESPACHO ====================
    
    private String procesarCrearReceta(JSONObject data) {
        try {
            String recetaId = data.getString(Protocol.FIELD_RECETA_ID);
            String fechaConfStr = data.getString(Protocol.FIELD_FECHA_CONFECCION);
            String fechaRetiroStr = data.getString(Protocol.FIELD_FECHA_RETIRO);
            String pacienteId = data.getString(Protocol.FIELD_PACIENTE_ID);
            String medicoId = data.getString(Protocol.FIELD_MEDICO_ID);
            JSONArray detallesArray = data.getJSONArray(Protocol.FIELD_DETALLES);
            
            Date fechaConfeccion = dateFormat.parse(fechaConfStr);
            Date fechaRetiro = dateFormat.parse(fechaRetiroStr);
            
            Paciente paciente = pacienteDao.buscarPorId(pacienteId);
            if (paciente == null) {
                return crearRespuestaError("Paciente no encontrado");
            }
            
            Receta receta = new Receta(recetaId, fechaConfeccion, fechaRetiro, paciente);
            receta.setMedicoId(medicoId);
            receta.setEstado(Protocol.ESTADO_CONFECCIONADA);
            
            // Agregar detalles
            for (int i = 0; i < detallesArray.length(); i++) {
                JSONObject detalleJson = detallesArray.getJSONObject(i);
                
                String medCodigo = detalleJson.getString(Protocol.FIELD_MEDICAMENTO_CODIGO);
                int cantidad = detalleJson.getInt(Protocol.FIELD_CANTIDAD);
                String indicaciones = detalleJson.getString(Protocol.FIELD_INDICACIONES);
                int duracionDias = detalleJson.getInt(Protocol.FIELD_DURACION_DIAS);
                
                Medicamento medicamento = medicamentoDao.buscarPorCodigo(medCodigo);
                if (medicamento == null) {
                    return crearRespuestaError("Medicamento no encontrado: " + medCodigo);
                }
                
                DetalleReceta detalle = new DetalleReceta(medicamento, cantidad, indicaciones, duracionDias);
                receta.agregarDetalle(detalle);
            }
            
            boolean exito = recetaDao.insertar(receta);
            
            if (exito) {
                return crearRespuestaExito("Receta creada exitosamente");
            } else {
                return crearRespuestaError("No se pudo crear la receta");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return crearRespuestaError("Error al crear receta: " + e.getMessage());
        }
    }
    
    private String procesarBuscarReceta(JSONObject data) {
        try {
            String recetaId = data.getString(Protocol.FIELD_RECETA_ID);
            
            Receta receta = recetaDao.buscarPorId(recetaId);
            
            if (receta != null) {
                JSONObject respData = recetaToJson(receta);
                return crearRespuestaExito("Receta encontrada", respData);
            } else {
                return crearRespuestaError("Receta no encontrada");
            }
        } catch (Exception e) {
            return crearRespuestaError("Error al buscar receta: " + e.getMessage());
        }
    }
    
    private String procesarListarRecetas() {
        try {
            List<Receta> recetas = recetaDao.listarTodas();
            
            JSONArray recetasArray = new JSONArray();
            for (Receta receta : recetas) {
                JSONObject recetaJson = recetaToJson(receta);
                recetasArray.put(recetaJson);
            }
            
            JSONObject respData = new JSONObject();
            respData.put("recetas", recetasArray);
            
            return crearRespuestaExito("Lista de recetas obtenida", respData);
        } catch (Exception e) {
            return crearRespuestaError("Error al listar recetas: " + e.getMessage());
        }
    }
    
    private String procesarListarRecetasEstado(JSONObject data) {
        try {
            String estado = data.getString(Protocol.FIELD_ESTADO);
            
            List<Receta> recetas = recetaDao.listarPorEstado(estado);
            
            JSONArray recetasArray = new JSONArray();
            for (Receta receta : recetas) {
                JSONObject recetaJson = recetaToJson(receta);
                recetasArray.put(recetaJson);
            }
            
            JSONObject respData = new JSONObject();
            respData.put("recetas", recetasArray);
            
            return crearRespuestaExito("Lista de recetas obtenida", respData);
        } catch (Exception e) {
            return crearRespuestaError("Error al listar recetas: " + e.getMessage());
        }
    }
    
    private String procesarIniciarDespacho(JSONObject data) {
        try {
            String recetaId = data.getString(Protocol.FIELD_RECETA_ID);
            
            boolean exito = recetaDao.actualizarEstado(recetaId, Protocol.ESTADO_PROCESO);
            
            if (exito) {
                return crearRespuestaExito("Despacho iniciado");
            } else {
                return crearRespuestaError("No se pudo iniciar el despacho");
            }
        } catch (Exception e) {
            return crearRespuestaError("Error al iniciar despacho: " + e.getMessage());
        }
    }
    
    private String procesarMarcarLista(JSONObject data) {
        try {
            String recetaId = data.getString(Protocol.FIELD_RECETA_ID);
            
            boolean exito = recetaDao.actualizarEstado(recetaId, Protocol.ESTADO_LISTA);
            
            if (exito) {
                return crearRespuestaExito("Receta marcada como lista");
            } else {
                return crearRespuestaError("No se pudo marcar la receta como lista");
            }
        } catch (Exception e) {
            return crearRespuestaError("Error al marcar receta lista: " + e.getMessage());
        }
    }
    
    private String procesarEntregarReceta(JSONObject data) {
        try {
            String recetaId = data.getString(Protocol.FIELD_RECETA_ID);
            
            boolean exito = recetaDao.actualizarEstado(recetaId, Protocol.ESTADO_ENTREGADA);
            
            if (exito) {
                return crearRespuestaExito("Receta entregada");
            } else {
                return crearRespuestaError("No se pudo entregar la receta");
            }
        } catch (Exception e) {
            return crearRespuestaError("Error al entregar receta: " + e.getMessage());
        }
    }
    
    // ==================== USUARIOS ====================
    
    private String procesarListarUsuarios() {
        try {
            List<Usuario> usuarios = usuarioDao.listarTodos();
            
            JSONArray usuariosArray = new JSONArray();
            for (Usuario usuario : usuarios) {
                JSONObject usuarioJson = new JSONObject();
                usuarioJson.put(Protocol.FIELD_USUARIO_ID, usuario.getId());
                usuarioJson.put(Protocol.FIELD_NOMBRE, usuario.getNombre());
                usuarioJson.put(Protocol.FIELD_ROL, usuario.getRol());
                
                if (usuario instanceof Medico) {
                    usuarioJson.put(Protocol.FIELD_ESPECIALIDAD, ((Medico) usuario).getEspecialidad());
                }
                
                usuariosArray.put(usuarioJson);
            }
            
            JSONObject respData = new JSONObject();
            respData.put("usuarios", usuariosArray);
            
            return crearRespuestaExito("Lista de usuarios obtenida", respData);
        } catch (Exception e) {
            return crearRespuestaError("Error al listar usuarios: " + e.getMessage());
        }
    }
    
    private String procesarAgregarUsuario(JSONObject data) {
        try {
            String id = data.getString(Protocol.FIELD_USUARIO_ID);
            String clave = data.getString(Protocol.FIELD_CLAVE);
            String nombre = data.getString(Protocol.FIELD_NOMBRE);
            String rol = data.getString(Protocol.FIELD_ROL);
            
            Usuario usuario = null;
            
            switch (rol) {
                case "ADM":
                    usuario = new Administrador(id, clave, nombre);
                    break;
                case "MED":
                    String especialidad = data.getString(Protocol.FIELD_ESPECIALIDAD);
                    usuario = new Medico(id, clave, nombre, especialidad);
                    break;
                case "FAR":
                    usuario = new Farmaceuta(id, clave, nombre);
                    break;
                default:
                    return crearRespuestaError("Rol no válido");
            }
            
            boolean exito = usuarioDao.insertar(usuario);
            
            if (exito) {
                return crearRespuestaExito("Usuario agregado exitosamente");
            } else {
                return crearRespuestaError("No se pudo agregar el usuario");
            }
        } catch (Exception e) {
            return crearRespuestaError("Error al agregar usuario: " + e.getMessage());
        }
    }
    
    private String procesarListarUsuariosActivos() {
        try {
            List<UsuarioActivo> usuariosActivos = usuarioActivoDao.listarActivos();
            
            JSONArray usuariosArray = new JSONArray();
            for (UsuarioActivo ua : usuariosActivos) {
                JSONObject uaJson = new JSONObject();
                uaJson.put(Protocol.FIELD_USUARIO_ID, ua.getUsuarioId());
                uaJson.put(Protocol.FIELD_NOMBRE, ua.getNombre());
                uaJson.put(Protocol.FIELD_ROL, ua.getRol());
                usuariosArray.put(uaJson);
            }
            
            JSONObject respData = new JSONObject();
            respData.put("usuariosActivos", usuariosArray);
            
            return crearRespuestaExito("Lista de usuarios activos obtenida", respData);
        } catch (Exception e) {
            return crearRespuestaError("Error al listar usuarios activos: " + e.getMessage());
        }
    }

    private String procesarEliminarUsuario(JSONObject data) {
        try {
            String id = data.getString(Protocol.FIELD_USUARIO_ID);
            
            boolean exito = usuarioDao.eliminar(id);
            
            if (exito) {
                return crearRespuestaExito("Usuario eliminado exitosamente");
            } else {
                return crearRespuestaError("No se pudo eliminar el usuario");
            }
        } catch (Exception e) {
            return crearRespuestaError("Error al eliminar usuario: " + e.getMessage());
        }
    }
    
    // ==================== MENSAJERÍA ====================
    
    private String procesarEnviarMensaje(JSONObject data, ClientHandler clientHandler) {
        try {
            String remitenteId = data.getString(Protocol.FIELD_REMITENTE_ID);
            String remitenteNombre = data.getString(Protocol.FIELD_REMITENTE_NOMBRE);
            String destinatarioId = data.getString(Protocol.FIELD_DESTINATARIO_ID);
            String destinatarioNombre = data.getString(Protocol.FIELD_DESTINATARIO_NOMBRE);
            String texto = data.getString(Protocol.FIELD_TEXTO);
            
            Mensaje mensaje = new Mensaje(remitenteId, remitenteNombre, 
                                         destinatarioId, destinatarioNombre, texto);
            
            boolean exito = mensajeDao.insertar(mensaje);
            
            if (exito) {
                // Notificar al destinatario si está conectado
                String notificacion = clientHandler.getServer().getNotificationManager()
                    .crearNotificacionMensaje(remitenteId, remitenteNombre, destinatarioId, texto);
                clientHandler.getServer().notificarACliente(destinatarioId, notificacion);
                
                return crearRespuestaExito("Mensaje enviado");
            } else {
                return crearRespuestaError("No se pudo enviar el mensaje");
            }
        } catch (Exception e) {
            return crearRespuestaError("Error al enviar mensaje: " + e.getMessage());
        }
    }
    
    private String procesarRecibirMensajes(JSONObject data) {
        try {
            String usuarioId = data.getString(Protocol.FIELD_USUARIO_ID);
            
            List<Mensaje> mensajes = mensajeDao.buscarMensajesNoLeidos(usuarioId);
            
            JSONArray mensajesArray = new JSONArray();
            for (Mensaje msg : mensajes) {
                JSONObject msgJson = new JSONObject();
                msgJson.put(Protocol.FIELD_MENSAJE_ID, msg.getId());
                msgJson.put(Protocol.FIELD_REMITENTE_ID, msg.getRemitenteId());
                msgJson.put(Protocol.FIELD_REMITENTE_NOMBRE, msg.getRemitenteNombre());
                msgJson.put(Protocol.FIELD_TEXTO, msg.getTexto());
                msgJson.put("fechaEnvio", dateFormat.format(msg.getFechaEnvio()));
                mensajesArray.put(msgJson);
            }
            
            JSONObject respData = new JSONObject();
            respData.put("mensajes", mensajesArray);
            
            return crearRespuestaExito("Mensajes obtenidos", respData);
        } catch (Exception e) {
            return crearRespuestaError("Error al recibir mensajes: " + e.getMessage());
        }
    }
    
    private String procesarMarcarMensajeLeido(JSONObject data) {
        try {
            int mensajeId = data.getInt(Protocol.FIELD_MENSAJE_ID);
            
            boolean exito = mensajeDao.marcarComoLeido(mensajeId);
            
            if (exito) {
                return crearRespuestaExito("Mensaje marcado como leído");
            } else {
                return crearRespuestaError("No se pudo marcar el mensaje");
            }
        } catch (Exception e) {
            return crearRespuestaError("Error al marcar mensaje: " + e.getMessage());
        }
    }
    
    private String procesarContarMensajesNoLeidos(JSONObject data) {
        try {
            String usuarioId = data.getString(Protocol.FIELD_USUARIO_ID);
            
            int count = mensajeDao.contarNoLeidos(usuarioId);
            
            JSONObject respData = new JSONObject();
            respData.put("count", count);
            
            return crearRespuestaExito("Conteo obtenido", respData);
        } catch (Exception e) {
            return crearRespuestaError("Error al contar mensajes: " + e.getMessage());
        }
    }
    
    // ==================== MÉTODOS AUXILIARES ====================
    
    private JSONObject pacienteToJson(Paciente pac) {
        JSONObject json = new JSONObject();
        json.put(Protocol.FIELD_PACIENTE_ID, pac.getId());
        json.put(Protocol.FIELD_NOMBRE, pac.getNombre());
        json.put(Protocol.FIELD_FECHA_NACIMIENTO, dateFormat.format(pac.getFechaNacimiento()));
        json.put(Protocol.FIELD_TELEFONO, pac.getTelefono());
        return json;
    }
    
    private JSONObject medicamentoToJson(Medicamento med) {
        JSONObject json = new JSONObject();
        json.put(Protocol.FIELD_MEDICAMENTO_CODIGO, med.getCodigo());
        json.put(Protocol.FIELD_NOMBRE, med.getNombre());
        json.put(Protocol.FIELD_PRESENTACION, med.getPresentacion());
        return json;
    }
    
    private JSONObject recetaToJson(Receta receta) {
        JSONObject json = new JSONObject();
        json.put(Protocol.FIELD_RECETA_ID, receta.getId());
        json.put(Protocol.FIELD_FECHA_CONFECCION, dateFormat.format(receta.getFechaConfeccion()));
        json.put(Protocol.FIELD_FECHA_RETIRO, dateFormat.format(receta.getFechaRetiro()));
        json.put(Protocol.FIELD_ESTADO, receta.getEstado());
        json.put(Protocol.FIELD_MEDICO_ID, receta.getMedicoId());
        json.put("paciente", pacienteToJson(receta.getPaciente()));
        
        // Fechas opcionales
        if (receta.getFechaProceso() != null) {
            json.put("fechaProceso", dateFormat.format(receta.getFechaProceso()));
        }
        if (receta.getFechaLista() != null) {
            json.put("fechaLista", dateFormat.format(receta.getFechaLista()));
        }
        if (receta.getFechaEntrega() != null) {
            json.put("fechaEntrega", dateFormat.format(receta.getFechaEntrega()));
        }
        
        // Detalles
        JSONArray detallesArray = new JSONArray();
        for (DetalleReceta detalle : receta.getDetalles()) {
            JSONObject detalleJson = new JSONObject();
            detalleJson.put("medicamento", medicamentoToJson(detalle.getMedicamento()));
            detalleJson.put(Protocol.FIELD_CANTIDAD, detalle.getCantidad());
            detalleJson.put(Protocol.FIELD_INDICACIONES, detalle.getIndicaciones());
            detalleJson.put(Protocol.FIELD_DURACION_DIAS, detalle.getDuracionDias());
            detallesArray.put(detalleJson);
        }
        json.put(Protocol.FIELD_DETALLES, detallesArray);
        
        return json;
    }
    
    private String crearRespuestaExito(String mensaje) {
        JSONObject response = new JSONObject();
        response.put("type", Protocol.TYPE_RESPONSE);
        response.put("status", Protocol.STATUS_SUCCESS);
        response.put("message", mensaje);
        response.put("data", new JSONObject());
        return response.toString();
    }
    
    private String crearRespuestaExito(String mensaje, JSONObject data) {
        JSONObject response = new JSONObject();
        response.put("type", Protocol.TYPE_RESPONSE);
        response.put("status", Protocol.STATUS_SUCCESS);
        response.put("message", mensaje);
        response.put("data", data);
        return response.toString();
    }
    
    private String crearRespuestaError(String mensaje) {
        JSONObject response = new JSONObject();
        response.put("type", Protocol.TYPE_RESPONSE);
        response.put("status", Protocol.STATUS_ERROR);
        response.put("message", mensaje);
        response.put("data", new JSONObject());
        return response.toString();
    }
}