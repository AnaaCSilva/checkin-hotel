-- =========================================
-- BANCO
-- =========================================

CREATE DATABASE IF NOT EXISTS mvc_java
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE mvc_java;

-- =========================================
-- PERFIS (Gerente / Recepcionista)
-- =========================================

CREATE TABLE IF NOT EXISTS perfis (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT uk_perfil_nome UNIQUE (nome)
);

-- =========================================
-- USUARIOS (funcionarios que acessam o sistema)
-- =========================================

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(150) NOT NULL,
    login VARCHAR(100) NOT NULL,
    senha VARCHAR(255) NOT NULL,
    perfil_id BIGINT NOT NULL,

    PRIMARY KEY (id),

    CONSTRAINT uk_usuario_login UNIQUE (login),

    CONSTRAINT fk_usuario_perfil
        FOREIGN KEY (perfil_id)
        REFERENCES perfis(id)
);

-- =========================================
-- QUARTOS
-- =========================================

CREATE TABLE IF NOT EXISTS quartos (
    id BIGINT NOT NULL AUTO_INCREMENT,
    numero VARCHAR(10) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'Disponível',

    PRIMARY KEY (id),
    CONSTRAINT uk_quarto_numero UNIQUE (numero)
);

-- =========================================
-- HOSPEDES (dados pessoais: id, nome, cpf)
-- =========================================

CREATE TABLE IF NOT EXISTS hospedes (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(150) NOT NULL,
    cpf VARCHAR(11) NOT NULL,
    telefone VARCHAR(20) NULL,
    email VARCHAR(150) NULL,

    PRIMARY KEY (id),
    CONSTRAINT uk_hospede_cpf UNIQUE (cpf)
);

-- =========================================
-- CHECKIN (hospedagem: quarto + dias + pagamento)
-- Depende de hospedes e quartos, por isso vem por ultimo.
-- =========================================

CREATE TABLE IF NOT EXISTS checkin (
    id BIGINT NOT NULL AUTO_INCREMENT,
    hospede_id BIGINT NOT NULL,
    quarto_id BIGINT NOT NULL,
    quantidade_dias INT NOT NULL,
    forma_pagamento VARCHAR(30) NOT NULL,
    data_checkin DATETIME NOT NULL,
    data_prevista_saida DATETIME NOT NULL,
    data_checkout DATETIME NULL,

    PRIMARY KEY (id),

    CONSTRAINT fk_checkin_hospede
        FOREIGN KEY (hospede_id) REFERENCES hospedes(id),

    CONSTRAINT fk_checkin_quarto
        FOREIGN KEY (quarto_id) REFERENCES quartos(id),

    CONSTRAINT chk_checkin_dias
        CHECK (quantidade_dias >= 1)
);

-- =========================================
-- DADOS INICIAIS
-- =========================================

INSERT INTO perfis (nome) VALUES ('Gerente'), ('Recepcionista');

-- Senhas em texto puro apenas por ser projeto de aula.
INSERT INTO usuarios (nome, login, senha, perfil_id) VALUES
    ('Gerente do Hotel',      'gerente',   'gerente123', 1),
    ('Recepcionista Turno 1', 'recepcao1', '123456',     2);

INSERT INTO quartos (numero, tipo, status) VALUES
    ('101', 'Solteiro', 'Disponível'),
    ('102', 'Casal',    'Disponível'),
    ('103', 'Solteiro', 'Disponível'),
    ('201', 'Suíte',    'Disponível'),
    ('202', 'Suíte',    'Manutenção');
