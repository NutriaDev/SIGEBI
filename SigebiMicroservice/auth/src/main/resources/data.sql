INSERT INTO auth_permission (id, code, module, action) VALUES
(gen_random_uuid(), 'users.create', 'USERS', 'CREATE'),
(gen_random_uuid(), 'users.read',   'USERS', 'READ'),
(gen_random_uuid(), 'users.update', 'USERS', 'UPDATE'),
(gen_random_uuid(), 'reports.read', 'REPORTS', 'READ'),
(gen_random_uuid(), 'reports.write','REPORTS', 'WRITE');

INSERT INTO auth_role (id, name) VALUES
(gen_random_uuid(), 'SUPERADMIN'),
(gen_random_uuid(), 'ADMIN'),
(gen_random_uuid(), 'SUPERVISOR'),
(gen_random_uuid(), 'TECNICO');

INSERT INTO role_permission (id, role_id, permission_id)
SELECT gen_random_uuid(), r.id, p.id
FROM auth_role r, auth_permission p
WHERE r.name = 'SUPERADMIN';


INSERT INTO role_permission (id, role_id, permission_id)
SELECT gen_random_uuid(), r.id, p.id
FROM auth_role r
JOIN auth_permission p ON p.code IN (
  'users.create',
  'users.read',
  'users.update',
  'reports.read',
  'reports.write'
)
WHERE r.name = 'ADMIN';

INSERT INTO role_permission (id, role_id, permission_id)
SELECT gen_random_uuid(), r.id, p.id
FROM auth_role r
JOIN auth_permission p ON p.code IN (
  'users.create',
    'users.read',
    'users.update',
    'reports.read',
    'reports.write'
)
WHERE r.name = 'SUPERVISOR';

INSERT INTO role_permission (id, role_id, permission_id)
SELECT gen_random_uuid(), r.id, p.id
FROM auth_role r
JOIN auth_permission p ON p.code IN (
  'reports.read',
  'reports.write',
  'users.read'
)
WHERE r.name = 'TECNICO';

