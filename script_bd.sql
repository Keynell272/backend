-- ============================================================
-- SCRIPT DE BASE DE DATOS - VERSIÓN ACTUALIZADA
-- Sistema de Prescripción y Despacho de Recetas
-- ============================================================

-- Crear la base de datos
DROP DATABASE IF EXISTS hospital_db;
CREATE DATABASE hospital_db;
USE hospital_db;

-- ============================================================
-- TABLA: usuarios
-- ============================================================
CREATE TABLE usuarios (
    id VARCHAR(20) PRIMARY KEY,
    clave VARCHAR(100) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    rol ENUM('ADM', 'MED', 'FAR') NOT NULL,
    especialidad VARCHAR(100) NULL,
    estado VARCHAR(20) DEFAULT 'activo',
    INDEX idx_rol (rol),
    INDEX idx_estado (estado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLA: pacientes
-- ============================================================
CREATE TABLE pacientes (
    id VARCHAR(20) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    estado VARCHAR(20) DEFAULT 'activo',
    INDEX idx_nombre (nombre),
    INDEX idx_estado (estado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLA: medicamentos
-- ============================================================
CREATE TABLE medicamentos (
    codigo VARCHAR(20) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    presentacion VARCHAR(100) NOT NULL,
    estado VARCHAR(20) DEFAULT 'activo',
    INDEX idx_nombre (nombre),
    INDEX idx_estado (estado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLA: recetas
-- ============================================================
CREATE TABLE recetas (
    id VARCHAR(20) PRIMARY KEY,
    fecha_confeccion TIMESTAMP NOT NULL,
    fecha_retiro TIMESTAMP NOT NULL,
    fecha_proceso TIMESTAMP NULL,
    fecha_lista TIMESTAMP NULL,
    fecha_entrega TIMESTAMP NULL,
    estado ENUM('confeccionada', 'proceso', 'lista', 'entregada') NOT NULL DEFAULT 'confeccionada',
    paciente_id VARCHAR(20) NOT NULL,
    medico_id VARCHAR(20) NOT NULL,
    FOREIGN KEY (paciente_id) REFERENCES pacientes(id),
    FOREIGN KEY (medico_id) REFERENCES usuarios(id),
    INDEX idx_estado (estado),
    INDEX idx_fecha_confeccion (fecha_confeccion),
    INDEX idx_paciente (paciente_id),
    INDEX idx_medico (medico_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLA: detalle_recetas
-- ============================================================
CREATE TABLE detalle_recetas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    receta_id VARCHAR(20) NOT NULL,
    medicamento_codigo VARCHAR(20) NOT NULL,
    cantidad INT NOT NULL,
    indicaciones TEXT NOT NULL,
    duracion_dias INT NOT NULL,
    FOREIGN KEY (receta_id) REFERENCES recetas(id) ON DELETE CASCADE,
    FOREIGN KEY (medicamento_codigo) REFERENCES medicamentos(codigo),
    INDEX idx_receta (receta_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLA: usuarios_activos
-- ============================================================
CREATE TABLE usuarios_activos (
    usuario_id VARCHAR(20) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    rol ENUM('ADM', 'MED', 'FAR') NOT NULL,
    fecha_login TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(50) NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLA: mensajes
-- ============================================================
CREATE TABLE mensajes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    remitente_id VARCHAR(20) NOT NULL,
    remitente_nombre VARCHAR(100) NOT NULL,
    destinatario_id VARCHAR(20) NOT NULL,
    destinatario_nombre VARCHAR(100) NOT NULL,
    texto TEXT NOT NULL,
    fecha_envio TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    leido BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (remitente_id) REFERENCES usuarios(id),
    FOREIGN KEY (destinatario_id) REFERENCES usuarios(id),
    INDEX idx_destinatario (destinatario_id),
    INDEX idx_leido (leido),
    INDEX idx_fecha (fecha_envio)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- DATOS DE PRUEBA
-- ============================================================

-- Insertar usuarios
INSERT INTO usuarios (id, clave, nombre, rol, especialidad, estado) VALUES
('ADM001', '1234', 'Admin Principal', 'ADM', NULL, 'activo'),
('MED001', '1234', 'Dr. Juan García', 'MED', 'Medicina General', 'activo'),
('MED002', '1234', 'Dra. María López', 'MED', 'Pediatría', 'activo'),
('MED003', '1234', 'Dr. Carlos Rodríguez', 'MED', 'Cardiología', 'activo'),
('FAR001', '1234', 'Farm. Ana Martínez', 'FAR', NULL, 'activo'),
('FAR002', '1234', 'Farm. Pedro Sánchez', 'FAR', NULL, 'activo');

-- Insertar pacientes
INSERT INTO pacientes (id, nombre, fecha_nacimiento, telefono, estado) VALUES
('PAC001', 'José Hernández', '1985-03-15', '8888-1111', 'activo'),
('PAC002', 'Laura Jiménez', '1990-07-22', '8888-2222', 'activo'),
('PAC003', 'Roberto Castro', '1978-11-30', '8888-3333', 'activo'),
('PAC004', 'Carmen Vargas', '1995-05-10', '8888-4444', 'activo'),
('PAC005', 'Miguel Ángel Mora', '1982-09-18', '8888-5555', 'activo');

-- Insertar medicamentos
INSERT INTO medicamentos (codigo, nombre, presentacion, estado) VALUES
('MED001', 'Ibuprofeno', 'Tabletas 400mg', 'activo'),
('MED002', 'Paracetamol', 'Tabletas 500mg', 'activo'),
('MED003', 'Amoxicilina', 'Cápsulas 500mg', 'activo'),
('MED004', 'Loratadina', 'Tabletas 10mg', 'activo'),
('MED005', 'Omeprazol', 'Cápsulas 20mg', 'activo'),
('MED006', 'Metformina', 'Tabletas 850mg', 'activo'),
('MED007', 'Losartán', 'Tabletas 50mg', 'activo'),
('MED008', 'Atorvastatina', 'Tabletas 20mg', 'activo'),
('MED009', 'Salbutamol', 'Inhalador 100mcg', 'activo'),
('MED010', 'Diclofenaco', 'Gel tópico 1%', 'activo'),
('MED011', 'Cetirizina', 'Tabletas 10mg', 'activo'),
('MED012', 'Ranitidina', 'Tabletas 150mg', 'activo');

-- Insertar algunas recetas de ejemplo
INSERT INTO recetas (id, fecha_confeccion, fecha_retiro, estado, paciente_id, medico_id) VALUES
('REC001', '2024-10-01 09:00:00', '2024-10-03 09:00:00', 'entregada', 'PAC001', 'MED001'),
('REC002', '2024-10-10 10:30:00', '2024-10-12 10:30:00', 'lista', 'PAC002', 'MED002'),
('REC003', '2024-10-13 14:00:00', '2024-10-15 14:00:00', 'proceso', 'PAC003', 'MED001'),
('REC004', '2024-10-13 16:00:00', '2024-10-16 16:00:00', 'confeccionada', 'PAC004', 'MED003');

-- Insertar detalles de recetas
INSERT INTO detalle_recetas (receta_id, medicamento_codigo, cantidad, indicaciones, duracion_dias) VALUES
('REC001', 'MED001', 20, 'Tomar 1 tableta cada 8 horas con alimentos', 7),
('REC001', 'MED005', 14, 'Tomar 1 cápsula en ayunas', 14),
('REC002', 'MED003', 21, 'Tomar 1 cápsula cada 8 horas', 7),
('REC002', 'MED002', 30, 'Tomar 1 tableta cada 6 horas si hay fiebre', 10),
('REC003', 'MED006', 60, 'Tomar 1 tableta con el desayuno y cena', 30),
('REC003', 'MED007', 30, 'Tomar 1 tableta en la mañana', 30),
('REC004', 'MED004', 10, 'Tomar 1 tableta al día', 10);