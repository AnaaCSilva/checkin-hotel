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
        return this.consultarLista("SELECT id, numero, tipo, status FROM quartos ORDER BY numero");
    }

    /** Usado no passo 2 do check-in: so os quartos livres aparecem para o recepcionista. */
    public List<Quarto> listarPorStatus(String status) {
        return this.consultarLista(
                "SELECT id, numero, tipo, status FROM quartos WHERE status = ? ORDER BY numero", status);
    }

    public Quarto buscarPorId(Long id) {
        return this.consultarUm("SELECT id, numero, tipo, status FROM quartos WHERE id = ?", id);
    }

    public Quarto buscarPorNumero(String numero) {
        return this.consultarUm("SELECT id, numero, tipo, status FROM quartos WHERE numero = ?", numero);
    }

    public void inserir(Quarto quarto) {
        String sql = "INSERT INTO quartos (numero, tipo, status) VALUES (?, ?, ?)";
        try {
            super.executarUpdate(sql, quarto.getNumero(), quarto.getTipo(), quarto.getStatus());
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir quarto.", e);
        }
    }

    public void alterar(Quarto quarto) {
        String sql = "UPDATE quartos SET numero = ?, tipo = ?, status = ? WHERE id = ?";
        try {
            super.executarUpdate(sql, quarto.getNumero(), quarto.getTipo(),
                    quarto.getStatus(), quarto.getId());
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao alterar quarto.", e);
        }
    }

    public void deletar(Long id) {
        try {
            super.executarUpdate("DELETE FROM quartos WHERE id = ?", id);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir quarto.", e);
        }
    }

    private List<Quarto> consultarLista(String sql, Object... parametros) {
        List<Quarto> lista = new ArrayList<>();
        try (ResultSet rs = super.executar(sql, parametros)) {
            while (rs.next()) {
                lista.add(this.mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar quartos.", e);
        }
        return lista;
    }

    private Quarto consultarUm(String sql, Object... parametros) {
        try (ResultSet rs = super.executar(sql, parametros)) {
            if (rs.next()) {
                return this.mapear(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar quarto.", e);
        }
        return null;
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
