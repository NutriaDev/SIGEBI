-- ============================================
-- ROLES (con manejo de duplicados)
-- ============================================
INSERT INTO role (name_role, status)
VALUES
    ('SUPERADMIN', true),
    ('ADMIN', true),
    ('SUPERVISOR', true),
    ('TECNICO', true)
ON CONFLICT (name_role) DO NOTHING;

-- ============================================
-- COMPANIES
-- ============================================
INSERT INTO company (name, status)
VALUES
    ('SIGEBI', true),
    ('DRAGONESDEV', true),
    ('SANITAS', true),
    ('CLINICAX', true)
ON CONFLICT (name) DO NOTHING;

-- ============================================
-- USERS DE PRUEBA
-- ============================================
INSERT INTO users (
    name,
    lastname,
    birth_date,
    phone,
    id,
    email,
    company_id,
    password_hash,
    status,
    role_id
) VALUES
    -- SUPERADMIN
    (
        'Diana',
        'Arevalo',
        '1990-01-01',
        '3000000000',
        1234567890,
        'nutriadevelop@gmail.com',
        1,
        '$2a$10$/RvqmTaTMXu12dygu0DXne0uKsgOiF9Z2HeSz/nzucRWhyrCzoLke',
        true,
        1
    ),
    -- ADMIN
    (
        'Oscar',
        'Tovar',
        '1991-08-20',
        '3002222222',
        1000000002,
        'caroldevelop9607@gmail.com',
        1,
        '$2a$10$/RvqmTaTMXu12dygu0DXne0uKsgOiF9Z2HeSz/nzucRWhyrCzoLke',
        true,
        2
    ),
    -- SUPERVISOR
    (
        'Niña',
        'Gata',
        '1993-03-15',
        '3003333333',
        1000000003,
        'ninaGataTest@gmail.com',
        1,
        '$2a$10$/RvqmTaTMXu12dygu0DXne0uKsgOiF9Z2HeSz/nzucRWhyrCzoLke',
        true,
        3
    ),
    -- TECNICO
    (
        'Pitin',
        'Gato Mion',
        '1994-11-01',
        '3004444444',
        1000000004,
        'pitinGatoMionTest@gmail.com',
        1,
        '$2a$10$/RvqmTaTMXu12dygu0DXne0uKsgOiF9Z2HeSz/nzucRWhyrCzoLke',
        true,
        4
    )
ON CONFLICT (id) DO NOTHING;