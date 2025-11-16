-- Seed core structures expected by the mobile app
INSERT INTO structures (id, name, address)
VALUES
    (101, 'B.L Région Hauts Plateaux (Sétif)', NULL),
    (102, 'Départ.Génie Logiciel', NULL),
    (103, 'Départ.Laboratoire', NULL),
    (104, 'B.L Est (Constantine)', NULL),
    (105, 'B.L Centre (Blida)', NULL),
    (106, 'B.L Ouest (Oran)', NULL),
    (107, 'S/Direc E.A.D.Rural', NULL),
    (108, 'Direction Générale', NULL)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    address = VALUES(address);

-- Seed personnel referenced by the Android client
INSERT INTO personnel (
    id,
    full_name,
    email,
    phone,
    role,
    structure_id,
    created_at,
    updated_at
) VALUES
    (28, 'Djebbar Guedjali', NULL, NULL, 'Cadre d''Etudes Junior N2', 101, NOW(), NOW()),
    (29, 'Farouk Meridji', NULL, NULL, 'Cadre d''Etudes Junior N2', 101, NOW(), NOW()),
    (31, 'Kamel Hebbache', NULL, NULL, 'Chef de Bureau de Liaison H. Plateaux', 101, NOW(), NOW()),
    (57, 'Fares Zeghnoun', NULL, NULL, 'Technicien Supérieur N1', 102, NOW(), NOW()),
    (59, 'Hadjira Baitich', NULL, NULL, 'Cadre d''Etudes et d''Application', 103, NOW(), NOW()),
    (64, 'Hassiba Aguelmine', NULL, NULL, 'Cadre d''Etudes Senior N1', 103, NOW(), NOW()),
    (67, 'Hassiba Dahmani', NULL, NULL, 'Chef de Département Labo', 103, NOW(), NOW()),
    (164, 'Farouk Touil', NULL, NULL, 'Technicien d''Etudes N3', 102, NOW(), NOW()),
    (170, 'Meriem Sadi', NULL, NULL, 'Cadre d''Etude', 103, NOW(), NOW()),
    (171, 'Noureddine Ait Mouhoub', NULL, NULL, 'Cadre d''Etude', 103, NOW(), NOW()),
    (279, 'Nesreddine Fentazi', NULL, NULL, 'Cadre d''Etudes Junior N2', 101, NOW(), NOW())
ON DUPLICATE KEY UPDATE
    full_name = VALUES(full_name),
    email = VALUES(email),
    phone = VALUES(phone),
    role = VALUES(role),
    structure_id = VALUES(structure_id),
    updated_at = NOW();
