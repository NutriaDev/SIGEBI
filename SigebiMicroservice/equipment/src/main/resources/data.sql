INSERT INTO equipments
(
serie,
name,
brand,
model,
invima,
area_id,
classification_id,
provider_id,
state_id,
location_id,
created_by,
risk_level,
acquisition_date,
useful_life,
warranty_end,
maintenance_frequency,
calibration_frequency,
active,
created_at
)
VALUES
('EQ-1001','Monitor Multiparámetro','Philips','MX450','INVIMA-001',1,3,1,1,1,1,'IIA',NOW(),10,NOW() + INTERVAL '3 years',6,12,true,NOW()),

('EQ-1002','Ventilador Mecánico','Dräger','Evita V500','INVIMA-002',2,2,2,1,2,1,'IIB',NOW(),12,NOW() + INTERVAL '4 years',6,12,true,NOW()),

('EQ-1003','Electrocardiógrafo','GE Healthcare','MAC 2000','INVIMA-003',1,1,3,1,1,1,'IIA',NOW(),8,NOW() + INTERVAL '2 years',12,24,true,NOW()),

('EQ-1004','Desfibrilador','Zoll','R Series','INVIMA-004',1,2,4,1,1,1,'III',NOW(),10,NOW() + INTERVAL '3 years',6,12,true,NOW()),

('EQ-1005','Incubadora Neonatal','Dräger','Isolette 8000','INVIMA-005',2,2,2,1,2,1,'IIB',NOW(),10,NOW() + INTERVAL '3 years',6,12,true,NOW()),

('EQ-1006','Analizador Hematológico','Mindray','BC-5000','INVIMA-006',3,4,5,1,3,1,'IIA',NOW(),8,NOW() + INTERVAL '3 years',12,12,true,NOW()),

('EQ-1007','Rayos X Digital','Siemens','Multix Fusion','INVIMA-007',4,1,1,1,4,1,'IIB',NOW(),15,NOW() + INTERVAL '5 years',12,12,true,NOW()),

('EQ-1008','Bomba de Infusión','Baxter','Sigma Spectrum','INVIMA-008',1,3,2,1,1,1,'IIA',NOW(),7,NOW() + INTERVAL '2 years',6,12,true,NOW()),

('EQ-1009','Monitor Fetal','Philips','Avalon FM30','INVIMA-009',5,3,3,1,5,1,'IIA',NOW(),10,NOW() + INTERVAL '3 years',12,24,true,NOW()),

('EQ-1010','Autoclave Hospitalario','Tuttnauer','3870EA','INVIMA-010',3,5,4,1,3,1,'IIB',NOW(),12,NOW() + INTERVAL '4 years',12,12,true,NOW());