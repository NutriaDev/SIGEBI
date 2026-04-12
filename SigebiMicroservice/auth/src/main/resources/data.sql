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
(gen_random_uuid(), 'reports.write', 'Crear/editar reportes'),

-- AREA EQUIPOS
(gen_random_uuid(), 'equipment.area.create', 'Crear Areas de los equipos'),
(gen_random_uuid(), 'equipment.area.read', 'Ver areas de equipos'),
(gen_random_uuid(), 'equipment.area.update', 'Actualizar areas de equipos'),

-- CLASIFICACION DE EQUIPOS
(gen_random_uuid(), 'equipment.classification.create', 'Crear clasificación de equipos'),
(gen_random_uuid(), 'equipment.classification.read', 'Ver clasificación de equipos'),
(gen_random_uuid(), 'equipment.classification.update', 'Actualizar clasificación de equipos'),

-- ESTADOS DE EQUIPOS
(gen_random_uuid(), 'equipment.state.create', 'Crear estados de equipos'),
(gen_random_uuid(), 'equipment.state.read', 'Ver estados de equipos'),
(gen_random_uuid(), 'equipment.state.update', 'Actualizar estados de equipos'),

-- UBICACION DE EQUIPOS
(gen_random_uuid(), 'equipment.location.create', 'Crear ubicaciones de equipos'),
(gen_random_uuid(), 'equipment.location.read', 'Ver ubicaciones de equipos'),
(gen_random_uuid(), 'equipment.location.update', 'Actualizar ubicaciones de equipos'),

-- PROVEEDOR DE EQUIPOS
(gen_random_uuid(), 'equipment.provider.create', 'Crear proveedores de equipos'),
(gen_random_uuid(), 'equipment.provider.read', 'Ver proveedores de equipos'),
(gen_random_uuid(), 'equipment.provider.update', 'Actualizar proveedores de equipos'),

-- EQUIPOS
(gen_random_uuid(), 'equipment.create', 'Crear equipos'),
(gen_random_uuid(), 'equipment.read', 'Ver equipos'),
(gen_random_uuid(), 'equipment.update', 'Actualizar equipos')
ON CONFLICT (name) DO NOTHING;

-- 3️⃣ INSERTAR ROLES
INSERT INTO auth_role (id, name) VALUES
(gen_random_uuid(), 'SUPERADMIN'),
(gen_random_uuid(), 'ADMIN'),
(gen_random_uuid(), 'SUPERVISOR'),
(gen_random_uuid(), 'TECNICO')
ON CONFLICT (name) DO NOTHING;

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
  'equipment.classification.read',
  'equipment.state.read',
  'equipment.location.read',
  'equipment.provider.read',
  'equipment.read',
  'equipment.create',
  'equipment.update',
  'equipment.provider.create',
  'equipment.provider.update',
  'equipment.location.create',
  'equipment.location.update',
  'equipment.state.create',
  'equipment.state.update',
  'equipment.classification.create',
  'equipment.classification.update',
  'equipment.area.create',
  'equipment.area.update',
  'equipment.area.read'

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
  'reports.write',
  'equipment.classification.read',
  'equipment.state.read',
  'equipment.location.read',
  'equipment.provider.read',
  'equipment.read',
  'equipment.create',
  'equipment.update',
  'equipment.provider.update',
  'equipment.provider.create',
  'equipment.location.create',
  'equipment.location.update',
  'equipment.classification.create',
  'equipment.classification.update',
  'equipment.area.read'

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
  'reports.write',
  'equipment.classification.read',
  'equipment.state.read',
  'equipment.location.read',
  'equipment.provider.read',
  'equipment.read',
  'equipment.create',
  'equipment.update',
  'equipment.location.create',
  'equipment.location.update',
  'equipment.classification.create',
  'equipment.classification.update',
  'equipment.area.read'

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