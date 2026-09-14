    CREATE DATABASE IF NOT EXISTS mvc_java
        CHARACTER SET utf8mb4
        COLLATE utf8mb4_unicode_ci;

    USE mvc_java;

-- =========================================
-- TABELA DE PERFIS
-- =========================================

    CREATE TABLE perfis (
        id BIGINT NOT NULL AUTO_INCREMENT,
        nome VARCHAR(100) NOT NULL,

        PRIMARY KEY (id)
    );

-- =========================================
-- TABELA DE USUÁRIOS
-- =========================================

    CREATE TABLE usuarios (
        id BIGINT NOT NULL AUTO_INCREMENT,
        nome VARCHAR(150) NOT NULL,
        login VARCHAR(100) NOT NULL,
        senha VARCHAR(255) NOT NULL,
        perfil_id BIGINT NOT NULL,

        PRIMARY KEY (id),

        CONSTRAINT uk_usuario_login
            UNIQUE (login),

        CONSTRAINT fk_usuario_perfil
            FOREIGN KEY (perfil_id)
            REFERENCES perfis(id)
    );

-- =========================================
-- TABELA DE HÓSPEDES
-- =========================================

    CREATE TABLE hospedes (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(150) NOT NULL,
    tipo_documento VARCHAR(20) NOT NULL,
    numero_documento VARCHAR(30) NOT NULL,
    telefone VARCHAR(20),
    email VARCHAR(150),

    PRIMARY KEY (id),

    CONSTRAINT uk_hospede_documento
        UNIQUE (numero_documento),

    CONSTRAINT chk_hospede_tipo_documento
        CHECK (tipo_documento IN ('CPF', 'PASSAPORTE'))
    );

-- =========================================
-- DADOS PARA TESTE
-- =========================================

    INSERT INTO perfis (nome)
        VALUES
        ('Gerente'),
        ('Recepcionista'),
        ('Camareira');

    INSERT INTO usuarios (
        nome,
        login,
        senha,
        perfil_id
    )

    VALUES
        ('Gerente do Hotel', 'gerente', '123456', 1),
        ('Recepcionista Turno 1', 'recepcao1', '123456', 2),
        ('Recepcionista Turno 2', 'recepcao2', '123456', 3);

    INSERT INTO hospedes (nome, tipo_documento, numero_documento, telefone, email)
    VALUES
        ('Carlos Silva', 'CPF', '111.111.111-11', '(34) 99999-0001', 'carlos@email.com'),
        ('Ana Souza', 'PASSAPORTE', 'AB123456', '(34) 99999-0002', 'ana@email.com');

-- =========================================
-- CONSULTA DE EXEMPLO
-- =========================================

    SELECT
        u.id,
        u.nome,
        u.login,
        p.nome AS perfil
    FROM usuarios u
    INNER JOIN perfis p
        ON p.id = u.perfil_id
    ORDER BY u.nome;