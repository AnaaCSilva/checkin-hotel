package br.com.mvc.dao;

import br.com.mvc.model.Hospede;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO = Data Access Object (acesso ao banco)
 *
 * Somente SQL e conversao ResultSet -> {@link Hospede}.
 */
public class HospedeDAO extends MysqlDAO {

    public HospedeDAO() {
        super();
    }

    public List<Hospede> listarTodos() {
        String sql = "SELECT id, nome, cpf, telefone, email FROM hospedes ORDER BY nome";
        List<Hospede> lista = new ArrayList<>();
        try (ResultSet rs = super.executar(sql)) {
            while (rs.next()) {
                lista.add(this.mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar hospedes.", e);
        }
        return lista;
    }

    public Hospede buscarPorId(Long id) {
        String sql = "SELECT id, nome, cpf, telefone, email FROM hospedes WHERE id = ?";
        try (ResultSet rs = super.executar(sql, id)) {
            if (rs.next()) {
                return this.mapear(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar por id.", e);
        }
        return null;
    }

    public Hospede buscarPorCpf(String cpf) {
        String sql = "SELECT id, nome, cpf, telefone, email FROM hospedes WHERE cpf = ?";
        try (ResultSet rs = super.executar(sql, cpf)) {
            if (rs.next()) {
                return this.mapear(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar por cpf.", e);
        }
        return null;
    }

    public void inserir(Hospede hospede) {
        String sql = "INSERT INTO hospedes (nome, cpf, telefone, email) VALUES (?, ?, ?, ?)";
        try {
            super.executarUpdate(
                    sql,
                    hospede.getNome(),
                    hospede.getCpf(),
                    hospede.getTelefone(),
                    hospede.getEmail());
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir.", e);
        }
    }

    public void alterar(Hospede hospede) {
        String sql = "UPDATE hospedes SET nome = ?, cpf = ?, telefone = ?, email = ? WHERE id = ?";
        try {
            super.executarUpdate(
                    sql,
                    hospede.getNome(),
                    hospede.getCpf(),
                    hospede.getTelefone(),
                    hospede.getEmail(),
                    hospede.getId());
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao alterar.", e);
        }
    }

    public void deletar(Long id) {
        String sql = "DELETE FROM hospedes WHERE id = ?";
        try {
            super.executarUpdate(sql, id);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar.", e);
        }
    }

    private Hospede mapear(ResultSet rs) throws SQLException {
        Hospede hospede = new Hospede();
        hospede.setId(rs.getLong("id"));
        hospede.setNome(rs.getString("nome"));
        hospede.setCpf(rs.getString("cpf"));
        hospede.setTelefone(rs.getString("telefone"));
        hospede.setEmail(rs.getString("email"));
        return hospede;
    }
}