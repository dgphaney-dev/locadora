-- =============================================================================
-- DRIVEWAY LOCADORA - SCRIPT DE CRIACAO DO BANCO DE DADOS E DADOS FICTICIOS
-- Universidade Catolica de Brasilia (UCB) - Programacao Orientada a Objetos
-- =============================================================================

-- 1. Criacao da base de dados com codificacao UTF-8
CREATE DATABASE IF NOT EXISTS locadora_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE locadora_db;

-- 2. Limpeza de tabelas existentes (na ordem correta de integridade referencial)
DROP TABLE IF EXISTS locacao;
DROP TABLE IF EXISTS veiculo;
DROP TABLE IF EXISTS cliente;

-- =============================================================================
-- TABELA 1: CLIENTE
-- Entidade independente que se relaciona com Locacao (1:N)
-- =============================================================================
CREATE TABLE cliente (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    telefone VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- =============================================================================
-- TABELA 2: VEICULO
-- Estrategia de Mapeamento: Single Table Inheritance (Tabela Unica)
-- Armazena a superclasse abstrata Veiculo e as subclasses concretas Carro e Moto.
-- A coluna 'tipo_veiculo' atua como discriminador.
-- =============================================================================
CREATE TABLE veiculo (
    id_veiculo INT AUTO_INCREMENT PRIMARY KEY,
    placa VARCHAR(7) NOT NULL UNIQUE,
    marca VARCHAR(50) NOT NULL,
    modelo VARCHAR(60) NOT NULL,
    ano INT NOT NULL,
    valor_diaria DECIMAL(10, 2) NOT NULL,
    disponivel BOOLEAN NOT NULL DEFAULT TRUE,
    tipo_veiculo ENUM('CARRO', 'MOTO') NOT NULL,
    
    -- Atributo especifico de Carro (HATCH, SEDAN, SUV)
    categoria_carro ENUM('HATCH', 'SEDAN', 'SUV') NULL,
    
    -- Atributo especifico de Moto (cilindrada em cc)
    cilindrada_moto INT NULL,
    
    -- Restricoes de integridade de negocio no banco
    CONSTRAINT chk_diaria_positiva CHECK (valor_diaria > 0),
    CONSTRAINT chk_ano_valido CHECK (ano >= 1990)
) ENGINE=InnoDB;

-- =============================================================================
-- TABELA 3: LOCACAO
-- Entidade de relacionamento entre Cliente e Veiculo
-- Chaves estrangeiras com restricao de integridade (ON DELETE RESTRICT)
-- =============================================================================
CREATE TABLE locacao (
    id_locacao INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    id_veiculo INT NOT NULL,
    data_locacao DATE NOT NULL,
    dias_previstos INT NOT NULL,
    valor_total DECIMAL(10, 2) NOT NULL,
    status ENUM('ATIVA', 'FINALIZADA') NOT NULL DEFAULT 'ATIVA',
    
    CONSTRAINT fk_locacao_cliente FOREIGN KEY (id_cliente)
        REFERENCES cliente(id_cliente)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
        
    CONSTRAINT fk_locacao_veiculo FOREIGN KEY (id_veiculo)
        REFERENCES veiculo(id_veiculo)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
        
    CONSTRAINT chk_dias_positivos CHECK (dias_previstos > 0),
    CONSTRAINT chk_total_positivo CHECK (valor_total > 0)
) ENGINE=InnoDB;

-- =============================================================================
-- INSERCAO DE DADOS FICTICIOS DE EXEMPLO
-- =============================================================================

-- Clientes
INSERT INTO cliente (nome, cpf, telefone, email) VALUES
('Ana Clara Mendonca', '11122233344', '(61) 98111-2233', 'ana.mendonca@email.com'),
('Bruno Henrique Sousa', '22233344455', '(61) 98222-3344', 'bruno.sousa@email.com'),
('Carla Vasconcelos', '33344455566', '(61) 98333-4455', 'carla.vasc@email.com'),
('Diego Alcantara Ribeiro', '44455566677', '(61) 98444-5566', 'diego.ribeiro@email.com');

-- Veiculos do tipo CARRO (HATCH, SEDAN, SUV)
INSERT INTO veiculo (placa, marca, modelo, ano, valor_diaria, disponivel, tipo_veiculo, categoria_carro, cilindrada_moto) VALUES
('BRA2E19', 'Toyota', 'Corolla Altis', 2024, 210.00, TRUE, 'CARRO', 'SEDAN', NULL),
('RIO2A18', 'Jeep', 'Compass Longitude', 2023, 260.00, FALSE, 'CARRO', 'SUV', NULL),
('SPO1B22', 'Volkswagen', 'Polo TSI', 2024, 130.00, TRUE, 'CARRO', 'HATCH', NULL),
('BHZ9C44', 'Honda', 'Civic Touring', 2023, 230.00, TRUE, 'CARRO', 'SEDAN', NULL);

-- Veiculos do tipo MOTO (baixa e alta cilindrada)
INSERT INTO veiculo (placa, marca, modelo, ano, valor_diaria, disponivel, tipo_veiculo, categoria_carro, cilindrada_moto) VALUES
('DFM4D55', 'Honda', 'CB 500F', 2023, 140.00, TRUE, 'MOTO', NULL, 500),
('GOI5E66', 'Yamaha', 'Factor 150', 2024, 85.00, TRUE, 'MOTO', NULL, 150),
('CTB7F88', 'BMW', 'F 850 GS', 2023, 250.00, FALSE, 'MOTO', NULL, 850),
('POA8G99', 'Yamaha', 'Lander 250', 2023, 110.00, TRUE, 'MOTO', NULL, 250);

-- Locacoes registradas
INSERT INTO locacao (id_cliente, id_veiculo, data_locacao, dias_previstos, valor_total, status) VALUES
(1, 2, '2026-11-01', 7, 2142.00, 'ATIVA'),
(2, 7, '2026-11-05', 5, 1420.00, 'ATIVA'),
(3, 1, '2026-10-15', 4, 1020.00, 'FINALIZADA'),
(4, 6, '2026-10-20', 3, 310.00, 'FINALIZADA');
