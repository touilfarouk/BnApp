-- order_app_schema.sql
-- Creates schema and tables for structures, personnel, products, and affectations.

DROP DATABASE IF EXISTS order_app;
CREATE DATABASE order_app
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;
USE order_app;

-- Structures reference data
CREATE TABLE structures (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Personnel working in structures
CREATE TABLE personnel (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(50),
    role VARCHAR(255),
    structure_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_personnel_structure
        FOREIGN KEY (structure_id) REFERENCES structures(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE=InnoDB;

-- Product catalog
CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    label VARCHAR(255),
    description TEXT,
    structure_id INT,
    structure_name VARCHAR(255),
    assigned_personnel_id INT,
    assigned_personnel_name VARCHAR(255),
    accessories TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_products_structure
        FOREIGN KEY (structure_id) REFERENCES structures(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE,
    CONSTRAINT fk_products_personnel
        FOREIGN KEY (assigned_personnel_id) REFERENCES personnel(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE,
    INDEX idx_products_structure (structure_id),
    INDEX idx_products_personnel (assigned_personnel_id)
) ENGINE=InnoDB;

-- Affectations of products to personnel/structures
CREATE TABLE affectations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    personnel_id INT,
    structure_id INT,
    affectation_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_affectations_product
        FOREIGN KEY (product_id) REFERENCES products(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_affectations_personnel
        FOREIGN KEY (personnel_id) REFERENCES personnel(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE,
    CONSTRAINT fk_affectations_structure
        FOREIGN KEY (structure_id) REFERENCES structures(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE,
    INDEX idx_affectations_product (product_id),
    INDEX idx_affectations_personnel (personnel_id),
    INDEX idx_affectations_structure (structure_id)
) ENGINE=InnoDB;

-- Optional basic seed data (remove if not needed)
INSERT INTO structures (name, address) VALUES
    ('Siège', 'Adresse principale'),
    ('Annexe 1', 'Adresse annexe 1');

INSERT INTO personnel (full_name, email, role, structure_id) VALUES
    ('Alice Dupont', 'alice@example.com', 'Gestionnaire', 1),
    ('Mohamed Karim', 'mohamed@example.com', 'Technicien', 2);

INSERT INTO products (
    name,
    label,
    structure_id,
    structure_name,
    assigned_personnel_id,
    assigned_personnel_name,
    accessories
) VALUES
    (
        'Scanner RFID',
        'RFID-4000',
        1,
        'Siège',
        1,
        'Alice Dupont',
        '["SCANNER_HEAD", "POWER_SUPPLY"]'
    ),
    (
        'Caméra IP',
        'IP-CAM-1080',
        2,
        'Annexe 1',
        2,
        'Mohamed Karim',
        '["MOUNT", "ETHERNET_CABLE"]'
    );

INSERT INTO affectations (product_id, personnel_id, structure_id, notes) VALUES
    (1, 1, 1, 'Affecté au service logistique'),
    (2, 2, 2, 'Installation à l\'entrepôt annexe');