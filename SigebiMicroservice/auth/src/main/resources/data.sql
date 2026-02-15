-- ==========================================
-- 1️⃣ INSERTAR PERMISOS
-- ==========================================
INSERT INTO auth_permission (id, name, description) VALUES
-- Permisos de usuarios
(gen_random_uuid(), 'users.create', 'Crear usuarios'),
(gen_random_uuid(), 'users.read',   'Ver usuarios'),
(gen_random_uuid(), 'users.update', 'Actualizar usuarios'),
(gen_random_uuid(), 'users.delete', 'Eliminar usuarios'),

-- Permisos de reportes
(gen_random_uuid(), 'reports.read',  'Ver reportes'),
(gen_random_uuid(), 'reports.write', 'Crear/editar reportes'),
(gen_random_uuid(), 'reports.delete', 'Eliminar reportes'),

-- Permisos de sesiones
(gen_random_uuid(), 'sessions.manage', 'Gestionar sesiones'),

-- Permisos de roles
(gen_random_uuid(), 'roles.manage', 'Gestionar roles y permisos');


-- ==========================================
-- 2️⃣ INSERTAR ROLES
-- ==========================================
INSERT INTO auth_role (id, name) VALUES
(gen_random_uuid(), 'SUPERADMIN'),
(gen_random_uuid(), 'ADMIN'),
(gen_random_uuid(), 'SUPERVISOR'),
(gen_random_uuid(), 'TECNICO');


-- ==========================================
-- 3️⃣ ASIGNAR PERMISOS A ROLES
-- ==========================================

-- SUPERADMIN: Todos los permisos
INSERT INTO role_permission (id, role_id, permission_id)
SELECT gen_random_uuid(), r.id, p.id
FROM auth_role r, auth_permission p
WHERE r.name = 'SUPERADMIN';


-- ADMIN: Casi todos los permisos (sin gestión de roles)
INSERT INTO role_permission (id, role_id, permission_id)
SELECT gen_random_uuid(), r.id, p.id
FROM auth_role r
JOIN auth_permission p ON p.name IN (
  'users.create',
  'users.read',
  'users.update',
  'users.delete',
  'reports.read',
  'reports.write',
  'reports.delete',
  'sessions.manage'
)
WHERE r.name = 'ADMIN';


-- SUPERVISOR: Permisos de lectura/escritura, sin eliminación
INSERT INTO role_permission (id, role_id, permission_id)
SELECT gen_random_uuid(), r.id, p.id
FROM auth_role r
JOIN auth_permission p ON p.name IN (
  'users.read',
  'users.update',
  'reports.read',
  'reports.write'
)
WHERE r.name = 'SUPERVISOR';


-- TECNICO: Solo lectura/escritura de reportes
INSERT INTO role_permission (id, role_id, permission_id)
SELECT gen_random_uuid(), r.id, p.id
FROM auth_role r
JOIN auth_permission p ON p.name IN (
  'reports.read',
  'reports.write',
  'users.read'
)
WHERE r.name = 'TECNICO';


-- ==========================================
-- 4️⃣ CREAR TABLA user_roles (si no existe)
-- ==========================================
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id UUID NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (role_id) REFERENCES auth_role(id) ON DELETE CASCADE
);