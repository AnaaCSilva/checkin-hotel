package br.com.mvc.dao;

import br.com.mvc.model.Quarto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuartoDAO extends MysqlDAO {

    public QuartoDAO() {
        super();
    }

    public List<Quarto> listarTodos() {
        String sql = "SELECT id, numero, tipo, status FROM quartos ORDER BY numero";
        List<Quarto> lista = new ArrayList<>();
        try (ResultSet rs = super.executar(sql)) {
            while (rs.next()) {
                lista.add(this.mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar quartos.", e);
        }
        return lista;
    }

    public Quarto buscarPorId(Long id) {
        String sql = "SELECT id, numero, tipo, status FROM quartos WHERE id = ?";
        try (ResultSet rs = super.executar(sql, id)) {
            if (rs.next()) {
                return this.mapear(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar por id.", e);
        }
        return null;
    }

    public Quarto buscarPorNumero(String numero) {
        String sql = "SELECT id, numero, tipo, status FROM quartos WHERE numero = ?";
        try (ResultSet rs = super.executar(sql, numero)) {
            if (rs.next()) {
                return this.mapear(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar por numero.", e);
        }
        return null;
    }

    public void inserir(Quarto quarto) {
        String sql = "INSERT INTO quartos (numero, tipo, status) VALUES (?, ?, ?)";
        try {
            super.executarUpdate(sql, quarto.getNumero(), quarto.getTipo(), quarto.getStatus());
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir.", e);
        }
    }

    public void alterar(Quarto quarto) {
        String sql = "UPDATE quartos SET numero = ?, tipo = ?, status = ? WHERE id = ?";
        try {
            super.executarUpdate(sql, quarto.getNumero(), quarto.getTipo(), quarto.getStatus(), quarto.getId());
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao alterar.", e);
        }
    }

    public void deletar(Long id) {
        String sql = "DELETE FROM quartos WHERE id = ?";
        try {
            super.executarUpdate(sql, id);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar.", e);
        }
    }

    private Quarto mapear(ResultSet rs) throws SQLException {
        Quarto quarto = new Quarto();
        quarto.setId(rs.getLong("id"));
        quarto.setNumero(rs.getString("numero"));
        quarto.setTipo(rs.getString("tipo"));
        quarto.setStatus(rs.getString("status"));
    return quarto;
}
}