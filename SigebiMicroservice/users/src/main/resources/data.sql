INSERT INTO role (name_role, status) VALUES
('SUPERADMIN', true),
('ADMIN', true),
('SUPERVISOR', true),
('TECNICO', true);




INSERT INTO company ( name, status) VALUES
('SIGEBI', true),
('DRAGONESDEV', true),
('SANITAS', true),
('CLINICAX', true);


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
) VALUES (
    'Admin',
    'System',
    '1990-01-01',
    '3000000000',
    1234567890,
    'admin@test.com',
    1,
    '$2a$12$1XGV5aOha/AnT66YXJ91.OKc2/EvjmWJWjOE08brKK3s/VBff7pIi',
    true,
    2
);



