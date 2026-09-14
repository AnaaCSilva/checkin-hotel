package br.com.mvc.dao;

import br.com.mvc.config.MysqlSingleton;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Classe base dos DAOs.
 * Garante que Connection e PreparedStatement sejam fechados a cada operacao.
 */
public abstract class MysqlDAO {

    protected Connection getConnection() throws SQLException {
        return MysqlSingleton.getInstance().getConnection();
    }

    protected void preencherParametros(PreparedStatement stmt, Object... parametros) throws SQLException {
        for (int i = 0; i < parametros.length; i++) {
            stmt.setObject(i + 1, parametros[i]);
        }
    }

    protected int executarUpdate(String sql, Object... parametros) throws SQLException {
        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            this.preencherParametros(stmt, parametros);
            return stmt.executeUpdate();
        }
    }
}