-- ==========================================
-- SCRIPT DE INICIALIZACIÓN MS AUTH
-- ==========================================

-- 1️⃣ LIMPIAR DATOS EXISTENTES (SOLO DESARROLLO)

-- ==========================================
--TRUNCATE TABLE user_roles CASCADE;
--TRUNCATE TABLE role_permission CASCADE;
--TRUNCATE TABLE auth_role CASCADE;
--TRUNCATE TABLE auth_permission CASCADE;
-- ==========================================

-- 2️⃣ INSERTAR PERMISOS
INSERT INTO auth_permission (id, name, description) VALUES

-- CREATE
(gen_random_uuid(), 'users.create.admin', 'Crear ADMIN'),
(gen_random_uuid(), 'users.create.supervisor', 'Crear SUPERVISOR'),
(gen_random_uuid(), 'users.create.tecnico', 'Crear TECNICO'),

-- READ
(gen_random_uuid(), 'users.read.admin', 'Ver ADMIN'),
(gen_random_uuid(), 'users.read.supervisor', 'Ver SUPERVISOR'),
(gen_random_uuid(), 'users.read.tecnico', 'Ver TECNICO'),

-- UPDATE
(gen_random_uuid(), 'users.update.admin', 'Actualizar ADMIN'),
(gen_random_uuid(), 'users.update.supervisor', 'Actualizar SUPERVISOR'),
(gen_random_uuid(), 'users.update.tecnico', 'Actualizar TECNICO'),

-- DELETE
(gen_random_uuid(), 'users.delete.admin', 'Eliminar ADMIN'),
(gen_random_uuid(), 'users.delete.supervisor', 'Eliminar SUPERVISOR'),
(gen_random_uuid(), 'users.delete.tecnico', 'Eliminar TECNICO'),

-- REPORTES
(gen_random_uuid(), 'reports.read', 'Ver reportes'),
(gen_random_uuid(), 'reports.write', 'Crear/editar reportes');

-- 3️⃣ INSERTAR ROLES
INSERT INTO auth_role (id, name) VALUES
(gen_random_uuid(), 'SUPERADMIN'),
(gen_random_uuid(), 'ADMIN'),
(gen_random_uuid(), 'SUPERVISOR'),
(gen_random_uuid(), 'TECNICO');

-- 4️⃣ ASIGNAR PERMISOS A ROLES

-- SUPERADMIN: Todos los permisos
INSERT INTO role_permission (id, role_id, permission_id)
SELECT gen_random_uuid(), r.id, p.id
FROM auth_role r
JOIN auth_permission p ON 1=1
WHERE r.name = 'SUPERADMIN'
AND NOT EXISTS (
    SELECT 1 FROM role_permission rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
);

-- ADMIN: Casi todos los permisos (sin gestión de roles)
INSERT INTO role_permission (id, role_id, permission_id)
SELECT gen_random_uuid(), r.id, p.id
FROM auth_role r
JOIN auth_permission p ON p.name IN (
  'users.create.supervisor',
  'users.create.tecnico',
  'users.read.admin',
  'users.read.supervisor',
  'users.read.tecnico',
  'users.update.admin',
  'users.update.supervisor',
  'users.update.tecnico',
  'users.delete.supervisor',
  'users.delete.tecnico',
  'reports.read',
  'reports.write'
)
WHERE r.name = 'ADMIN'
AND NOT EXISTS (
    SELECT 1 FROM role_permission rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
);

-- SUPERVISOR: Permisos de lectura/escritura, sin eliminación
INSERT INTO role_permission (id, role_id, permission_id)
SELECT gen_random_uuid(), r.id, p.id
FROM auth_role r
JOIN auth_permission p ON p.name IN (
  'users.create.tecnico',
  'users.read.admin',
  'users.read.supervisor',
  'users.read.tecnico',
  'users.update.supervisor',
  'users.update.tecnico',
  'reports.read',
  'reports.write'
)
WHERE r.name = 'SUPERVISOR'
AND NOT EXISTS (
    SELECT 1 FROM role_permission rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
);

-- TECNICO: Solo lectura/escritura de reportes
INSERT INTO role_permission (id, role_id, permission_id)
SELECT gen_random_uuid(), r.id, p.id
FROM auth_role r
JOIN auth_permission p ON p.name IN (
  'users.read.admin',
  'users.read.supervisor',
  'users.read.tecnico',
  'reports.read',
  'reports.write'
)
WHERE r.name = 'TECNICO'
AND NOT EXISTS (
    SELECT 1 FROM role_permission rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
);

-- 5️⃣ ASIGNAR ROLES A USUARIOS DE PRUEBA
-- ✅ CAMBIO CRÍTICO: Usar 1234567890 en lugar de 1
--INSERT INTO user_roles (user_id, role_id, created_at)
--SELECT u.id_users, r.id, CURRENT_TIMESTAMP
--FROM users u
--JOIN auth_role r ON r.name = 'SUPERADMIN'
--WHERE u.email = 'nutriadevelop@gmail.com'
--AND NOT EXISTS (
--    SELECT 1 FROM user_roles ur
--    WHERE ur.user_id = u.id_users AND ur.role_id = r.id
--);